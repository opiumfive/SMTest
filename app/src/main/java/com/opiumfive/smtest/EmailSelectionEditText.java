package com.opiumfive.smtest;


import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


public class EmailSelectionEditText extends AppCompatEditText {

    private boolean isHelpEnabled;

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


}
