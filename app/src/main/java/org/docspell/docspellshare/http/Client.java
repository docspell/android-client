package org.docspell.docspellshare.http;

import androidx.annotation.NonNull;

import org.docspell.docspellshare.data.Option;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public final class Client {
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
          .addInterceptor(new UserAgentInterceptor())
          .socketFactory(new RestrictedSocketFactory(CHUNK_SIZE))
          .followRedirects(true)
          .followSslRedirects(true)
          .build();

  public static OkHttpClient get() {
    return client;
  }

  public interface DataPart {
    InputStream getData() throws IOException;

    String getName();

    Option<String> getType();

    /** Return -1, if unknown. */
    long getTotalSize();
  }

  static RequestBody createPartBody(DataPart part, ProgressListener listener) {
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
