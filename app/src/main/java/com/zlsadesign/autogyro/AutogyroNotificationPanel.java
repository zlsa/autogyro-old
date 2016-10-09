package com.zlsadesign.autogyro;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

public class AutogyroNotificationPanel {

  private int NOTIFICATION_ID = 2;

  private Context mContext;
  private NotificationManager nManager;
  private NotificationCompat.Builder nBuilder;
  private RemoteViews remoteView;

  public AutogyroNotificationPanel(Context parent) {
    mContext = parent;

    nBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(parent)
            .setContentTitle("Autogyro")
            .setSmallIcon(R.drawable.ic_notify)
            .setPriority(Notification.PRIORITY_MIN)
            .setOngoing(true);

    Intent close = new Intent(AutogyroBroadcastReceiver.INTENT_STOP);
    nBuilder.setDeleteIntent(PendingIntent.getBroadcast(mContext, 0, close, 0));

    remoteView = new RemoteViews(parent.getPackageName(), R.layout.notification_view);

    setListeners(remoteView);
    nBuilder.setContent(remoteView);

    nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
    nManager.notify(NOTIFICATION_ID, nBuilder.build());
  }

  public void setListeners(RemoteViews view){
    // flip

    Intent flip = new Intent("com.zlsadesign.autogyro.FLIP");
    view.setOnClickPendingIntent(R.id.flip, PendingIntent.getBroadcast(mContext, 0, flip, 0));

    // Rotate left

    Intent rotateLeft = new Intent("com.zlsadesign.autogyro.ROTATE_LEFT");
    view.setOnClickPendingIntent(R.id.rotate_left, PendingIntent.getBroadcast(mContext, 0, rotateLeft, 0));

    // Rotate right

    Intent rotateRight = new Intent("com.zlsadesign.autogyro.ROTATE_RIGHT");
    view.setOnClickPendingIntent(R.id.rotate_right, PendingIntent.getBroadcast(mContext, 0, rotateRight, 0));

    // Settings

    Intent settings = new Intent(mContext, SettingsActivity.class);
    view.setOnClickPendingIntent(R.id.settings, PendingIntent.getActivity(mContext, 0, settings, 0));

    // Close

    // Intent close = new Intent(AutogyroBroadcastReceiver.INTENT_STOP);
    // view.setOnClickPendingIntent(R.id.stop, PendingIntent.getBroadcast(mContext, 0, close, 0));
  }

  public void cancel() {
    nManager.cancelAll();
  }}
