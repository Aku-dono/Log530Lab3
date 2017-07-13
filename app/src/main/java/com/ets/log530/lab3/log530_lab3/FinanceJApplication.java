package com.ets.log530.lab3.log530_lab3;


import android.app.Application;

import com.ets.log530.lab3.log530_lab3.models.Account;
import com.ets.log530.lab3.log530_lab3.models.Category;
import com.ets.log530.lab3.log530_lab3.models.Ledger;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class FinanceJApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        //EVERYTHING BELOW IS TEMPORARY, REMOVE AFTERWARDS
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        try{
            Account testAccount = realm.createObject(Account.class, "Tests");
            testAccount.setDescription("For testing");

            Account testAccount2 = realm.createObject(Account.class, "Testing");
            testAccount2.setDescription("Still for testing");

            Category testCategory = realm.createObject(Category.class, "Business");
            testCategory.setDescription("Big business opportunities");
            testCategory.setBudget(1000);

            Category testCategory2 = realm.createObject(Category.class, "Development");
            testCategory2.setDescription("Making more money");
            testCategory2.setBudget(10000);

            Ledger entry1 = realm.createObject(Ledger.class, 1);
            entry1.setDescription("Money");
            entry1.setAccount(testAccount);
            entry1.setCategory(testCategory);
            entry1.setAmount(150);

            Ledger entry2 = realm.createObject(Ledger.class, 2);
            entry2.setDescription("More Money");
            entry2.setAccount(testAccount2);
            entry2.setCategory(testCategory);
            entry2.setAmount(200);

            Ledger entry3 = realm.createObject(Ledger.class, 3);
            entry3.setDescription("More Money");
            entry3.setAccount(testAccount2);
            entry3.setCategory(testCategory2);
            entry3.setAmount(250);

            realm.insertOrUpdate(testAccount);
            realm.insertOrUpdate(testAccount2);
            realm.insertOrUpdate(testCategory);
            realm.insertOrUpdate(testCategory2);
            realm.insertOrUpdate(entry1);
            realm.insertOrUpdate(entry2);
            realm.insertOrUpdate(entry3);

            realm.commitTransaction();
        } catch (RealmPrimaryKeyConstraintException e) {
            realm.cancelTransaction();
        } finally {
            realm.close();
        }
    }
}
