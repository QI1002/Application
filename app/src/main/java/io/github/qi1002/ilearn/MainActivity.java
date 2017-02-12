package io.github.qi1002.ilearn;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtLoopupDictionary;
    private Button mBtPracticeDataset;
    private Button mBtVocabularyExam;
    private Button mBtPronunciationExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtLoopupDictionary = (Button) findViewById(R.id.bt_dictionary_lookup);
        mBtLoopupDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(LookupDictionaryActivity.class);
            }
        });

        mBtPracticeDataset = (Button) findViewById(R.id.bt_dataset_practice);
        mBtPracticeDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(PracticeDatasetActivity.class);
            }
        });

        mBtVocabularyExam = (Button) findViewById(R.id.bt_exam_vocabulary);
        mBtVocabularyExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(VocabularyExamActivity.class);
            }
        });

        mBtPronunciationExam = (Button) findViewById(R.id.bt_exam_pronunciation);
        mBtPronunciationExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(PronunciationExamActivity.class);
            }
        });

        // enable external storage
        Helper.verifyStoragePermissions(this);
        // read dataset in default
        DatasetRecord.parseDataset(this);
    }

    private void launchActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}


