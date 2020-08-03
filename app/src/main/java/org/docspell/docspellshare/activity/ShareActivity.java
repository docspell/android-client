package org.docspell.docspellshare.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.docspell.docspellshare.R;
import org.docspell.docspellshare.data.UrlItem;
import org.docspell.docspellshare.http.HttpRequest;
import org.docspell.docspellshare.http.UploadManager;
import org.docspell.docspellshare.util.DataStore;
import org.docspell.docspellshare.util.Strings;

import java.util.Collections;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

  private DataStore dataStore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.dataStore = new DataStore(this);
    setContentView(R.layout.activity_share);
    UploadManager.getInstance()
        .setProgress(
            (name, progress) -> {
              TextView label = findViewById(R.id.uploadFileField);
              label.post(
                  () -> {
                    label.setText(name);
                  });
              ProgressBar pb = findViewById(R.id.uploadProgress);
              pb.post(
                  () -> {
                    pb.setProgress(progress);
                  });
            });

    Intent intent = getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && Strings.notNullOrBlank(type)) {
      if ("text/plain".equals(type)) {
        Log.i("missing", "handling " + type + " not implemented");
      } else {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
          handleFiles(Collections.singletonList(uri), type);
        }
      }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        Log.w("missing", "handling multiple text/plain not implemented");
      } else {
        List<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileUris != null) {
          handleFiles(fileUris, type);
        }
      }
    } else {
      Log.i("missing", "handling action '" + action + "' / '" + type + "' not implemented");
    }
  }

  void handleFiles(List<Uri> uris, String type) {
    String url = dataStore.getDefaultUrl().map(UrlItem::getUrl).orElse(null);
    if (url != null) {
      HttpRequest.Builder req = HttpRequest.newBuilder().setUrl(url);
      ContentResolver resolver = getContentResolver();
      for (Uri uri : uris) {
        req.addFile(resolver, uri, type);
      }
      UploadManager.getInstance().submit(req.build());
    }
  }
}
