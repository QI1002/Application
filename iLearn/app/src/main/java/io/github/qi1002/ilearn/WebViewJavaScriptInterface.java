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
    public void voiceCheck(int classCount, String word) {
        if (context instanceof LookupDictionaryActivity) {
            Log.d("LookupInfo", "Voice check " + classCount);
            LookupDictionaryActivity activity = (LookupDictionaryActivity)context;
            if (classCount == 0) {
                activity.setVoiceDone(true);
                Toast.makeText(context, word + "has no voice", Toast.LENGTH_SHORT).show();
            }
        }
        if (context instanceof PracticeDatasetActivity) {
            Log.d("PracticeInfo", "Voice check " + classCount);
            PracticeDatasetActivity activity = (PracticeDatasetActivity)context;
            if (classCount == 0) {
                activity.setVoiceDone(true);
                Toast.makeText(context, word + "has no voice", Toast.LENGTH_SHORT).show();
            }
        }
        if (context instanceof VocabularyExamActivity) {
            Log.d("ExamInfo", "Voice check " + classCount);
            VocabularyExamActivity activity = (VocabularyExamActivity)context;
            if (classCount == 0) {
                activity.setVoiceDone(true);
                Toast.makeText(context, word + "has no voice", Toast.LENGTH_SHORT).show();
            }
        }
        if (context instanceof PronunciationExamActivity) {
            Log.d("ExamInfo", "Voice check " + classCount);
            PronunciationExamActivity activity = (PronunciationExamActivity)context;
            if (classCount == 0) {
                activity.setVoiceDone(true);
                activity.skipTest();
                Toast.makeText(context, word + "has no voice", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void voiceDone(String message){

        String strValue = Helper.getPreferenceString(context, R.string.pref_key_behavior_wait_voice);
        int behavior_wait_voice = Integer.valueOf(strValue); // 1: always , 0: practice only, 2: never

        Object[] arguments = { message , behavior_wait_voice};
        Thread sleepThread = new Thread(new DataPassThread(context, arguments) {
            @Override
            public void run() {
                try {
                    assert((inner_arguments != null) && (inner_arguments.length == 2));
                    String message = (String)inner_arguments[0];
                    int wait_voice = (int)inner_arguments[1];
                    Thread.sleep(waitVoicePlayTime);

                    if (context instanceof LookupDictionaryActivity) {
                        LookupDictionaryActivity activity = (LookupDictionaryActivity)context;
                        if (wait_voice == 1) activity.setVoiceDone(true);
                        Log.d("LookupInfo", "Voice play done " + message);
                    }

                    if (context instanceof PracticeDatasetActivity) {
                        PracticeDatasetActivity activity = (PracticeDatasetActivity)context;
                        if (wait_voice != -1) activity.setVoiceDone(true);
                        Log.d("PracticeInfo", "Voice play done " + message);
                    }

                    if (context instanceof VocabularyExamActivity) {
                        VocabularyExamActivity activity = (VocabularyExamActivity)context;
                        if (wait_voice == 1) activity.setVoiceDone(true);
                        Log.d("ExamInfo", "Voice play done " + message);
                    }

                    if (context instanceof PronunciationExamActivity) {
                        PronunciationExamActivity activity = (PronunciationExamActivity)context;
                        activity.setVoiceDone(true);
                        Log.d("ExamInfo", "Voice play done " + message);
                    }

                } catch (Exception e) {
                    Helper.GenericExceptionHandler(inner_context, e);
                }
            }
        });

        sleepThread.start();

        if (context instanceof LookupDictionaryActivity) {
            LookupDictionaryActivity activity = (LookupDictionaryActivity)context;
            if (behavior_wait_voice != 1) activity.setVoiceDone(true);
            Log.d("LookupInfo", "Voice done " + message);
        }

        if (context instanceof PracticeDatasetActivity) {
            PracticeDatasetActivity activity = (PracticeDatasetActivity)context;
            if (behavior_wait_voice == -1) activity.setVoiceDone(true);
            Log.d("PracticeInfo", "Voice done " + message);
        }

        if (context instanceof VocabularyExamActivity) {
            VocabularyExamActivity activity = (VocabularyExamActivity)context;
            if (behavior_wait_voice != 1) activity.setVoiceDone(true);
            Log.d("ExamInfo", "Voice done " + message);
        }

        if (context instanceof PronunciationExamActivity) {
            Log.d("ExamInfo", "Voice done " + message);
        }
    }

    @JavascriptInterface
    public void getHTMLSource(String html, String word) {

        if (context instanceof PracticeDatasetActivity) {
            PracticeDatasetActivity activity = (PracticeDatasetActivity)context;
            activity.setHTMLDone(html, word);
        }

        if (context instanceof VocabularyExamActivity) {
            VocabularyExamActivity activity = (VocabularyExamActivity)context;
            activity.setHTMLDone(html, word);
        }
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
