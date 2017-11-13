package com.opiumfive.smtest;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.opiumfive.smtest.email_choser_view.Contact;
import com.opiumfive.smtest.email_choser_view.EmailSelectionEditText;
import com.opiumfive.smtest.email_choser_view.GetContsTask;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private boolean isDestroyed = false;
    private GetContsTask getContsTask;
    private EmailSelectionEditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit);

        button = findViewById(R.id.result);
        button.setOnClickListener(this);

        if (editText.isContactListEmpty()) {
            getContactsWithPermissionCheckAsync();
        }
    }

    private void getContactsWithPermissionCheckAsync() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                showPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            getContactsAsync();
        }
    }

    @Override
    public void onClick(View view) {
        // show Result
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(R.string.result);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        arrayAdapter.addAll(editText.getChosenEmails());

        builderSingle.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactsAsync();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        showPermissionExplanationDialog();
                    }
                }
            }
        }
    }

    private void showPermissionExplanationDialog() {
        //stub
    }

    private void getContactsAsync() {
        getContsTask = new GetContsTask(this);
        getContsTask.execute();
    }

    public void setContacts(List<Contact> list) {
        if (editText != null) {
            editText.setContactList(list);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        button.setOnClickListener(null);
        isDestroyed = true;
        if (getContsTask != null) {
            getContsTask.cancel(true);
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
