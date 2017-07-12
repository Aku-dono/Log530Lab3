package com.ets.log530.lab3.log530_lab3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private List<String> balances;
    private ArrayAdapter<String> balancesAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.getDefaultInstance();
        initializeBalanceTable();
    }

    private void initializeBalanceTable()
    {
        this.balances = new ArrayList<>();
        this.balancesAdaptor = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, balances);

        ListView table = (ListView)findViewById(R.id.accountBalanceTable);
        if(table != null)
        {
            table.setAdapter(this.balancesAdaptor);

            updateAccountBalances();
        }
    }

    private void updateAccountBalances()
    {
        this.balances.clear();

        for(int i = 0; i < 10; i++) {
            this.balances.add("Test #" + i);
        }
        //DO QUERY HERE

        this.balancesAdaptor.notifyDataSetChanged();
    }
}
