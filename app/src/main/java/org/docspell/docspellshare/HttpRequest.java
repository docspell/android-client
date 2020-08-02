package org.docspell.docspellshare;

import android.content.ContentResolver;
import android.net.Uri;

public final class HttpRequest {
  private HttpRequest() {}

  public void execute() {}

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    public Builder addFile(ContentResolver resolver, Uri data) {
      return this;
    }

    public HttpRequest build() {
      return new HttpRequest();
    }
  }
}
