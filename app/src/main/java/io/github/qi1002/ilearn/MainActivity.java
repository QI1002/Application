package io.github.qi1002.ilearn;

import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // global settings
    public static boolean switchActivity = false;
    public static IEnumerable practiceEnumerate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // enable external storage
        Helper.verifyStoragePermissions(this);

        // initialize dataset in default
        if (!DatasetRecord.isInitialized())
            DatasetRecord.initialDataset(this);

        // initialize scoreHistory  in default
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        ScoreRecord.initialScoreHistory(this, cal.get(Calendar.YEAR));
        ScoreRecord.updateDataset();

        // let apk use media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void launchActivity(Class<?> cls) {
        if (DatasetRecord.isInitialized()) {
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


