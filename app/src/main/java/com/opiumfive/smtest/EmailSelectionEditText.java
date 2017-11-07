package com.opiumfive.smtest;


import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;


public class EmailSelectionEditText extends AppCompatEditText {

    private boolean isHelpEnabled;
    private List<Contact> contactList = new ArrayList<>();

    public EmailSelectionEditText(Context context) {
        super(context);
    }

    public EmailSelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHelpEnabled(boolean b) {
        isHelpEnabled = b;
    }

    public boolean isHelpEnabled() {
        return isHelpEnabled;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
