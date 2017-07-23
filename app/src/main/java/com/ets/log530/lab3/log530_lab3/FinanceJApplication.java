package com.ets.log530.lab3.log530_lab3;


import android.app.Application;

import com.ets.log530.lab3.log530_lab3.models.Account;
import com.ets.log530.lab3.log530_lab3.models.Category;
import com.ets.log530.lab3.log530_lab3.models.Ledger;

import java.sql.Date;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class FinanceJApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
