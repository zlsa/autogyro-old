package com.zlsadesign.autogyro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class DebugActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debug);

    initToolbar();
    initButton();
  }

  private void start() {
    Intent intent = new Intent(this, AutogyroService.class);
    startService(intent);
  }

  private void stop() {
    Intent intent = new Intent(this, AutogyroService.class);
    stopService(intent);
  }

  private void initToolbar() {
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
  }

  private void initButton() {
    Button button = (Button) findViewById(R.id.stop_service);
    assert button != null;
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        stop();
      }
    });

    button = (Button) findViewById(R.id.start_service);
    assert button != null;
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        start();
      }
    });
  }

}
