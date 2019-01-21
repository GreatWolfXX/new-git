package com.example.authapi;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class SecondActivity extends AppCompatActivity {
    Button buttonLogin;
    Button buttonReg;
    EditText editLogin;
    EditText editPassword;
    CheckBox checkBoxPassword;
    String password;
    String login;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonReg = (Button) findViewById(R.id.buttonReg);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPassword = (EditText) findViewById(R.id.editPassword);
        checkBoxPassword = (CheckBox) findViewById(R.id.checkBoxPassword);
        dbHelper = new DBHelper(this);
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

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecondActivity.this, InfoActivity.class);
                login = editLogin.getText().toString();

                try {
                    password = (computeHash(editPassword.getText().toString()));
                }catch (UnsupportedEncodingException exception) {

                }catch (NoSuchAlgorithmException noSuchAlgorithmException) {

                }

                Cursor res = dbHelper.getAllData();
                if(res.getCount() == 0){
                    Toast.makeText(SecondActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
                res.moveToFirst();

                //Start Search for matches in the database with the entered data
                while (res.moveToNext()){
                    if(res.getString(1).equals(login) && res.getString(2).equals(password)) {
                        startActivity(intent);
                    }else{
                        editPassword.setError("Not a valid username or password!");
                    }
                }
            }
        });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecondActivity.this, MainActivity.class));
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
