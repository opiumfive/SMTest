package com.opiumfive.smtest;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
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
            /*Cursor people = null;
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

            return contList; */

            HashSet<String> emailsSet = new HashSet<>();

            ContentResolver cr = activity.getContentResolver();
            String[] PROJECTION = new String[] {
                    ContactsContract.RawContacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.CommonDataKinds.Email.DATA,
                    ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
            String order = "CASE WHEN "
                    + ContactsContract.Contacts.DISPLAY_NAME
                    + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                    + ContactsContract.Contacts.DISPLAY_NAME
                    + ", "
                    + ContactsContract.CommonDataKinds.Email.DATA
                    + " COLLATE NOCASE";
            String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
            if (cur.moveToFirst()) {
                do {
                    String name = cur.getString(1);
                    String emailAddress = cur.getString(3);

                    // keep unique only
                    if (emailsSet.add(emailAddress.toLowerCase())) {
                        contList.add(new Contact(name, emailAddress));
                    }
                } while (cur.moveToNext());
            }

            cur.close();
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
