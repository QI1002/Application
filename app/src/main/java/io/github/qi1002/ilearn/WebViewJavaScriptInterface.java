package io.github.qi1002.ilearn;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/*
 * JavaScript Interface. Web code can access methods in here
 * (as long as they have the @JavascriptInterface annotation)
 */
public class WebViewJavaScriptInterface{

    private Context context;
    private String currentPractice = "";
    private final long waitVoicePlayTime = 3000;

    /*
         * Need a reference to the context in order to sent a post message
         */
    public WebViewJavaScriptInterface(Context context){
        this.context = context;
    }

    /*
         * This method can be called from Android. @JavascriptInterface
         * required after SDK version 17.
         */
    @JavascriptInterface
    public void voiceDone(String message){

        currentPractice = message;

        Thread sleepActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(waitVoicePlayTime);
                    PracticeDatasetActivity activity = (PracticeDatasetActivity)context;
                    activity.setVoiceDone(true);
                    Log.d("PracticeInfo", "Voice play done " + currentPractice);
                } catch (Exception e) {
                    Helper.MessageBox(context, e.getLocalizedMessage());;
                }
            }
        });

        sleepActivity.start();
        Log.d("PracticeInfo", "Voice done " + currentPractice);
    }
}
