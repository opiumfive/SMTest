package com.opiumfive.smtest.email_choser_view;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import com.opiumfive.smtest.MainActivity;
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
    protected List<Contact> doInBackground(Void... params) {
        MainActivity activity = mActivity.get();

        List<Contact> contList = new ArrayList<>();

        if (activity != null && !isCancelled()) {
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
            if (cur != null && cur.moveToFirst()) {
                do {
                    String name = cur.getString(1);
                    String emailAddress = cur.getString(3);

                    // keep unique only
                    if (emailsSet.add(emailAddress.toLowerCase())) {
                        contList.add(new Contact(name, emailAddress));
                    }
                } while (cur.moveToNext() && !isCancelled());
                cur.close();
            }
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
