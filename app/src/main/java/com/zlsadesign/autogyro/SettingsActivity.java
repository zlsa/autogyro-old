package com.zlsadesign.autogyro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

  private String LOG = "com.zlsadesign.autogyro.SettingsActivity";

  private TextView toggleText = null;
  private Switch toggleSwitch = null;
  private View toggleBar = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    initToolbar();
    initToggleBar();

    if(isRunning()) {
      start();
    } else {
      stop();
    }

    getFragmentManager().beginTransaction().replace(R.id.fragment_frame, new MyPreferenceFragment()).commit();
  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private void initToggleBar() {
    toggleText = (TextView) findViewById(R.id.toggle_text);
    toggleSwitch = (Switch) findViewById(R.id.toggle_switch);
    toggleBar = (View) findViewById(R.id.toggle_bar);

    updateRunning();

    toggleBar.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        toggle();
      }
    });

  }

  public void toggle() {
    if(isRunning())
      stop();
    else
      start();
  }

  public void start() {
    toggleText.setText(getString(R.string.toggle_on));
    toggleSwitch.setChecked(true);
    sendBroadcast(new Intent(AutogyroBroadcastReceiver.INTENT_START));
  }

  private void stop() {
    toggleText.setText(getString(R.string.toggle_off));
    toggleSwitch.setChecked(false);
    sendBroadcast(new Intent(AutogyroBroadcastReceiver.INTENT_STOP));
  }

  private boolean isRunning() {
    return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_running", false);
  }

  private void updateRunning() {
    if(toggleSwitch == null) return;

    toggleSwitch.setChecked(isRunning());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.settings_menu, menu);

    Drawable drawable = menu.findItem(R.id.open_help).getIcon();
    drawable = DrawableCompat.wrap(drawable);
    DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.white));
    menu.findItem(R.id.open_help).setIcon(drawable);

    return super.onCreateOptionsMenu(menu);
  }

  private void openHelp() {
    DialogFragment newFragment = new AboutDialogFragment();
    newFragment.show(getSupportFragmentManager(), "about");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.open_help:
        openHelp();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public static class MyPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences);
    }

  }

}
