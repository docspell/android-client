package org.docspell.docspellshare.http;

import static org.docspell.docspellshare.util.Strings.requireNonEmpty;

import android.content.ContentResolver;
import android.net.Uri;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.docspell.docspellshare.data.Option;
import org.docspell.docspellshare.util.Strings;
import org.docspell.docspellshare.util.Uris;

public final class HttpRequest {
  private static final int CHUNK_SIZE = 16 * 1024;
  private static final OkHttpClient client =
      new OkHttpClient.Builder()
          .connectionSpecs(
              Arrays.asList(
                  ConnectionSpec.MODERN_TLS,
                  ConnectionSpec.COMPATIBLE_TLS,
                  ConnectionSpec.CLEARTEXT))
          .readTimeout(5, TimeUnit.MINUTES)
          .writeTimeout(5, TimeUnit.MINUTES)
          .socketFactory(new RestrictedSocketFactory(CHUNK_SIZE))
          .followRedirects(true)
          .followSslRedirects(true)
          .build();

  private final String url;
  private final List<DataPart> data;

  private HttpRequest(String url, List<DataPart> data) {
    this.url = requireNonEmpty(url, "url must be specified");
    this.data = data;
  }

  public int execute(ProgressListener progressListener) throws IOException {
    MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
    for (DataPart dp : data) {
      body.addFormDataPart("file", dp.getName(), createPartBody(dp, progressListener));
    }

    Request req = new Request.Builder().url(url).post(body.build()).build();
    Response response = client.newCall(req).execute();
    return response.code();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private final List<DataPart> parts = new ArrayList<>();
    private String url;

    public Builder addFile(ContentResolver resolver, Uri data, String fileName) {
      parts.add(
          new DataPart() {
            @Override
            public InputStream getData() throws IOException {
              return resolver.openInputStream(data);
            }

            @Override
            public String getName() {
              if (Strings.isNullOrBlank(fileName)) {
                return data.getLastPathSegment();
              } else {
                return fileName;
              }
            }

            @Override
            public Option<String> getType() {
              return Option.ofNullable(resolver.getType(data));
            }

            @Override
            public long getTotalSize() {
              return Uris.getFileSize(data, resolver);
            }
          });
      return this;
    }

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public HttpRequest build() {
      return new HttpRequest(url, parts);
    }
  }

  public interface DataPart {
    InputStream getData() throws IOException;

    String getName();

    Option<String> getType();

    /** Return -1, if unknown. */
    long getTotalSize();
  }

  private static RequestBody createPartBody(DataPart part, ProgressListener listener) {
    final String octetStream = "application/octet-stream";
    return new RequestBody() {
      @Override
      public MediaType contentType() {
        final MediaType mt = MediaType.parse(part.getType().orElse(octetStream));
        return mt != null ? mt : MediaType.get(octetStream);
      }

      @Override
      public long contentLength() {
        return part.getTotalSize();
      }

      @Override
      public void writeTo(@NonNull BufferedSink sink) throws IOException {
        try (InputStream in = part.getData();
             Source source = Okio.source(in)) {
          long total = 0;
          long read;
          while ((read = source.read(sink.getBuffer(), CHUNK_SIZE)) != -1) {
            total += read;
            sink.flush();
            listener.onProgress(part.getName(), total, part.getTotalSize());
          }
        }
      }
    };
  }
}
