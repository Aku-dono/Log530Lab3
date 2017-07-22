package com.ets.log530.lab3.log530_lab3.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ets.log530.lab3.log530_lab3.R;
import com.ets.log530.lab3.log530_lab3.models.Account;
import com.ets.log530.lab3.log530_lab3.models.Ledger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class AccountsActivity extends AppCompatActivity {

    private List<Map<String, String>> accounts;
    private List<Account> rawAccounts;
    private SimpleAdapter accountsAdaptor;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        ((Button)findViewById(R.id.accounts_addBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount();
            }
        });


        this.realm = Realm.getDefaultInstance();
        initializeAccountsTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateAccounts();
    }

    private void initializeAccountsTable()
    {
        this.accounts = new ArrayList<>();
        this.rawAccounts = new ArrayList<>();
        this.accountsAdaptor = new SimpleAdapter(this, this.accounts, android.R.layout.simple_list_item_2,
                                                new String[] {"item", "subitem"}, new int[]{android.R.id.text1, android.R.id.text2});

        ListView table = (ListView)findViewById(R.id.accounts_accountsList);
        table.setLongClickable(true);
        table.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeAccount(position);
                return true;
            }
        });
        table.setAdapter(this.accountsAdaptor);

        updateAccounts();
    }

    private void updateAccounts()
    {
        this.accounts.clear();
        this.rawAccounts.clear();

        List<Account> accounts = this.realm.where(Account.class).findAll();
        for(Account account : accounts) {
            Map<String, String> listElement = new HashMap<>();
            listElement.put("item", account.getName());
            listElement.put("subitem", account.getDescription());

            this.accounts.add(listElement);
            this.rawAccounts.add(account);
        }

        this.accountsAdaptor.notifyDataSetChanged();
    }

    private void addAccount()
    {
        String name = ((EditText)findViewById(R.id.accounts_nameTxt)).getText().toString();
        String description = ((EditText) findViewById(R.id.accounts_descriptionTxt)).getText().toString();

        Account account = new Account();
        account.setName(name);
        account.setDescription(description);

        this.realm.beginTransaction();
        try {
            this.realm.insertOrUpdate(account);

            this.realm.commitTransaction();
        } catch (RealmPrimaryKeyConstraintException e) {
            this.realm.cancelTransaction();
        }

        Toast.makeText(getApplicationContext(), "Account '" + name + "' added!",
                Toast.LENGTH_LONG).show();

        updateAccounts();
    }

    private void removeAccount(int position)
    {
        Account account = this.rawAccounts.get(position);
        String accountName = account.getName();

        this.realm.beginTransaction();
        try {
            List<Ledger> ledgers = this.realm.where(Ledger.class).equalTo("account.name", accountName).findAll();

            for (Ledger ledger : ledgers) {
                ledger.deleteFromRealm();
            }

            account.deleteFromRealm();

            this.realm.commitTransaction();
        } catch (RealmPrimaryKeyConstraintException e) {
            this.realm.cancelTransaction();
        }

        Toast.makeText(getApplicationContext(), "Account '" + accountName + "' deleted!",
                Toast.LENGTH_LONG).show();

        updateAccounts();
    }
}
