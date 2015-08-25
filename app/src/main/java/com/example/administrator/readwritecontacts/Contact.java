package com.example.administrator.readwritecontacts;

/**
 * Created by Administrator on 2015/8/25.
 */
public class Contact {
    private String name;
    private String number;
    public boolean isChecked;
    public Contact(String name ,String number){
        this.name=name;
        this.number=number;
        isChecked=false;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
