package org.docspell.docspellshare.http;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;

import org.docspell.docspellshare.data.Option;

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
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import static org.docspell.docspellshare.util.Strings.requireNonEmpty;

public final class HttpRequest {
  private static final OkHttpClient client =
      new OkHttpClient.Builder()
          .connectionSpecs(
              Arrays.asList(
                  ConnectionSpec.MODERN_TLS,
                  ConnectionSpec.COMPATIBLE_TLS,
                  ConnectionSpec.CLEARTEXT))
          .readTimeout(5, TimeUnit.MINUTES)
          .writeTimeout(5, TimeUnit.MINUTES)
          .followRedirects(true)
          .followSslRedirects(true)
          .build();

  private final String url;
  private final List<DataPart> data;

  private HttpRequest(String url, List<DataPart> data) {
    this.url = requireNonEmpty(url, "url must be specified");
    this.data = data;
  }

  public void execute() throws IOException {
    MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
    for (DataPart dp : data) {
      body.addFormDataPart("file", dp.getName(), createPartBody(dp));
    }

    Request req = new Request.Builder().url(url).post(body.build()).build();
    client.newCall(req).execute();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private final List<DataPart> parts = new ArrayList<>();
    private String url;

    public Builder addFile(ContentResolver resolver, Uri data, String type) {
      parts.add(
          new DataPart() {
            @Override
            public InputStream getData() throws IOException {
              return resolver.openInputStream(data);
            }

            @Override
            public String getName() {
              return data.getLastPathSegment();
            }

            @Override
            public Option<String> getType() {
              return Option.of(type);
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
  }

  private static RequestBody createPartBody(DataPart part) {
    final String octetStream = "application/octet-stream";
    return new RequestBody() {
      @Override
      public MediaType contentType() {
        final MediaType mt = MediaType.parse(part.getType().orElse(octetStream));
        return mt != null ? mt : MediaType.get(octetStream);
      }

      @Override
      public void writeTo(@NonNull BufferedSink sink) throws IOException {
        try (InputStream in = part.getData();
            Source source = Okio.source(in)) {
          sink.writeAll(source);
        }
      }
    };
  }
}
