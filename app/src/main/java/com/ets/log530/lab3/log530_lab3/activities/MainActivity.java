package com.ets.log530.lab3.log530_lab3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ets.log530.lab3.log530_lab3.R;
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

        ((Button)findViewById(R.id.accountsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AccountsActivity.class));
            }
        });
        ((Button)findViewById(R.id.categoriesBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
            }
        });

        this.realm = Realm.getDefaultInstance();
        initializeBalanceTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAccountBalances();
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
