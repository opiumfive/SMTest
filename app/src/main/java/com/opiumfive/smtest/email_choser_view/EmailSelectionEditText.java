package com.opiumfive.smtest.email_choser_view;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import java.util.ArrayList;
import java.util.List;


public class EmailSelectionEditText extends AppCompatEditText implements AdapterView.OnItemClickListener {

    private List<Contact> contactList = new ArrayList<>();
    private ListPopupWindow popupWindow;
    private ContactSearchAdapter adapter;

    private boolean openBefore;

    int currentStart = 0;
    int currentEnd = 0;

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

        setSaveEnabled(true);
        setFocusable(true);
        addTextChangedListener(new MyWatcher());
    }

    public List<String> getChosenEmails() {
        List<String> result = new ArrayList<>();
        for (String s : getText().toString().split(",")) {
            // validate and add uniques
            if (Patterns.EMAIL_ADDRESS.matcher(s).matches() && !result.contains(s)) {
                result.add(s);
            }
        }

        return result;
    }

    public boolean isContactListEmpty() {
        return contactList.isEmpty();
    }

    public void setContactList(List<Contact> list) {
        if (list == null) return;
        contactList.clear();
        contactList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void showPopup() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        boolean needToPlaceDelimeter = true;
        if (getText().length() > currentEnd && getText().charAt(currentEnd) == ',') {
            needToPlaceDelimeter = false;
        }

        String put = adapter.getItem(i).getEmail();
        if (needToPlaceDelimeter) {
            put += ",";
        }

        getText().replace(currentStart, currentEnd, put);

        popupWindow.dismiss();
    }

    private class MyWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            if (isAttachedToWindow()) doAfterTextChanged();
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
        openBefore = popupWindow.isShowing();
    }

    void doAfterTextChanged() {
        if (openBefore && !popupWindow.isShowing()) {
            return;
        }

        if (enoughToFilter()) {
            if (getFilter() != null) {
                showPopup();

                int pos = getSelectionStart();
                String text = getText().toString();

                String toCurrentPosString = text.substring(0, pos);
                int start = toCurrentPosString.lastIndexOf(",");
                currentStart = start == -1 ? 0 : start + 1;

                int end = text.indexOf(",", pos);
                currentEnd = end == -1 ? text.length() : end;

                String toFilter = text.substring(currentStart, currentEnd);
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

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = this.contactList;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.contactList.addAll(ss.value);
    }

    private static class SavedState extends BaseSavedState {

        List<Contact> value;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.createTypedArrayList(Contact.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeTypedList(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
