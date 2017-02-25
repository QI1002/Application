package io.github.qi1002.ilearn;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by QI on 2017/2/12.
 */
public class Helper {

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    //more step in android studio 0) generate sdcard image file by mksdcard.exe by android sdk
    // 1) enable to use external SDcard in emulator.exe avd file by AVD manager (menu->tools->Android)
    // 2) add below flow to verifyStoragePermission of this activity (refer: http://stackoverflow.com/questions/33030933/android-6-0-open-failed-eacces-permission-denied)
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void InformationBox(final Context context, final String title, String information) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.information_box, null);

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setView(view);
        dlgAlert.setTitle(title);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });

        TextView textView = (TextView) (view.findViewById(R.id.textView1));
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(information);
        dlgAlert.create().show();
    }

    public static void MessageBox(Context context, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("MessageBox");
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });

        dlgAlert.create().show();
    }

    public static void ExitBox(final Context context, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("MessageBox");
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                    }
                });

        dlgAlert.create().show();
    }

    public static void GenericExceptionHandler(Context context, Exception exception)
    {
        Log.d("GenericExceptionHandler", exception.getLocalizedMessage());
        Log.d("GenericExceptionHandler", Log.getStackTraceString(exception));
        Helper.MessageBox(context, exception.getMessage());
    }
}
