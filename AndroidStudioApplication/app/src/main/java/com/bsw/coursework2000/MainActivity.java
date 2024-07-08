package com.bsw.coursework2000;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bsw.coursework2000.helper.DataInitializer;
import com.bsw.coursework2000.helper.NotificationHelper;
import com.bsw.coursework2000.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //put test data into the internal storage
        DataInitializer.initializeData(this);
        NotificationHelper.initializeNotificationChannel(this);




        //if the user is logged in go straight to home page
        SharedPreferences user = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        if (user.contains("userId")){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }


        //user isn't logged in
        setContentView(R.layout.app_open);


        Button buttonLogin = findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(v -> openLoginPage());

        Button buttonCreateAccount = findViewById(R.id.btn_create_account);
        buttonCreateAccount.setOnClickListener(v -> openCreateAccountPage());

    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openCreateAccountPage() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

}