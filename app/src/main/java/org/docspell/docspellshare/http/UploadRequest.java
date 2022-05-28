package org.docspell.docspellshare.http;

import static org.docspell.docspellshare.util.Strings.requireNonEmpty;

import android.content.ContentResolver;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.docspell.docspellshare.data.Option;
import org.docspell.docspellshare.util.Strings;
import org.docspell.docspellshare.util.Uris;
import org.json.JSONException;
import org.json.JSONObject;

public final class UploadRequest {
  private static final MediaType json = MediaType.Companion.get("application/json");

  private final String url;
  private final List<Client.DataPart> data;

  private UploadRequest(String url, List<Client.DataPart> data) {
    this.url = requireNonEmpty(url, "url must be specified");
    this.data = data;
  }

  public Response execute(ProgressListener progressListener) throws IOException, JSONException {
    MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);
    // see https://docspell.org/docs/api/upload/#metadata
    JSONObject meta = new JSONObject();
    meta.put("multiple", true);
    meta.put("skipDuplicates", true);
    body.addFormDataPart("meta", "meta.json", RequestBody.create(meta.toString(), json));

    // adding all files
    for (Client.DataPart dp : data) {
      body.addFormDataPart("file", dp.getName(), Client.createPartBody(dp, progressListener));
    }

    Request req = new Request.Builder().url(url).post(body.build()).build();
    return Client.get().newCall(req).execute();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private final List<Client.DataPart> parts = new ArrayList<>();
    private String url;

    public Builder addFile(ContentResolver resolver, Uri data, String fileName) {
      parts.add(
          new Client.DataPart() {
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

    public UploadRequest build() {
      return new UploadRequest(url, parts);
    }
  }
}
