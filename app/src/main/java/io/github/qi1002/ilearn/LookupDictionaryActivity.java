package io.github.qi1002.ilearn;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LookupDictionaryActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private String currentLookup = "";
    private WebView mWebView = null;
    private EditText mLookupWord = null;
    private Menu contextMenu = null;
    private boolean bPlayVoiceDone = true;
    private boolean bLoadPageDone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_dictionary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lookup Dictionary");
        setSupportActionBar(toolbar);

        mWebView = (WebView) findViewById(R.id.lookup_dictionary_webview);
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
                Log.d("LookupInfo", "URL done " + url);
                if (!Helper.isNullOrEmpty(currentLookup) &&
                        url.compareTo(DatasetRecord.getDictionaryProvider().getEntrance()) != 0) {
                    if (getLookupSpeak()) {
                        bPlayVoiceDone = false;
                        view.loadUrl("javascript:(function() {  app.voiceCheck(" + DatasetRecord.getDictionaryProvider().getWordVoiceCheck(currentLookup) + ");  " +
                                DatasetRecord.getDictionaryProvider().getWordVoiceLink(currentLookup) +
                                " app.voiceDone('" + currentLookup + "' ); })()");
                    }
                    if (getSaveToXML()) {
                        view.loadUrl("javascript:app.checkHTMLSource" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>', '" + currentLookup + "');");
                    }
                }
            }


            @Override
            public void onLoadResource(WebView view, String url) {
                if (url.endsWith(".mp3"))
                    Log.d("LookupInfo", "Resource done " + url);
            }
        });

        //mWebView.loadUrl(DatasetRecord.getDictionaryProvider().getEntrance());

        mLookupWord = (EditText) findViewById(R.id.lookup_dictionary);
        mLookupWord.setOnEditorActionListener(this);
        mLookupWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // code to execute when EditText loses focus
                    hideKeyboard();
                }
            }
        });

        // default to set foucs to WebView
        focusWebView();

        // let apk use media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lookup_dictionary, menu);
        contextMenu = menu;
        // update menu item "speak"
        updateSpeakOption();
        // update menu item "save"
        updateSaveOption();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_return:
                finish();
                return true;
            case R.id.action_back:
                if (mWebView.canGoBack())
                    mWebView.goBack();
                return true;
            case R.id.action_next:
                if (mWebView.canGoForward())
                    mWebView.goForward();
                return true;
            case R.id.action_speak:
                Helper.putPreferenceBoolean(this, "lookup speak", !getLookupSpeak());
                updateSpeakOption();
                return true;
            case R.id.action_save:
                Helper.putPreferenceBoolean(this, "lookup save", !getSaveToXML());
                updateSaveOption();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            currentLookup = v.getText().toString();
            if (Helper.isNullOrEmpty(currentLookup))
                Toast.makeText(this, "Lookup word is empty", Toast.LENGTH_SHORT).show();
            else if (!bPlayVoiceDone || !bLoadPageDone) {
                Log.d("LookupInfo", "Next not yet");
                if (getLookupSpeak())
                    Toast.makeText(this, "Load Page or Play voice not done yet", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Load Page not done yet", Toast.LENGTH_SHORT).show();
            } else {
                bLoadPageDone = false;
                mWebView.loadUrl(DatasetRecord.getDictionaryProvider().getWordMeanLink(currentLookup));
                hideKeyboard();
                focusWebView();
            }
            return true; // consume.
        }

        return false; // pass on to other listener
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mLookupWord.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void focusWebView() {
        mWebView.requestFocus();
    }

    public void setVoiceDone(boolean value) { bPlayVoiceDone = value; }

    public boolean getSaveToXML()
    {
        return Helper.getPreferenceBoolean(this, "lookup save", true);
    }

    public boolean getLookupSpeak()
    {
        return Helper.getPreferenceBoolean(this, "lookup speak", false);
    }

    private void updateSpeakOption() {
        MenuItem speakItem = contextMenu.findItem(R.id.action_speak);
        speakItem.setCheckable(true);
        speakItem.setChecked(getLookupSpeak());
        bPlayVoiceDone = true;
    }

    private void updateSaveOption() {
        MenuItem saveItem = contextMenu.findItem(R.id.action_save);
        saveItem.setCheckable(true);
        saveItem.setChecked(getSaveToXML());
    }
}
