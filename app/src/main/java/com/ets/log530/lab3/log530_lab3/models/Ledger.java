package com.ets.log530.lab3.log530_lab3.models;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Ledger extends RealmObject{

    @PrimaryKey
    private int id;

    private Account account;
    private Category category;

    private boolean rec;
    private Date tdate;
    private String payee;
    private String description;
    private float amount;
}
