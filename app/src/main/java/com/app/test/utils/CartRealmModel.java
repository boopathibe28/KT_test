package com.app.test.utils;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CartRealmModel extends RealmObject {
    @PrimaryKey
    private int id;

    private String emp_name;
    private String emp_id;
    private String emp_roll;
    private String emp_details;
    private String QTY;
    private String item_id;

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_roll() {
        return emp_roll;
    }

    public void setEmp_roll(String emp_roll) {
        this.emp_roll = emp_roll;
    }

    public String getEmp_details() {
        return emp_details;
    }

    public void setEmp_details(String emp_details) {
        this.emp_details = emp_details;
    }
}