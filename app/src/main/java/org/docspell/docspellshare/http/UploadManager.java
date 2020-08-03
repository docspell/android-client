package org.docspell.docspellshare.http;

import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public final class UploadManager {

  private static final UploadManager INSTANCE = new UploadManager();
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final AtomicReference<ProgressListener> progress =
      new AtomicReference<>(ProgressListener.NONE);

  private UploadManager() {}

  public static UploadManager getInstance() {
    return INSTANCE;
  }

  public void setProgress(ProgressListener listener) {
    if (listener != null) {
      this.progress.set(listener);
    } else {
      this.progress.set(ProgressListener.NONE);
    }
  }

  public void submit(HttpRequest request) {
    throw new UnsupportedOperationException("not implemented");
  }

  static class UploadWorker implements Runnable {
    @Override
    public void run() {
      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }
  }

  public interface ProgressListener {
    ProgressListener NONE = (name, progress) -> {};

    void onProgress(String name, int progress);
  }
}
