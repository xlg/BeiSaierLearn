package com.xlg.beisaierlearn;

/**
 * Created by xulinggang on 15/6/29.
 */
public class Log {

    public static void i(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.i(tag,msg);
        }
    }

}
