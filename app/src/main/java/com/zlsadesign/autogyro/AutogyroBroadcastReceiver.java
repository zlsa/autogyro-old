package com.zlsadesign.autogyro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AutogyroBroadcastReceiver extends BroadcastReceiver {

  private String LOG = "com.zlsadesign.autogyro.AutogyroBroadcastReceiver";

  private SharedPreferences prefs = null;

  public static String INTENT_START = "com.zlsadesign.autogyro.START";
  public static String INTENT_STOP = "com.zlsadesign.autogyro.STOP";

  public static String INTENT_FLIP = "com.zlsadesign.autogyro.FLIP";
  public static String INTENT_ROTATE_LEFT = "com.zlsadesign.autogyro.ROTATE_LEFT";
  public static String INTENT_ROTATE_RIGHT = "com.zlsadesign.autogyro.ROTATE_RIGHT";

  private Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;

    Log.i(LOG, intent.getAction());

    String action = intent.getAction();

    if(action.equals("android.intent.action.BOOT_COMPLETED")) {
      onBoot();
    } else if(action.equals(INTENT_START)) {
      start();
    } else if(action.equals(INTENT_STOP)) {
      stop();
    } else if(action.equals(INTENT_FLIP)) {
      rotate(2);
    } else if(action.equals(INTENT_ROTATE_LEFT)) {
      rotate(-1);
    } else if(action.equals(INTENT_ROTATE_RIGHT)) {
      rotate(1);
    }

  }

  private SharedPreferences getPrefs() {
    if(prefs == null)
      prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    return prefs;
  }

  private SharedPreferences.Editor getPrefEditor() {
    return getPrefs().edit();
  }

  private void setIsRunning(boolean running) {
    SharedPreferences.Editor editor = getPrefEditor();
    editor.putBoolean("is_running", running);
    editor.apply();

    Log.i(LOG, "Saved running state: " + running);
  }

  private void start() {
    Log.i(LOG, "started");

    rotate(0);
    setIsRunning(true);
  }

  private void stop() {
    Log.i(LOG, "stopping");

    Intent intent = new Intent(mContext, AutogyroService.class);

    setIsRunning(false);
    mContext.stopService(intent);
  }

  private void onBoot() {
    boolean is_running = getPrefs().getBoolean("is_running", false);

    if(is_running) {
      start();
    }
  }

  private void rotate(int direction) {
    Intent serviceIntent = new Intent(mContext, AutogyroService.class);
    serviceIntent.putExtra("rotate", direction);
    mContext.startService(serviceIntent);
  }

}
