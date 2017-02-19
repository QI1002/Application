package io.github.qi1002.ilearn;

import android.webkit.WebView;

/**
 * Created by QI on 2017/2/19.
 */
public class IchachaProvider implements IDictionaryProvider {

    public String getEntrance()
    {
        return "http://tw.ichacha.net/m/";
    }

    public String getWordMeanLink(String word)
    {
        return "http://tw.ichacha.net/m/" + word + ".html";
    }

    public String getWordVoiceLink(String word)
    {
        return "javascript:(function() { document.getElementsByClassName('laba')[0].click(); app.voiceDone('" + word + "' ); })()";
    }

    public boolean matchWordMean(WebView view, String word)
    {
        return false;
    }
}
