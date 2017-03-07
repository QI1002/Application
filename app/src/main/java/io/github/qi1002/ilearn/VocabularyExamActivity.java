package io.github.qi1002.ilearn;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.ActionBar;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class VocabularyExamActivity extends AppCompatActivity {

    private int test_count = 0;
    private int correct_count = 0;

    private String currentExam = "";
    private String current_mean = "";
    private WebView mWebView = null;
    private TextView mWordLabel = null;
    private EditText mWordAnswer = null;
    private Menu contextMenu = null;
    private Button nextButton = null;
    private IEnumerable datasetEnumerate = null;
    private ScoreRecord scoreData = null;
    private boolean bPlayVoiceDone = true;
    private boolean bLoadPageDone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_exam);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Vocabulary Exam");
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar ();
        actionbar.setDisplayHomeAsUpEnabled(true);

        nextButton = (Button) findViewById(R.id.voc_exam_next);
        nextButton.setText("(" + correct_count + "/" + test_count + "/" + getTestCount() + ") Next");
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                examWordCheck();
            }
        });

        mWebView = (WebView) findViewById(R.id.voc_exam_dataset_webview);
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
                Log.d("ExamInfo", "URL done " + url);

                if (getExamSpeak()) {
                    bPlayVoiceDone = false;
                    view.loadUrl("javascript:(function() {  app.voiceCheck(" + DatasetRecord.getDictionaryProvider().getWordVoiceCheck(currentExam) + ");  " +
                            DatasetRecord.getDictionaryProvider().getWordVoiceLink(currentExam) +
                            " app.voiceDone('" + currentExam + "' ); })()");
                }

                view.loadUrl("javascript:app.getHTMLSource" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', '" + currentExam + "');");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.endsWith(".mp3"))
                    Log.d("ExamInfo", "Resource done " + url);
            }
        });

        mWordAnswer = (EditText) findViewById(R.id.voc_exam_answer);
        mWordLabel = (TextView) findViewById(R.id.voc_exam_dataset);
        mWordLabel.setOnTouchListener(new SwipeTouchListener(this) {

            public void onSwipeLeft() {
                examWordCheck();
            }

            public void onClick() {
                examWordCheck();
            }
        });

        // default to set foucs to WebView
        focusWebView();

        // let apk use media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //do the first exam
        String datasetEnumerateWay = Helper.getPreferenceString(this, "vocabulary_exam_enumerate", "LookupCount");
        datasetEnumerate = DatasetRecord.getEnumerator(DatasetRecord.getDataset(), datasetEnumerateWay);
        examWord();

        //initialize
        scoreData = new ScoreRecord();
        scoreData.type = ScoreRecord.MEAN;
        scoreData.test_cnt = getTestCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vocabulary_exam, menu);
        contextMenu = menu;
        // update menu item "speak"
        updateSpeakOption();
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_return:
                finish();
                return true;
            case R.id.action_next:
                examWordCheck();
                return true;
            case R.id.action_speak:
                Helper.putPreferenceBoolean(this, "exam speak", !getExamSpeak());
                updateSpeakOption();
                return true;
            case R.id.action_show:
                if (!bLoadPageDone) {
                    Log.d("ExamInfo", "Show not yet");
                    Toast.makeText(this, "Load Page not done yet", Toast.LENGTH_SHORT).show();
                }else {
                    mWebView.setVisibility(View.VISIBLE);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void examWordCheck() {
        if (!bPlayVoiceDone || !bLoadPageDone) {
            Log.d("ExamInfo", "Next not yet");
            if (getExamSpeak())
                Toast.makeText(this, "Load Page or Play voice not done yet", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Load Page not done yet", Toast.LENGTH_SHORT).show();
        }else {
            examWord();
        }
    }

    private void examWord() {

        // check score
        if (test_count != 0) {
            if (Helper.isNullOrEmpty(current_mean)) {
                Toast.makeText(this, "The word mean is not got yet", Toast.LENGTH_SHORT).show();
                return;
            } else if ( Helper.isNullOrEmpty(mWordAnswer.getText().toString())) {
                Toast.makeText(this, "The word answer is not got yet", Toast.LENGTH_SHORT).show();
                return;
            } else {
                boolean correct = false;
                if (current_mean.contains(mWordAnswer.getText().toString())) {
                    correct_count++;
                    correct = true;
                }

                scoreData.applyResult(currentExam, test_count - 1, correct);
                nextButton.setText("(" + correct_count + "/" + test_count + "/" + getTestCount() + ") Next");
                mWordAnswer.setText("");
            }
        }

        if (test_count < getTestCount()) {
            current_mean = "";
            bLoadPageDone = false;
            DatasetRecord record = datasetEnumerate.getCurrent();
            datasetEnumerate.moveNext();
            mWebView.setVisibility(View.INVISIBLE);
            mWebView.loadUrl(DatasetRecord.getDictionaryProvider().getWordMeanLink(record.name));
            mWordLabel.setText("Exam : " + record.name);
            currentExam = record.name;
        }else
        {
            Helper.ExitBox(this, "the score is " + (correct_count * 100 / getTestCount()) + " (" + correct_count + "/" +
                    test_count + "/" + getTestCount() + ")\nand then exit");
            scoreData.updateRecord(this);
        }

        test_count++;
    }

    private void focusWebView() {
        mWebView.requestFocus();
    }

    public int getTestCount() {
        return getTestCount(this);
    }
    public static int getTestCount(Context context) {
        String count = Helper.getPreferenceString(context, "vocabulary_exam_count", "5");
        return Integer.valueOf(count);
    }

    public void setVoiceDone(boolean value) { bPlayVoiceDone = value; }

    public void setHTMLDone(String html, String word)
    {
        current_mean = DatasetRecord.getDictionaryProvider().getWordMean(this, html, word);
        if (DatasetRecord.getDictionaryProvider().isTranslate())
            current_mean = Translate.StoT(current_mean);
    }

    public boolean getExamSpeak()
    {
        return Helper.getPreferenceBoolean(this, "exam speak", false);
    }

    private void updateSpeakOption() {
        MenuItem speakItem = contextMenu.findItem(R.id.action_speak);
        speakItem.setCheckable(true);
        speakItem.setChecked(getExamSpeak());
        bPlayVoiceDone = true;
    }
}
