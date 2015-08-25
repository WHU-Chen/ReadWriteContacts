package com.example.administrator.readwritecontacts;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/8/25.
 */
class ComparatorContact implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        Contact a= (Contact) o,b= (Contact) t1;
        int flag=a.getName().compareTo(b.getName());
        if(flag==0)return a.getNumber().compareTo(b.getNumber());
        return flag;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
