package com.zlsadesign.autogyro;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class OverlayPermissionsActivity extends AppCompatActivity {

  private int REQUEST_CODE = 1337;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkOverlayPermission();
    }

  }

  @TargetApi(Build.VERSION_CODES.M)
  private void checkOverlayPermission() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, REQUEST_CODE);
  }


  private void start() {
    Intent intent = new Intent(this, AutogyroService.class);
    startService(intent);
  }

  @Override
  @TargetApi(Build.VERSION_CODES.M)
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if(requestCode == REQUEST_CODE) {
      if(Settings.canDrawOverlays(this)) {
        start();
      } else {
        Toast.makeText(getBaseContext(), "You need to allow Autogyro to draw on top of all windows", Toast.LENGTH_LONG).show();
      }
    }

  }

}