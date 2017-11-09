package com.opiumfive.smtest;


import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;


public class EmailSelectionEditText extends AppCompatEditText implements AdapterView.OnItemClickListener {

    private boolean isHelpEnabled;
    private List<Contact> contactList = new ArrayList<>();
    private ListPopupWindow popupWindow;
    private ContactSearchAdapter adapter;
    private int lastKeyCode;
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

    public void setHelpEnabled(boolean b) {
        isHelpEnabled = b;
    }

    public void setContactList(List<Contact> list) {
        if (list == null) return;
        contactList.clear();
        contactList.addAll(list);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        adapter.getFilter().filter(getText().toString());
        showPopup();
        performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void showPopup() {
        if (!popupWindow.isShowing()) {
            popupWindow.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        append(adapter.getItem(i).getEmail());
        popupWindow.dismiss();
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popupWindow.isShowing() && !popupWindow.isDropDownAlwaysVisible()) {
             // special case for the back key, we do not even try to send it
             // to the drop down list but instead, consume it immediately
             if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                 KeyEvent.DispatcherState state = getKeyDispatcherState();
                 if (state != null) {
                     state.startTracking(event, this);
                 }
                 return true;
             } else if (event.getAction() == KeyEvent.ACTION_UP) {
                 KeyEvent.DispatcherState state = getKeyDispatcherState();
                 if (state != null) {
                     state.handleUpEvent(event);
                 }
                 if (event.isTracking() && !event.isCanceled()) {
                     popupWindow.dismiss();
                     return true;
                 }
             }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean consumed = popupWindow.onKeyUp(keyCode, event);
         if (consumed) {
             switch (keyCode) {
                 // if the list accepts the key events and the key event
                 // was a click, the text view gets the selected item
                 // from the drop down as its content
                 case KeyEvent.KEYCODE_ENTER:
                 case KeyEvent.KEYCODE_DPAD_CENTER:
                 case KeyEvent.KEYCODE_TAB:
                     if (event.hasNoModifiers()) {
                         performCompletion();
                     }
                     return true;
             }
         }

         if (popupWindow.isShowing() && keyCode == KeyEvent.KEYCODE_TAB && event.hasNoModifiers()) {
             performCompletion();
             return true;
         }

         return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (popupWindow.onKeyDown(keyCode, event)) {
            return true;
        }

        if (!popupWindow.isShowing()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (event.hasNoModifiers()) {
                        performValidation();
                    }
            }
        }

        if (popupWindow.isShowing() && keyCode == KeyEvent.KEYCODE_TAB && event.hasNoModifiers()) {
            return true;
        }

        lastKeyCode = keyCode;
        boolean handled = super.onKeyDown(keyCode, event);
        lastKeyCode = KeyEvent.KEYCODE_UNKNOWN;

        if (handled && popupWindow.isShowing()) {
            popupWindow.clearListSelection();
        }

        return handled;
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

        // the drop down is shown only when a minimum number of characters
        // was typed in the text view
        if (enoughToFilter()) {
            if (mFilter != null) {
                mPopupCanBeUpdated = true;
                performFiltering(getText(), lastKeyCode);
            }
        } else {
            // drop down is automatically dismissed when enough characters
            // are deleted from the text view
            if (!popupWindow.isDropDownAlwaysVisible()) {
                popupWindow.dismiss();
            }
            if (mFilter != null) {
                mFilter.filter(null);
            }
        }
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
