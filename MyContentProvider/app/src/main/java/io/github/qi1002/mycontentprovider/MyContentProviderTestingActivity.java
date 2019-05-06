package io.github.qi1002.mycontentprovider;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MyContentProviderTestingActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Cursor c = getContentResolver().query(
                Uri.parse("content://io.github.qi1002.mycontentprovider/MyTable1"),
                null,null,null,null ); // get all
        if( c != null ) {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                Log.e("CP", "MyTable1 field1:" + c.getString(0) + ", field2:" + c.getString(1));
                c.moveToNext();
            }
            c.close();

            c = getContentResolver().query(
                    Uri.parse("content://io.github.qi1002.mycontentprovider/MyTable2"),
                    null, null, null, null); // get all
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                Log.e("CP", "MyTable2 field1:" + c.getString(0) + ", field2:" + c.getString(1));
                c.moveToNext();
            }
            c.close();
        }else {
            Log.e("CP", "content://io.github.qi1002.mycontentprovider/MyTable1 fail");
        }
        // Example of a call to a native method
        // TextView tv = (TextView) findViewById(R.id.sample_text);
        // tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}