package com.bsw.coursework2000;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bsw.coursework2000.helper.FileHelper;
import com.bsw.coursework2000.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonCreateAccount = findViewById(R.id.btn_create_account);
        buttonCreateAccount.setOnClickListener(v -> openCreateAccountPage());

        Button buttonLogin = findViewById(R.id.btn_login);
        buttonLogin.setOnClickListener(v -> checkExistingUser());

    }

    private void openCreateAccountPage() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void checkExistingUser() {

        String username = ((EditText) findViewById(R.id.edit_txt_username)).getText().toString();
        String phoneNum = ((EditText) findViewById(R.id.edit_txt_phone_num)).getText().toString();

        try {
            JSONObject jsonObject = FileHelper.readJsonFile(this, FileHelper.USERS_JSON);

            if (jsonObject != null) {
                JSONObject usersObject = jsonObject.getJSONObject("users");

                //check if the email and password match a user
                Iterator<String> keys = usersObject.keys();
                while (keys.hasNext()) {
                    String userId = keys.next();
                    JSONObject user = usersObject.getJSONObject(userId);

                    String storedUsername = user.getString("username");
                    String storedPhoneNum = user.getString("phoneNum");

                    if (username.equals(storedUsername) && phoneNum.equals(storedPhoneNum)) {
                        //Successful

                        //save current user details
                        SharedPreferences.Editor editor = getSharedPreferences("CurrentUser", MODE_PRIVATE).edit();
                        editor.putString("userId", userId);
                        editor.putString("username", username);
                        editor.putString("phoneNum", phoneNum);
                        editor.apply();

                        //goto home page
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish(); //cant go back to login
                        return;
                    }
                }

                //failed
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();

            } else {
                //JSON file is empty or doesn't exist
                Toast.makeText(this, "No user accounts found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //JSON cant read
            e.printStackTrace();
            Toast.makeText(this, "Error reading JSON", Toast.LENGTH_SHORT).show();
        }
    }
}
