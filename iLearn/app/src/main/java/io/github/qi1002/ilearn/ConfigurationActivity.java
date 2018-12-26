package io.github.qi1002.ilearn;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import io.github.qi1002.ilearn.configuration.ConfigurationAboutActivity;
import io.github.qi1002.ilearn.configuration.ConfigurationSettingActivity;
import io.github.qi1002.ilearn.configuration.ConfigurationScoreActivity;
import io.github.qi1002.ilearn.configuration.ConfigurationUtilityActivity;

public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Configuration");

        Button button;

        button = (Button) findViewById(R.id.bt_configuration_about);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(ConfigurationAboutActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_configuration_settings);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(ConfigurationSettingActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_configuration_score);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(ConfigurationScoreActivity.class);
            }
        });

        button = (Button) findViewById(R.id.bt_configuration_utility);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(ConfigurationUtilityActivity.class);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
