package org.docspell.docspellshare.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.docspell.docspellshare.R;
import org.docspell.docspellshare.data.UrlItem;
import org.docspell.docspellshare.http.HttpRequest;
import org.docspell.docspellshare.http.ProgressListener;
import org.docspell.docspellshare.http.UploadManager;
import org.docspell.docspellshare.util.DataStore;
import org.docspell.docspellshare.util.Strings;

import java.util.Collections;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

  private DataStore dataStore;
  private Handler handler = new Handler();

  private final ProgressListener progressListener =
      new ProgressListener() {
        @Override
        public void onProgress(String name, long bytesWritten, long total) {
          handler.post(
              () -> {
                TextView label = findViewById(R.id.uploadFileField);
                label.setText(getString(R.string.uploadingLabel, name));

                ProgressBar pb = findViewById(R.id.uploadProgress);
                if (total > 0) {
                  Log.w("share", String.format("Progress %d/%d", bytesWritten, total));
                  pb.setIndeterminate(false);
                  pb.setMax((int) total);
                  pb.setProgress((int) bytesWritten);
                } else {
                  Log.w("share", "No total size: " + total);
                  pb.setIndeterminate(true);
                }
              });
        }

        @Override
        public void onFinish(int code) {
          handler.post(() -> finish());
        }

        @Override
        public void onException(Exception error) {
          handler.post(
              () -> {
                TextView msg = findViewById(R.id.finishMessage);
                msg.setText(getString(R.string.uploadError, error.getMessage()));
              });
        }
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.dataStore = new DataStore(this);
    setContentView(R.layout.activity_share);
    UploadManager.getInstance().setProgress(progressListener);

    Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && Strings.notNullOrBlank(type)) {
      if ("text/plain".equals(type)) {
        Log.i("missing", "handling " + type + " not implemented");
      } else {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
          handleFiles(Collections.singletonList(uri));
        }
      }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        Log.w("missing", "handling multiple text/plain not implemented");
      } else {
        List<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileUris != null) {
          handleFiles(fileUris);
        }
      }
    } else {
      Log.i("missing", "handling action '" + action + "' / '" + type + "' not implemented");
    }
  }

  void handleFiles(List<Uri> uris) {
    String url = dataStore.getDefaultUrl().map(UrlItem::getUrl).orElse(null);
    if (url != null) {
      HttpRequest.Builder req = HttpRequest.newBuilder().setUrl(url);
      ContentResolver resolver = getContentResolver();
      for (Uri uri : uris) {
        req.addFile(resolver, uri);
      }
      UploadManager.getInstance().submit(req.build());
    }
  }
}
