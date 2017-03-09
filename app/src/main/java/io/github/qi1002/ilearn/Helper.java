package io.github.qi1002.ilearn;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by QI on 2017/2/12.
 */
public class Helper {

    public static boolean isNullOrEmpty(String word)
    {
        return (word == null || word.length() == 0);
    }

    public static void InformationBox(Context context, String title, String information) {

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
                        dialog.dismiss();
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
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dlgAlert.create().show();
    }

    public static void SelectionBox(Context context, String message, String yes, String no, String title,
                                   DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setCancelable(false);

        if (positiveListener != null)
            dlgAlert.setPositiveButton(yes, positiveListener);
        else
            dlgAlert.setPositiveButton(yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        if (negativeListener != null)
            dlgAlert.setNegativeButton(no, negativeListener);
        else
            dlgAlert.setNegativeButton(no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        dlgAlert.create().show();
    }

    public static void ExitBox(final Context context, String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("ExitBox");
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity) context).finish();
                    }
                });

        dlgAlert.create().show();
    }

    public static void EditTextBox(final Context context, String message, final String defaultText) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setTitle("EditTextBox");
        dlgAlert.setMessage(message);

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(defaultText);
        dlgAlert.setView(input);

        dlgAlert.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String importedfile = input.getText().toString();
                        int index = importedfile.lastIndexOf("/");
                        String filename = importedfile.substring(index + 1);
                        String filepath = importedfile.substring(0, index);
                        File file = new File(filepath, filename);
                        if (file.exists() == true) {
                            boolean result = DatasetRecord.parseECDICT(context, filepath, filename);
                            if (result)
                                Toast.makeText(context, "Import Success", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "Import Fail", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Not found file", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        dlgAlert.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
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

    public static boolean getPreferenceBoolean(Context context, String prefString, boolean defaultValue)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(prefString, defaultValue);
    }

    public static void putPreferenceBoolean(Context context, String prefString, boolean value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(prefString, value);
        editor.commit(); // Commit the edits!
    }

    public static String getPreferenceString(Context context, String prefString, String defaultValue)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(prefString, defaultValue);
    }

    public static void putPreferenceString(Context context, String prefString, String value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(prefString, value);
        editor.commit(); // Commit the edits!
    }
}
