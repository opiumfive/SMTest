package com.opiumfive.smtest;


import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EmailSelectionEditText extends AppCompatEditText implements AdapterView.OnItemClickListener {

    private List<Contact> contactList = new ArrayList<>();
    private ListPopupWindow popupWindow;
    private ContactSearchAdapter adapter;

    private boolean openBefore;
    private boolean blockCompletion;

    public EmailSelectionEditText(Context context) {
        super(context);
        init(context);
    }

    public EmailSelectionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        popupWindow = new ListPopupWindow(context);
        adapter = new ContactSearchAdapter(context, android.R.layout.simple_list_item_1, contactList);
        popupWindow.setAdapter(adapter);
        popupWindow.setOnItemClickListener(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnchorView(this);

        setFocusable(true);
        addTextChangedListener(new MyWatcher());

    }

    public List<String> getChosenEmails() {
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(getText().toString().split("-|\\.|,|;| |_")));
        return result;
    }

    public void setContactList(List<Contact> list) {
        if (list == null) return;
        contactList.clear();
        contactList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void showPopup() {
        if (!popupWindow.isShowing()) {
            popupWindow.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        append(adapter.getItem(i).getEmail() + ", ");
        popupWindow.dismiss();
    }

    private class MyWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            doAfterTextChanged();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            doBeforeTextChanged();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    void doBeforeTextChanged() {
        if (blockCompletion) return;
        openBefore = popupWindow.isShowing();
    }

    void doAfterTextChanged() {
        if (blockCompletion) return;

        // if the list was open before the keystroke, but closed afterwards,
        // then something in the keystroke processing (an input filter perhaps)
        // called performCompletion() and we shouldn't do any more processing.
        if (openBefore && !popupWindow.isShowing()) {
            return;
        }

        if (enoughToFilter()) {
            if (getFilter() != null) {
                //mPopupCanBeUpdated = true;
                showPopup();

                int pos = getSelectionStart();
                String text = getText().toString();
                String current = text.substring(0, pos);
                int start = current.lastIndexOf(" ");
                String toFilter = start == -1 ? current : current.substring(start);
                getFilter().filter(toFilter.trim());
            }
        } else {
            popupWindow.dismiss();
            if (getFilter() != null) {
                getFilter().filter(null);
            }
        }
    }

    private Filter getFilter() {
        if (adapter == null) return null;
        return adapter.getFilter();
    }

    private boolean enoughToFilter() {
        return getText().length() > 1;
    }

    public void setText(CharSequence text, boolean filter) {
        if (filter) {
            setText(text);
        } else {
            blockCompletion = true;
            setText(text);
            blockCompletion = false;
        }
    }
}
