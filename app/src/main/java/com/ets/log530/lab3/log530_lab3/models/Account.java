package com.ets.log530.lab3.log530_lab3.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Account extends RealmObject {

    @PrimaryKey
    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
