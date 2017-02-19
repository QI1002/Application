package io.github.qi1002.ilearn;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VocabularyExamActivity extends AppCompatActivity {

    private Button mBtGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_exam);

        mBtGoBack = (Button) findViewById(R.id.bt_go_back);
        mBtGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // let apk use media volume
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
