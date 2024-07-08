package com.bsw.coursework2000;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bsw.coursework2000.helper.FileHelper;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button buttonLogin = findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(v -> openLoginPage());

        Button buttonCreateNewAccount = findViewById(R.id.btn_create_account);
        buttonCreateNewAccount.setOnClickListener(v -> createNewAccount());

    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void createNewAccount() {

        String username = ((EditText) findViewById(R.id.edit_txt_name)).getText().toString();
        String phoneNum = ((EditText) findViewById(R.id.edit_txt_phone_number)).getText().toString();

        //Add new user to the JSON file
        if(FileHelper.addUser(this, username, phoneNum)){
            Toast.makeText(this, "Account created", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to create account", Toast.LENGTH_LONG).show();
        }



    }
}