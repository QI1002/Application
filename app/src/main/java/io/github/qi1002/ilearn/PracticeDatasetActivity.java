package io.github.qi1002.ilearn;

import android.media.AudioManager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PracticeDatasetActivity extends AppCompatActivity {

    private String currentPractice = "";
    private WebView mWebView = null;
    private TextView mWordLabel = null;
    private Menu contextMenu = null;
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

        Button nextButton = (Button) findViewById(R.id.practice_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                practiceWordCheck();
            }
        });

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
                view.loadUrl("javascript:(function() { " +
                             DatasetRecord.getDictionaryProvider().getWordVoiceLink(currentPractice) +
                             " app.voiceDone('" + currentPractice + "' ); })()");
                view.loadUrl("javascript:app.getHTMLSource" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', '" + currentPractice + "');");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.endsWith(".mp3"))
                    Log.d("PracticeInfo", "Resource done " + url);
            }
        });


        mWordLabel = (TextView) findViewById(R.id.practice_dataset);
        mWordLabel.setOnTouchListener(new SwipeTouchListener(this) {

            public void onSwipeRight() {
                practiceWordCheck();
            }

            public void onClick() {
                practiceWordCheck();
            }
        });

        // default to set foucs to WebView
        focusWebView();

        // let apk use media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //do the first practice
        datasetEnumerate = DatasetRecord.getEnumerator(DatasetRecord.getDataset(), datasetEnumerateWay);
        practiceWord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice_dataset, menu);
        contextMenu = menu;
        // update menu item "mean"
        updateMeanOption();
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
                practiceWordCheck();
                return true;
            case R.id.action_mean:
                MainActivity.practiceMean = !MainActivity.practiceMean;
                updateMeanOption();
                return true;
            case R.id.action_show:
                if (!bLoadPageDone) {
                    Log.d("PracticeInfo", "Show not yet");
                    Toast.makeText(this, "Load Page not done yet", Toast.LENGTH_SHORT).show();
                }else {
                    mWebView.setVisibility(View.VISIBLE);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void practiceWordCheck() {
        if (!bPlayVoiceDone || !bLoadPageDone) {
            Log.d("PracticeInfo", "Next not yet");
            Toast.makeText(this, "Load Page or Play voice not done yet", Toast.LENGTH_SHORT).show();
        }else {
            practiceWord();
        }
    }

    private void practiceWord() {

        bLoadPageDone = false;
        DatasetRecord record = datasetEnumerate.getCurrent();
        datasetEnumerate.moveNext();
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.loadUrl(DatasetRecord.getDictionaryProvider().getWordMeanLink(record.name));
        mWordLabel.setText("Practice: " + record.name);
        currentPractice = record.name;
    }

    private void focusWebView() {
        mWebView.requestFocus();
    }

    public void setVoiceDone(boolean value) { bPlayVoiceDone = value; }

    public void setHTMLDone(String html, String word)
    {
        if (MainActivity.practiceMean) {
            String mean = DatasetRecord.getDictionaryProvider().getWordMean(this, html, word);
            Helper.InformationBox(this, "Extracted Meaning", mean);
        }
    }

    private void updateMeanOption() {
        MenuItem meanItem = contextMenu.findItem(R.id.action_mean);
        meanItem.setCheckable(true);
        meanItem.setChecked(MainActivity.practiceMean);
    }
}
