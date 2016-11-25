package com.example.pie.hellospen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by pie on 16. 11. 22.
 */

class Utils {

    public static void showAlertDialog(final Activity activity, final String msg, final boolean closeActivity) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        dlg.setIcon(activity.getResources().getDrawable(
                android.R.drawable.ic_dialog_alert));
        dlg.setTitle("Upgrade Notification")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                // Go to the market website and install/update APK.
                                Uri uri = Uri.parse("market://details?id=" + Spen.getSpenPackageName());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(intent);

                                dialog.dismiss();
                                activity.finish();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                if(closeActivity == true) {
                                    // Terminate the activity if APK is not installed.
                                    activity.finish();
                                }
                                dialog.dismiss();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(closeActivity == true) {
                            // Terminate the activity if APK is not installed.
                            activity.finish();
                        }
                    }
                }).show();
        dlg = null;
    }

    public static boolean processUnsupportedException(final Activity activity,SsdkUnsupportedException e) {

        e.printStackTrace();
        int errType = e.getType();
        // If the device is not a Samsung device or if the device does not support Pen.
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            Toast.makeText(activity, "This device does not support Spen.",
                    Toast.LENGTH_SHORT).show();
            activity.finish();
        }
        else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            // If SpenSDK APK is not installed.
            showAlertDialog( activity,
                    "You need to install additional Spen software"
                            +" to use this application."
                            + "You will be taken to the installation screen."
                            + "Restart this application after the software has been installed."
                    , true);
        } else if (errType
                == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            // SpenSDK APK must be updated.
            showAlertDialog( activity,
                    "You need to update your Spen software "
                            + "to use this application."
                            + " You will be taken to the installation screen."
                            + " Restart this application after the software has been updated."
                    , true);
        } else if (errType
                == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            // Update of SpenSDK APK to an available new version is recommended.
            showAlertDialog( activity,
                    "We recommend that you update your Spen software"
                            +" before using this application."
                            + " You will be taken to the installation screen."
                            + " Restart this application after the software has been updated."
                    , false);
            return false;
        }
        return true;
    }

    public static String getTimeFileName() {
        //**get the date, time for file name.
        String label ;
        Calendar calendar = new GregorianCalendar (  );
        int yy = calendar.get ( Calendar.YEAR );
        int mo = calendar.get ( Calendar.MONTH ) + 1;
        int dd = calendar.get ( Calendar.DAY_OF_MONTH );
        int hh = calendar.get ( Calendar.HOUR_OF_DAY );
        int mm = calendar.get ( Calendar.MINUTE );
        int miliSec = calendar.get ( Calendar.MILLISECOND );
        label = Integer.toString ( yy ) + "-" + Integer.toString ( mo ) + "-" + Integer.toString ( dd );
        label += "_" + Integer.toString ( hh ) + "_" + Integer.toString ( mm ) +
                "_" + Integer.toString ( miliSec );
        label += ".spd" ;

        return label ;
    }

    public static String[ ] setFileList(File dir, final Context context) {
        // Call the file list under the directory in dir.
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Toast.makeText(context, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        // Filter in spd and png files.
        File[] fileList = dir.listFiles ( new TxtFileFilter( ) ) ;
        if (fileList == null) {
            Toast.makeText(context, "File does not exist.", Toast.LENGTH_SHORT).show();
            return null;
        }

        int i = 0;
        String[] strFileList = new String[fileList.length];
        for (File file : fileList) {
            strFileList[i++] = file.getName();
        }

        return strFileList;
    }

    private static class TxtFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".spd") || name.endsWith(".png"));
        }
    }

}
