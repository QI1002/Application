package io.github.qi1002.ilearn.configuration;

import android.media.AudioManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import io.github.qi1002.ilearn.DatasetRecord;
import io.github.qi1002.ilearn.R;

public class ConfigurationUtilityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_utility);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.config_utility));

        Button button;
        button = (Button) findViewById(R.id.bt_ecdict_import);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importEcdict();
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

    public void importEcdict()
    {
        DatasetRecord.parseECDICT(this, "/data/data/com.csst.ecdict/shared_prefs" , "history.xml");
    }
}
