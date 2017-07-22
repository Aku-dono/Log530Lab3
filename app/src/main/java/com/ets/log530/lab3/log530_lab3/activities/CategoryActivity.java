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
import com.ets.log530.lab3.log530_lab3.models.Category;
import com.ets.log530.lab3.log530_lab3.models.Ledger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class CategoryActivity extends AppCompatActivity {

    private List<Map<String, String>> categories;
    private List<Category> rawCategories;
    private SimpleAdapter categoriesAdaptor;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        ((Button)findViewById(R.id.categories_addBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });


        this.realm = Realm.getDefaultInstance();
        initializeCategoriesTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCategories();
    }

    private void initializeCategoriesTable()
    {
        this.categories = new ArrayList<>();
        this.rawCategories = new ArrayList<>();
        this.categoriesAdaptor = new SimpleAdapter(this, this.categories, android.R.layout.simple_list_item_2,
                new String[] {"item", "subitem"}, new int[]{android.R.id.text1, android.R.id.text2});

        ListView table = (ListView)findViewById(R.id.categories_categoriesList);
        table.setLongClickable(true);
        table.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeCategory(position);
                return true;
            }
        });
        table.setAdapter(this.categoriesAdaptor);

        updateCategories();
    }

    private void updateCategories()
    {
        this.categories.clear();
        this.rawCategories.clear();

        List<Category> categories = this.realm.where(Category.class).findAll();
        for(Category category : categories) {
            Map<String, String> listElement = new HashMap<>();
            listElement.put("item", category.getName() + " ($" + category.getBudget() + ")");
            listElement.put("subitem", category.getDescription());

            this.categories.add(listElement);
            this.rawCategories.add(category);
        }

        this.categoriesAdaptor.notifyDataSetChanged();
    }

    private void addCategory()
    {
        String name = ((EditText)findViewById(R.id.categories_nameTxt)).getText().toString();
        String description = ((EditText) findViewById(R.id.categories_descriptionTxt)).getText().toString();
        float budget = Float.parseFloat(((EditText) findViewById(R.id.categories_budgetTxt)).getText().toString());

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setBudget(budget);

        this.realm.beginTransaction();
        try {
            this.realm.insertOrUpdate(category);

            this.realm.commitTransaction();
        } catch (RealmPrimaryKeyConstraintException e) {
            this.realm.cancelTransaction();
        }

        Toast.makeText(getApplicationContext(), "Category '" + name + "' added!",
                Toast.LENGTH_LONG).show();

        updateCategories();
    }

    private void removeCategory(int position)
    {
        Category category = this.rawCategories.get(position);
        String categoryName = category.getName();

        this.realm.beginTransaction();
        try {
            List<Ledger> ledgers = this.realm.where(Ledger.class).equalTo("category.name", categoryName).findAll();

            for (Ledger ledger : ledgers) {
                ledger.deleteFromRealm();
            }

            category.deleteFromRealm();

            this.realm.commitTransaction();
        } catch (RealmPrimaryKeyConstraintException e) {
            this.realm.cancelTransaction();
        }

        Toast.makeText(getApplicationContext(), "Category '" + categoryName + "' deleted!",
                Toast.LENGTH_LONG).show();

        updateCategories();
    }
}
