package com.zlsadesign.autogyro;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class AboutDialogFragment extends DialogFragment {

  private Context context;
  private String packageName;
  private String versionName;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    getPackageInfo();

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder
            .setTitle(getString(R.string.app_name) + " v" + versionName)
            .setMessage(R.string.app_description)
            .setNeutralButton(R.string.send_feedback, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                sendFeedbackEmail();
              }
            })
            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {

              }
            });

    return builder.create();
  }

  private void getPackageInfo() {
    context = getContext();

    packageName = context.getPackageName();

    try {
      versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
    } catch(PackageManager.NameNotFoundException e) {
      versionName = "v<?>";
    }
  }

  private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("*/*");
    intent.putExtra(Intent.EXTRA_EMAIL, "jonross.zlsa@gmail.com");
    intent.putExtra(Intent.EXTRA_SUBJECT, "Autogyro feedback (" + packageName + " v" + versionName + ")");

    final ComponentName componentName = intent.resolveActivity(context.getPackageManager());

    if(componentName != null) {
      try {
        startActivity(intent);
      } catch(ActivityNotFoundException ex) {
        Toast.makeText(context, R.string.toast_no_email, Toast.LENGTH_LONG).show();
      }
    }
  }

}
