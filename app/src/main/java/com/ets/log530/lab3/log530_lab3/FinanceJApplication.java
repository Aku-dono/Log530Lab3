package com.ets.log530.lab3.log530_lab3;


import android.app.Application;

import io.realm.Realm;

public class FinanceJApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
