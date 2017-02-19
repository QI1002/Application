package io.github.qi1002.ilearn;

import android.content.Context;

/**
 * Created by QI on 2017/2/19.
 */
public interface IDictionaryProvider {
    abstract public String getEntrance();
    abstract public String getWordMeanLink(String word);
    abstract public String getWordVoiceLink(String word);
    abstract public String getWordMean(Context context, String html, String word);
}
