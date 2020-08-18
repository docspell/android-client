package org.docspell.docspellshare.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import org.docspell.docspellshare.R;
import org.docspell.docspellshare.data.UrlItem;
import org.docspell.docspellshare.util.Strings;

public class AddUrlActivity extends AppCompatActivity {
  private static final int GET_QR_CODE = 1;
  private static final int CAMERA_PERM_REQUEST_CODE = 2;

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

  private void showCameraRequiredMessage() {
    View view = findViewById(R.id.addUrlLayout);
    Snackbar.make(view, "Camera permission is required to capture QR codes.", Snackbar.LENGTH_LONG)
        .setAction("Action", null)
        .show();
  }

  private void captureQRCodeActivity() {
    Intent intent = new Intent(this, QrCodeActivity.class);
    startActivityForResult(intent, GET_QR_CODE);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == CAMERA_PERM_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        captureQRCodeActivity();
      } else {
        showCameraRequiredMessage();
      }
    }
  }

  public void runQrCode(View view) {
    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      captureQRCodeActivity();
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_REQUEST_CODE);
      } else {
        showCameraRequiredMessage();
      }
    }
  }
}
