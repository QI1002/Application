package io.github.qi1002.ilearn;

import android.webkit.WebView;

/**
 * Created by QI on 2017/2/19.
 */
public interface IDictionaryProvider {
    abstract public String getEntrance();
    abstract public String getWordMeanLink(String word);
    abstract public String getWordVoiceLink(String word);
    abstract public boolean matchWordMean(WebView view, String word);
}
