package io.github.qi1002.ilearn;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by QI on 2017/2/12.
 */
public class Helper {

    static final ArrayList<String> PerferenceLists = new ArrayList<String>(Arrays.asList(
            "pref_key_dataset_update", "pref_key_dataset_check", "pref_key_dataset_url_location","pref_key_behavior_wait_voice",
            "pref_key_lookup_save", "pref_key_lookup_speak",
            "pref_key_resume_practice", "pref_key_show_mean_dialog", "pref_key_practice_enumerate",
            "pref_key_exam_speak", "pref_key_vocabulary_exam_count", "pref_key_vocabulary_exam_enumerate",
            "pref_key_pronunciation_exam_count", "pref_key_pronunciation_exam_enumerate"
            ));

    static final Map<String , String> PrefenceDefaults = new HashMap<String , String>();

    public static String getStringByIdName(Context context, String name) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(name, "string", context.getPackageName());
        if (resId == 0)
            return name;
        else
            return context.getString(resId);
    }

    public static int getIdByName(Context context, String name) {
        Resources res = context.getResources();
        return res.getIdentifier(name, "string", context.getPackageName());
    }

    public static void initialPerferenceDefault(Context context)
    {
        for (int i = 0; i< PerferenceLists.size(); i++) {
            String Key = getStringByIdName(context, PerferenceLists.get(i));
            String defaultValue = getStringByIdName(context, PerferenceLists.get(i).replace("pref_key", "pref_default"));

            PrefenceDefaults.put(Key, defaultValue);
        }
    }

    public static void restorePerferenceDefault(Context context)
    {
        for (int i = 0; i< PerferenceLists.size(); i++) {
            int resId = getIdByName(context, PerferenceLists.get(i));
            String Key = getStringByIdName(context, PerferenceLists.get(i));
            String defaultValue = getStringByIdName(context, PerferenceLists.get(i).replace("pref_key", "pref_default"));
            if (defaultValue.compareTo("true") == 0 || defaultValue.compareTo("false") == 0)
                putPreferenceBoolean(context, resId, Boolean.valueOf(defaultValue));
            else
                putPreferenceString(context, resId, defaultValue);
        }
    }

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

    public static void EditTextBox(Context context, String message, String defaultText, int id, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setTitle("EditTextBox");
        dlgAlert.setMessage(message);

        EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(defaultText);
        input.setId(id);
        dlgAlert.setView(input);

        dlgAlert.setPositiveButton("YES", positiveListener);
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

    public static boolean getPreferenceBoolean(Context context, int resId)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String prefString = context.getString(resId);
        boolean defaultValue = Boolean.valueOf(PrefenceDefaults.get(prefString));
        return settings.getBoolean(prefString, defaultValue);
    }

    public static void putPreferenceBoolean(Context context, int resId, boolean value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        String prefString = context.getString(resId);
        editor.putBoolean(prefString, value);
        editor.commit(); // Commit the edits!
    }

    public static String getPreferenceString(Context context, int resId)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String prefString = context.getString(resId);
        String defaultValue = PrefenceDefaults.get(prefString);
        return settings.getString(prefString, defaultValue);
    }

    public static void putPreferenceString(Context context, int resId, String value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        String prefString = context.getString(resId);
        editor.putString(prefString, value);
        editor.commit(); // Commit the edits!
    }
}
