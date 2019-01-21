package com.example.authapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.database.Cursor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    Button buttonNext;
    EditText editFirstName;
    EditText editLastName;
    EditText editLogin;
    EditText editPassword;
    CheckBox checkBoxPassword;
    String hashPassword;
    String password;
    String login;
    String token;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPassword = (EditText) findViewById(R.id.editPassword);
        checkBoxPassword = (CheckBox) findViewById(R.id.checkBoxPassword);
        dbHelper = new DBHelper(this);
        //Я не успел пофиксить баг если не будет хоть одного столбца то будут создаваться дубликаты
        dbHelper.insertData("admin", "admin", "admin", "admin");
        //:( но создаються дубликаты админа
        Intent i = getIntent();

        //Start onClick CheckBox
        checkBoxPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxPassword.isChecked()){
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editPassword.setSelection(editPassword.getText().length());
                }else{
                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editPassword.setSelection(editPassword.getText().length());
                }
            }
        });
        //End onClick CheckBox

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkEditText = 4;
                int goodCheck = 0;
                boolean duplicateChecking = true;

                //Start Fill Check
                if(editFirstName.getText().length() > 0) {
                    goodCheck++;
                } else {
                    editFirstName.setError("Це поле має бути заповнене");
                    goodCheck = 0;
                }
                if(editLastName.getText().length() > 0)
                {
                    goodCheck++;
                } else {
                    editLastName.setError("Це поле має бути заповнене");
                    goodCheck = 0;
                }
                if(editLogin.getText().length() > 0)
                {
                    goodCheck++;
                } else {
                    editLogin.setError("Це поле має бути заповнене");
                    goodCheck = 0;
                }
                if(editPassword.getText().length() > 0)
                {
                    goodCheck++;
                } else {
                    editPassword.setError("Це поле має бути заповнене");
                    goodCheck = 0;
                }
                //End Fill Check

                if(goodCheck == checkEditText) {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    password = "" + editPassword.getText();
                    login = "" + editLogin.getText();
                    try {
                        hashPassword = (computeHash(password));
                    }catch (UnsupportedEncodingException exception) {

                    }catch (NoSuchAlgorithmException noSuchAlgorithmException) {

                    }

                    Cursor res = dbHelper.getAllData();
                    res.moveToFirst();

                    //Start Check for duplicates in the database
                        while (res.moveToNext()) {
                            if (res.getString(1).equals(login)) {
                                editLogin.setError("Це логін вже зайнятий.");
                                duplicateChecking = false;
                            }
                        }
                    //End Check for duplicates in the database
                    if(duplicateChecking == true) {
                        dbHelper.insertData(login, hashPassword, editFirstName.getText().toString(), editLastName.getText().toString(), "" + token);
                    }
                    if(duplicateChecking == true) {
                        startActivity(intent);

                    }
                }
            }
        });
    }

    public String computeHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
    digest.reset();
    byte[] byteData = digest.digest(input.getBytes("UTF-8"));
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < byteData.length; i++){
        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
    }
}
