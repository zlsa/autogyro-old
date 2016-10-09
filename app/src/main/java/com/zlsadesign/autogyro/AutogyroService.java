package com.zlsadesign.autogyro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class AutogyroService extends Service {

  private String LOG = "com.zlsadesign.autogyro.AutogyroService";

  private boolean autoRotateEnabled = false;

  private boolean isCreated = false;

  private boolean swapPortraitLandscape = false;
  private boolean invertPortrait = false;
  private boolean invertLandscape = false;

  private SharedPreferences.OnSharedPreferenceChangeListener listener;

  private View view;
  private WindowManager.LayoutParams params;
  private WindowManager wm;

  private int orientation = 0;

  private AutogyroNotificationPanel notify;

  @Override
  public void onCreate() {

    updatePrefs();

    start();

    listenPrefs();
  }

  private void checkOverlayPermission() {
    if(!hasOverlayPermission()) {
      Log.w(LOG, "No overlay permission; requesting");
      getOverlayPermission();
      stopSelf();
    } else {
      start();
    }
  }

  private boolean hasOverlayPermission() {
    return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this));
  }

  private void updatePrefs() {
    Log.d(LOG, "updated prefs");

    orientation = getPrefs().getInt("orientation", 0);

    Log.d(LOG, "orientation: " + orientation);

    swapPortraitLandscape = getPrefs().getBoolean("swap_portrait_landscape", false);
    invertPortrait = getPrefs().getBoolean("invert_portrait", false);
    invertLandscape = getPrefs().getBoolean("invert_landscape", false);
  }

  private SharedPreferences getPrefs() {
    return PreferenceManager.getDefaultSharedPreferences(this);
  }

  private void listenPrefs() {
    listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefs();
        rotate(0);
      }
    };

    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(listener);
  }


  @Override
  public void onDestroy() {
    super.onDestroy();

    Log.d(LOG, "destroyed...");

    stop();
  }

  private void getOverlayPermission() {
    Log.w(LOG, "Asking for overlay permission...");
    Intent dialogIntent = new Intent(this, OverlayPermissionsActivity.class);
    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(dialogIntent);
  }

  private void start() {
    //autoRotateEnabled = getAutoRotateEnabled();
    //setAutoRotateEnabled(true);

    if(!hasOverlayPermission()) {
      getOverlayPermission();
    } else {
      createOverlay();
      createNotification();
      isCreated = true;
    }
  }

  private void stop() {
    setAutoRotateEnabled(autoRotateEnabled);

    removeOverlay();
    removeNotification();
    isCreated = false;

    stopSelf();
  }

  private int getOrientation(int difference) {
    int[] portrait = {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
    };

    int[] landscape = {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
    };


    int[] orientations = new int[4];

    int temp;

    if(invertPortrait) {
      temp = portrait[0];
      portrait[0] = portrait[1];
      portrait[1] = temp;
    }

    if(invertLandscape) {
      temp = landscape[0];
      landscape[0] = landscape[1];
      landscape[1] = temp;
    }

    if(swapPortraitLandscape) {
      orientations[0] = landscape[0];
      orientations[1] = portrait[1];
      orientations[2] = landscape[1];
      orientations[3] = portrait[0];
    } else {
      orientations[0] = portrait[0];
      orientations[1] = landscape[0];
      orientations[2] = portrait[1];
      orientations[3] = landscape[1];
    }

    orientation = ((((orientation + difference) % 4) + 4) % 4);

    Log.i(LOG, "rotation offset " + difference + ", absolute: " + orientation);

    return orientations[orientation];
  }

  public void setAutoRotateEnabled(boolean enabled) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) return;
    Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
  }

  public boolean getAutoRotateEnabled() {
    Log.d(LOG, "getting setting for accelerometer rotation!");

    try {
      return (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1);
    } catch(Settings.SettingNotFoundException e) {
      Log.d(LOG, "no setting for accelerometer rotation!");
      return false;
    }
  }

  private void setOrientation(int o) {
    checkOverlayPermission();

    if(view == null) {
      Log.d(LOG, "Starting service (no overlay exists)");
      start();
    }

    params.screenOrientation = o;

    wm.updateViewLayout(view, params);
  }

  private void rotate(int difference) {
    setOrientation(getOrientation(difference));

    SharedPreferences.Editor editor = getPrefs().edit();
    editor.putInt("orientation", orientation);
    editor.apply();
  }

  // Create the overlay

  private void createOverlay() {
    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    view = inflater.inflate(R.layout.activity_overlay, null);
    params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);

    params.gravity = Gravity.CENTER | Gravity.TOP;
    params.width = 0;
    params.height = 0;

    params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    wm = (WindowManager) getSystemService(WINDOW_SERVICE);
    wm.addView(view, params);
  }

  private void removeOverlay() {
    wm.removeView(view);
  }

  // Create the notification

  private void createNotification() {
    notify = new AutogyroNotificationPanel(this);
  }

  private void removeNotification() {
    notify.cancel();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if(!isCreated) {
      Log.w(LOG, "onStartCommand without having created anything yet");
      return START_STICKY;
    }

    if(intent != null) {
      rotate(intent.getIntExtra("rotate", 0));
    }

    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
