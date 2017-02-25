package io.github.qi1002.ilearn;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/*
 * JavaScript Interface. Web code can access methods in here
 * (as long as they have the @JavascriptInterface annotation)
 */
public class WebViewJavaScriptInterface{

    private Context context;
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

        final Object[] arguments = { message };
        Thread sleepThread = new Thread(new DataPassThread(context, arguments) {
            @Override
            public void run() {
                try {
                    assert((inner_arguments != null) && (inner_arguments.length == 1));
                    String message = (String)inner_arguments[0];
                    Thread.sleep(waitVoicePlayTime);
                    PracticeDatasetActivity activity = (PracticeDatasetActivity)inner_context;
                    activity.setVoiceDone(true);
                    Log.d("PracticeInfo", "Voice play done " + message);
                } catch (Exception e) {
                    Helper.GenericExceptionHandler(inner_context, e);
                }
            }
        });

        sleepThread.start();
        Log.d("PracticeInfo", "Voice done " + message);
    }

    @JavascriptInterface
    public void getHTMLSource(String html, String word) {
        //Log.d("ExamInfo", "HTML " + word + " source:\n" + html);
        //Helper.InformationBox(context, "Extracted Meaning", html);
        String mean = DatasetRecord.getDictionaryProvider().getWordMean(context, html, word);
        Helper.InformationBox(context, "Extracted Meaning", mean);
    }

    @JavascriptInterface
    public void checkHTMLSource(String html, String word) {
        Log.d("LookupInfo", "checkHTMLSource " + word);
        if (!DatasetRecord.getDictionaryProvider().isInvalidLookup(html)) {
            DatasetRecord.updateRecord(word);
        }
        else {
            Toast.makeText(context, word + "is invalid word", Toast.LENGTH_SHORT).show();
            Log.d("LookupInfo", "checkHTMLSource " + word + " fail");
        }
    }
}
