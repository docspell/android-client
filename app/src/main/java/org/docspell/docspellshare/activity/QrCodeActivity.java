package org.docspell.docspellshare.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
  public static final String QR_RESULT = QrCodeActivity.class + ".QR_RESULT";
  private ZXingScannerView scannerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    scannerView = new ZXingScannerView(this);
    setContentView(scannerView);
  }

  @Override
  protected void onResume() {
    super.onResume();
    scannerView.setResultHandler(this);
    scannerView.startCamera();
  }

  @Override
  protected void onPause() {
    super.onPause();
    scannerView.stopCamera();
  }

  @Override
  public void handleResult(Result result) {
    String url = result.getText();
    Intent intent = new Intent();
    intent.putExtra(QR_RESULT, url);
    setResult(RESULT_OK, intent);
    finish();
  }
}
