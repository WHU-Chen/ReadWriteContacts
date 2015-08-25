package com.example.administrator.readwritecontacts;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Administrator on 2015/8/25.
 */

//Learning
class ComparatorContact implements Comparator {

    @Override
    public int compare(Object o, Object t1) {
        Contact a= (Contact) o,b= (Contact) t1;
        int flag=chineseCompare(a.getName() ,b.getName());
        if(flag==0)return a.getNumber().compareTo(b.getNumber());
        return flag;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    //Learning
    private static int chineseCompare(Object _oChinese1, Object _oChinese2) {

        return Collator.getInstance(Locale.CHINESE).compare(_oChinese1,
                _oChinese2);
    }
}
