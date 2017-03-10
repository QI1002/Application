package io.github.qi1002.ilearn.configuration;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import io.github.qi1002.ilearn.DatasetRecord;
import io.github.qi1002.ilearn.Helper;
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
        final int id = View.generateViewId();
        DialogInterface.OnClickListener positiveListner =
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog dlgAlert = (AlertDialog) dialog;
                EditText input = (EditText)dlgAlert.findViewById(id);
                String importedfile = input.getText().toString();
                int index = importedfile.lastIndexOf("/");
                String filename = importedfile.substring(index + 1);
                String filepath = importedfile.substring(0, index);
                File file = new File(filepath, filename);
                if (file.exists() == true) {
                    ArrayList<DatasetRecord> result = DatasetRecord.parseECDICT(dlgAlert.getContext(), filepath, filename);
                    if (result != null) {
                        Toast.makeText(dlgAlert.getContext(), "Import Success", Toast.LENGTH_SHORT).show();
                        DatasetRecord.mergeDataset(DatasetRecord.getDataset(), result);
                    } else
                        Toast.makeText(dlgAlert.getContext(), "Import Fail", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(dlgAlert.getContext(), "Not found file", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //Helper.EditTextBox(this, "Enter Imported File", "/data/data/com.csst.ecdict/shared_prefs/history.xml");
        Helper.EditTextBox(this, "Enter Imported File", Environment.getExternalStorageDirectory() + "/history.xml", id, positiveListner);
    }
}
