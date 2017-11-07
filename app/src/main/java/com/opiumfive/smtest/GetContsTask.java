package com.opiumfive.smtest;


import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class GetContsTask extends AsyncTask<Void, Void, List<Contact>> {

    private WeakReference<MainActivity> mActivity;

    public GetContsTask(MainActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        MainActivity activity = mActivity.get();
    }

    @Override
    protected List<Contact> doInBackground(Void... params) {
        MainActivity activity = mActivity.get();

        List<Contact> contList = new ArrayList<>();

        if (activity != null) {
            Cursor people = null;
            try {
                people = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while (people.moveToNext()) {
                    String contactName = people.getString(people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasPhone = people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if ((Integer.parseInt(hasPhone) > 0)) {
                        Cursor phones = activity.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null, null);
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            contList.add(new Contact(contactName, phoneNumber, email));
                        }
                        phones.close();
                    }
                }

                activity.startManagingCursor(people);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (people != null) {
                    people.close();
                }
            }

            return contList;
        }
        return contList;
    }



    @Override
    protected void onPostExecute(List<Contact> result) {
        MainActivity activity = mActivity.get();
        if (activity != null && !activity.isDestroyed()) {
            if (result != null) {
                activity.setContacts(result);
            }
        }
    }
}
