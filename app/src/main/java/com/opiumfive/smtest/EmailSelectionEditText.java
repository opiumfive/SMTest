package com.opiumfive.smtest;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.util.ArrayList;
import java.util.List;


public class EmailSelectionEditText extends AppCompatEditText implements AdapterView.OnItemClickListener {

    private boolean isHelpEnabled;
    private List<Contact> contactList = new ArrayList<>();
    private PopupWindow popupWindow;
    private ArrayAdapter<Contact> adapter;

    public EmailSelectionEditText(Context context) {
        super(context);
        init(context);
    }

    public EmailSelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        popupWindow = new PopupWindow(context);
        ListView listView = new ListView(context);
        listView.setBackgroundColor(Color.WHITE);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listView);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        adapter.notifyDataSetChanged();
        showPopup();
        performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void showPopup() {
        popupWindow.showAsDropDown(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        popupWindow.dismiss();
    }
}
