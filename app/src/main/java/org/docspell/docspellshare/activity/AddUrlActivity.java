package org.docspell.docspellshare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import org.docspell.docspellshare.R;
import org.docspell.docspellshare.util.Strings;
import org.docspell.docspellshare.data.UrlItem;

public class AddUrlActivity extends AppCompatActivity {
  private static final int GET_QR_CODE = 1;

  public static final String URL_ITEM_EXTRA = "extraUrlItem";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_url);

    Intent intent = getIntent();
    UrlItem item = (UrlItem) intent.getSerializableExtra(URL_ITEM_EXTRA);
    if (item != null) {
      EditText nameField = findViewById(R.id.urlNameField);
      nameField.setText(item.getName());
      nameField.setEnabled(false);
      EditText urlField = findViewById(R.id.urlValueField);
      urlField.setText(item.getUrl());
    }
  }

  public void addUrl(View view) {
    EditText nameField = findViewById(R.id.urlNameField);
    EditText urlField = findViewById(R.id.urlValueField);
    String name = nameField.getText().toString();
    String url = urlField.getText().toString();
    if (Strings.isNullOrBlank(name) || !Strings.isValidUrl(url)) {
      Snackbar.make(view, "A valid URL and name are required!", Snackbar.LENGTH_LONG)
          .setAction("Action", null)
          .show();
    } else {
      UrlItem urlItem = new UrlItem(name, url);
      Intent intent = new Intent();
      intent.putExtra(UrlItem.class.getName(), urlItem);
      setResult(RESULT_OK, intent);
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (data != null && requestCode == GET_QR_CODE && resultCode == RESULT_OK) {
      String url = data.getStringExtra(QrCodeActivity.QR_RESULT);
      EditText urlField = findViewById(R.id.urlValueField);
      urlField.setText(url);
    }
  }

  public void runQrCode(View view) {
    Intent intent = new Intent(this, QrCodeActivity.class);
    startActivityForResult(intent, GET_QR_CODE);
  }
}
