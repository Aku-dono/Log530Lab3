package com.ets.log530.lab3.log530_lab3.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ets.log530.lab3.log530_lab3.R;
import com.ets.log530.lab3.log530_lab3.models.Account;
import com.ets.log530.lab3.log530_lab3.models.Category;
import com.ets.log530.lab3.log530_lab3.models.Ledger;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class LedgerActivity extends AppCompatActivity {

    private List<Map<String, String>> transactions;
    private List<Ledger> rawTransactions;
    private SimpleAdapter transactionsAdaptor;

    private List<String> accountNames;
    private ArrayAdapter<String> accountAdapter;

    private List<String> categoriesNames;
    private ArrayAdapter<String> categoriesAdapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);

        ((Button)findViewById(R.id.ledger_addBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });
        ((Spinner)findViewById(R.id.ledger_accountDropdown)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Spinner)findViewById(R.id.ledger_categoryDropdown)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        this.realm = Realm.getDefaultInstance();

        initializeAccountsDropdown();
        initializeCategoriesDropdown();
        initializeTransactionsTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateTransactions();
        updateAccounts();
        updateCategories();
    }

    private void initializeAccountsDropdown()
    {
        Spinner accountSpinner = (Spinner)findViewById(R.id.ledger_accountDropdown);
        this.accountNames = new ArrayList<>();

        this.accountAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountNames);
        accountSpinner.setAdapter(this.accountAdapter);

        updateAccounts();
    }

    private void initializeCategoriesDropdown()
    {
        Spinner categorySpinner = (Spinner)findViewById(R.id.ledger_categoryDropdown);
        this.categoriesNames = new ArrayList<>();

        this.categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesNames);
        categorySpinner.setAdapter(this.categoriesAdapter);

        updateCategories();
    }

    private void initializeTransactionsTable()
    {
        this.transactions = new ArrayList<>();
        this.rawTransactions = new ArrayList<>();
        this.transactionsAdaptor = new SimpleAdapter(this, this.transactions, android.R.layout.simple_list_item_2,
                new String[] {"item", "subitem"}, new int[]{android.R.id.text1, android.R.id.text2});

        ListView table = (ListView)findViewById(R.id.ledger_transactionsList);
        table.setLongClickable(true);
        table.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeTransaction(position);
                return true;
            }
        });
        table.setAdapter(this.transactionsAdaptor);

        updateTransactions();
    }

    private void updateAccounts()
    {
        List<Account> accounts = this.realm.where(Account.class).findAll();
        this.accountNames.clear();
        for(Account account : accounts)
            this.accountNames.add(account.getName());

        this.accountAdapter.notifyDataSetChanged();
    }

    private void updateCategories()
    {
        List<Category> categories = this.realm.where(Category.class).findAll();
        this.categoriesNames.clear();
        for(Category category : categories)
            this.categoriesNames.add(category.getName());

        this.categoriesAdapter.notifyDataSetChanged();
    }

    private void updateTransactions()
    {
        this.transactions.clear();
        this.rawTransactions.clear();

        Object account = ((Spinner)findViewById(R.id.ledger_accountDropdown)).getSelectedItem();
        if(account != null)
        {
            String accountName = account.toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            List<Ledger> transactions = this.realm.where(Ledger.class).equalTo("account.name", accountName).findAll();
            for(Ledger ledger : transactions) {
                Map<String, String> listElement = new HashMap<>();

                String itemStr = dateFormat.format(ledger.getTdate()) + " - " + ledger.getDescription() + ": $" + ledger.getAmount();
                String subItemStr = ledger.getCategory().getName() + " - " + ledger.getPayee() + (ledger.isRec() ? " (REC)" : "");

                listElement.put("item", itemStr);
                listElement.put("subitem", subItemStr);

                this.transactions.add(listElement);
                this.rawTransactions.add(ledger);
            }
        }

        this.transactionsAdaptor.notifyDataSetChanged();
    }

    private void addTransaction()
    {
        String id = UUID.randomUUID().toString();
        String payee = ((EditText)findViewById(R.id.ledger_payeeTxt)).getText().toString();
        String description = ((EditText) findViewById(R.id.ledger_descriptionTxt)).getText().toString();
        float amount = Float.parseFloat(((EditText) findViewById(R.id.ledger_amountTxt)).getText().toString());
        Date date = Date.valueOf(((EditText) findViewById(R.id.ledger_dateTxt)).getText().toString());
        boolean rec = ((Switch) findViewById(R.id.ledger_rec)).isChecked();

        String accountName = ((Spinner)findViewById(R.id.ledger_accountDropdown)).getSelectedItem().toString();
        Account account = this.realm.where(Account.class).equalTo("name", accountName).findFirst();

        String categoryName = ((Spinner)findViewById(R.id.ledger_categoryDropdown)).getSelectedItem().toString();
        Category category = this.realm.where(Category.class).equalTo("name", categoryName).findFirst();

        Ledger transaction = new Ledger();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setPayee(payee);
        transaction.setDescription(description);
        transaction.setTdate(date);
        transaction.setRec(rec);
        transaction.setAccount(account);
        transaction.setCategory(category);

        this.realm.beginTransaction();
        try {
            this.realm.insertOrUpdate(transaction);

            this.realm.commitTransaction();

            Toast.makeText(getApplicationContext(), "Transaction added!",
                    Toast.LENGTH_LONG).show();
        } catch (RealmPrimaryKeyConstraintException e) {
            this.realm.cancelTransaction();

            Toast.makeText(getApplicationContext(), "Transaction failed!",
                    Toast.LENGTH_LONG).show();
        }

        updateTransactions();
    }

    private void removeTransaction(int position)
    {
        Ledger transaction = this.rawTransactions.get(position);

        this.realm.beginTransaction();
        try {
            transaction.deleteFromRealm();

            this.realm.commitTransaction();
        } catch (RealmPrimaryKeyConstraintException e) {
            this.realm.cancelTransaction();
        }

        Toast.makeText(getApplicationContext(), "Transaction deleted!",
                Toast.LENGTH_LONG).show();

        updateTransactions();
    }
}
