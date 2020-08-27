package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    static final  int PIC_CONCAT_REQUEST = 0;
    static final int PERMISSION_CODE = 1;
    private String contact_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    public void openContact(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PIC_CONCAT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == PIC_CONCAT_REQUEST){
            if(resultCode == RESULT_OK){

                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                System.out.println(c.getCount());
                if(c.moveToFirst()){
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    contact_id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));


                    System.out.println(name);
                    System.out.println(hasPhone);

                    if(hasPhone.equalsIgnoreCase("1")){
                        hasPhone = "true";
                    }else{
                        hasPhone = "false";
                    }
                    c.close();

                    if(Boolean.parseBoolean(hasPhone)){
                        System.out.println("=============");
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CODE);
                        }else{
                            getPhoneNumber();
                        }

                    }
                }
                System.out.println(data);
            }
        }
    }

    private void getPhoneNumber(){
        String phoneNumber = null;
        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + '=' + contact_id,
                null,
                null);

        while (phones.moveToNext()){
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        phones.close();
        System.out.println(phoneNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getPhoneNumber();
                }else{
                    System.out.println("permission denied");
                }
                return;
        }
    }
}
