package io.github.qi1002.ilearn;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // global settings
    public static boolean switchActivity = false;
    public static IEnumerable practiceEnumerate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isNetworkAvailable() == false)
            Helper.ExitBox(this, "No Network is available now, please connect network and startup this APP");

        Helper.initialPerferenceDefault(this);

        Button button;

        button = (Button) findViewById(R.id.bt_dictionary_lookup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(LookupDictionaryActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_dataset_practice);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(PracticeDatasetActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_exam_vocabulary);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(VocabularyExamActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_exam_pronunciation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(PronunciationExamActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_configuration);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(ConfigurationActivity.class);
            }
        });

        // let apk use media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // enable external storage
        if (verifyStoragePermissions() == false)
            dataLoading();

    }

    //persmission method.
    //more step in android studio 0) generate sdcard image file by mksdcard.exe by android sdk
    // 1) enable to use external SDcard in emulator.exe avd file by AVD manager (menu->tools->Android)
    // 2) add below flow to verifyStoragePermission of this activity (refer: http://stackoverflow.com/questions/33030933/android-6-0-open-failed-eacces-permission-denied)
    public boolean verifyStoragePermissions() {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            return true;
        }

        return false;
    }

    private void dataLoading()
    {
        // initialize dataset in default
        if (!DatasetRecord.isInitialized())
            DatasetRecord.initialDataset(this);

        // initialize scoreHistory  in default
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        ScoreRecord.initialScoreHistory(this, cal.get(Calendar.YEAR));
        ScoreRecord.updateDataset();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    dataLoading();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Helper.ExitBox(this, "Permission Rejected, leave the APP");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean isNetworkAvailable() {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void launchActivity(Class<?> cls) {
        if (DatasetRecord.isInitialized() || cls == ConfigurationActivity.class) {
            if (cls == PracticeDatasetActivity.class &&
                    DatasetRecord.getDataset().size() == 0) {
                Toast.makeText(this, " Zero items in dataset so no practice", Toast.LENGTH_SHORT).show();
            }else if (cls == VocabularyExamActivity.class &&
                    DatasetRecord.getDataset().size() < VocabularyExamActivity.getTestCount(this)) {
                Toast.makeText(this, " Too few items in dataset to exam", Toast.LENGTH_SHORT).show();
            } else if (cls == PronunciationExamActivity.class &&
                    DatasetRecord.getDataset().size() < PronunciationExamActivity.getTestCount(this)) {
                Toast.makeText(this, "Too few items in dataset to exam", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(this, cls);
                startActivity(intent);
                switchActivity = true;
            }
        } else {
            Helper.MessageBox(this, "dataset is not initialized yet");
        }
    }

    @Override
    public void onStop() {
        // wait  checkHTMLSource function (thread join to avoid lrge data.xml)
        // if we can check its really finalized not in the background , refer http://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html
        super.onStop();
        if (DatasetRecord.isDirty() && switchActivity == false) {
            DatasetRecord.writeDataset(this, DatasetRecord.output_filename);
            DatasetRecord.updateDataset(DatasetRecord.output_filename, DatasetRecord.dataset_filename);
            Log.d("LookupInfo", "write xml");
        }

        switchActivity = false;
    }
}


