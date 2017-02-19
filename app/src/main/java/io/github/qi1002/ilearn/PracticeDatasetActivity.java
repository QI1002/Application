package io.github.qi1002.ilearn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.util.ArrayList;

public class PracticeDatasetActivity extends AppCompatActivity {

    private String currentPractice = "";
    private WebView mWebView = null;
    private TextView mWordLabel = null;
    private Menu contextMenu = null;
    private ArrayList<DatasetRecord> practice_dataset = null;
    private String datasetEnumerateWay = "Counter";
    private IEnumerable datasetEnumerate = null;
    private boolean bPlayVoiceDone = true;
    private boolean bLoadPageDone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_dataset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWebView = (WebView) findViewById(R.id.practice_dataset_webview);
        /// Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "app");
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient() {
            //refer: http://stackoverflow.com/questions/6199717/how-can-i-know-that-my-webview-is-loaded-100
            @Override
            public void onPageFinished(WebView view, String url) {
                bLoadPageDone = true;
                bPlayVoiceDone = false;
                Log.d("PracticeInfo", "URL done " + url);
                view.loadUrl(DatasetRecord.getTeachBase().getWordVoiceLink(currentPractice));
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.endsWith(".mp3"))
                    Log.d("PracticeInfo", "Resource done " + url);
            }
        });

        // get dataset to practice
        practice_dataset = DatasetRecord.getDataset();

        mWordLabel = (TextView) findViewById(R.id.practice_dataset);

        // default to set foucs to WebView
        focusWebView();
        //do the first practice
        datasetEnumerate = DatasetRecord.getEnumerator(practice_dataset, datasetEnumerateWay);
        practiceWord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice_dataset, menu);
        contextMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_return:
                finish();
                return true;
            case R.id.action_next:
                if (!bPlayVoiceDone || !bLoadPageDone) {
                    Log.d("PracticeInfo", "Next not yet");
                }else {
                    practiceWord();
                }
                return true;
            case R.id.action_show:
                if (!bLoadPageDone) {
                    Log.d("PracticeInfo", "Show not yet");
                }else {
                    mWebView.setVisibility(View.VISIBLE);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void practiceWord() {

        bLoadPageDone = false;
        DatasetRecord record = datasetEnumerate.getCurrent();
        datasetEnumerate.moveNext();
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.loadUrl(DatasetRecord.getTeachBase().getWordMeanLink(record.name));
        mWordLabel.setText("Practice data: " + record.name);
        currentPractice = record.name;
    }

    private void focusWebView() {
        mWebView.requestFocus();
    }
    public void setVoiceDone(boolean value) { bPlayVoiceDone = value; }
}
