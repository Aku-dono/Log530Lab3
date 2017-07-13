package com.ets.log530.lab3.log530_lab3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ets.log530.lab3.log530_lab3.models.Account;
import com.ets.log530.lab3.log530_lab3.models.Ledger;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private List<String> balances;
    private ArrayAdapter<String> balancesAdaptor;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.realm = Realm.getDefaultInstance();
        initializeBalanceTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.realm.close();
    }

    private void initializeBalanceTable()
    {
        this.balances = new ArrayList<>();
        this.balancesAdaptor = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, balances);

        ListView table = (ListView)findViewById(R.id.accountBalanceTable);
        table.setAdapter(this.balancesAdaptor);

        updateAccountBalances();
    }

    private void updateAccountBalances()
    {
        this.balances.clear();

        List<Account> accounts = this.realm.where(Account.class).findAll();
        for(Account account : accounts) {
            String accountName = account.getName();
            this.balances.add(accountName + ": $" + this.realm.where(Ledger.class).equalTo("account.name", accountName).sum("amount"));
        }
        this.balancesAdaptor.notifyDataSetChanged();
    }
}
