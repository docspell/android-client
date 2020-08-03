package org.docspell.docspellshare.http;

public interface ProgressListener {
  ProgressListener NONE =
      new ProgressListener() {
        @Override
        public void onProgress(String name, long bytesWritten, long total) {}

        @Override
        public void onFinish(int code) {}

        @Override
        public void onException(Exception error) {}
      };

  void onProgress(String name, long bytesWritten, long total);

  void onFinish(int code);

  void onException(Exception error);
}
