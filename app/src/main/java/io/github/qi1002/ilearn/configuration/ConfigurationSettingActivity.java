package io.github.qi1002.ilearn.configuration;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.github.qi1002.ilearn.R;

public class ConfigurationSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_setting);

        Button button = (Button) findViewById(R.id.bt_go_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // let apk use media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
