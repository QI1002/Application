package io.github.qi1002.ilearn;

import android.content.Context;

/**
 * Created by QI on 2017/2/18.
 */
public class DataPassThread implements Runnable {

    protected Context inner_context;
    protected Object[] inner_arguments;

    public DataPassThread(Context context, Object[] arguments)
    {
        inner_context = context;
        inner_arguments = arguments;
    }

    @Override
    public void run() {
        try {
            // convert arguments to what you assign
            // do what you want
        } catch (Exception e) {
            Helper.GenericExceptionHandler(inner_context, e);
        }
    }
}
