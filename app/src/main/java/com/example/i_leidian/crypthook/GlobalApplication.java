package com.example.i_leidian.crypthook;

import android.app.Application;

/**
 * Created by i-leidian on 2017/6/28.
 */

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static GlobalApplication getInstance() {

        return instance;
    }
}
