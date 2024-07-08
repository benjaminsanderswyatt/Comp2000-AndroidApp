package com.bsw.coursework2000.accountsettings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bsw.coursework2000.MainActivity;
import com.bsw.coursework2000.R;
import com.bsw.coursework2000.helper.FileHelper;
import com.bsw.coursework2000.helper.SpinnerHelper;
import com.bsw.coursework2000.home.HomeActivity;

public class AccountSettingsActivity extends AppCompatActivity implements SpinnerHelper.SpinnerCallback{

    private static final String DELETE_ACCOUNT_FRAGMENT = "DeleteAccountFragment";

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //Fragments
        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(DELETE_ACCOUNT_FRAGMENT) == null) {
            //fragment doesn't exist so add it to the fragment manager
            DeleteAccountDialogFragment deleteAccountDialogFragment = new DeleteAccountDialogFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.account_main_container, deleteAccountDialogFragment, DELETE_ACCOUNT_FRAGMENT)
                    .hide(deleteAccountDialogFragment)
                    .commit();
        }

        Button buttonDeleteAccount = findViewById(R.id.btn_delete_account);
        buttonDeleteAccount.setOnClickListener(view -> new DeleteAccountDialogFragment().show(fragmentManager, DELETE_ACCOUNT_FRAGMENT));



        //Activities

        ImageButton buttonHome = findViewById(R.id.btn_home);
        buttonHome.setOnClickListener(v -> openHomePage());

        Button buttonSignOut = findViewById(R.id.btn_sign_out);
        buttonSignOut.setOnClickListener(v -> signOut());




        //spinners

        Spinner spinnerFavTable = findViewById(R.id.spn_fav_table);
        new SpinnerHelper(spinnerFavTable, FileHelper.getAllTables(this), this);

        Spinner spinnerFavMeal = findViewById(R.id.spn_fav_meal);
        new SpinnerHelper(spinnerFavMeal, FileHelper.getAllMeals(this), this);

    }

    @Override
    public void onItemSelected(Spinner spinner, String selectedItem) {
        //save the item to the saved preferences
        String spinnerName = getResources().getResourceEntryName(spinner.getId());
        saveToSharedPreferences(spinnerName,selectedItem);
    }

    private void saveToSharedPreferences(String key, String value) {
        //save the favtable and favmeal under the spinner id
        SharedPreferences.Editor editor = getSharedPreferences("CurrentUser", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }


    private void openHomePage() {
        //save the spinner value
        Spinner favTable = findViewById(R.id.spn_fav_table);
        Spinner favMeal = findViewById(R.id.spn_fav_meal);

        String selectedFavTable = favTable.getSelectedItem().toString();
        String selectedFavMeal = favMeal.getSelectedItem().toString();

        SharedPreferences.Editor editor = getSharedPreferences("CurrentUser", MODE_PRIVATE).edit();
        editor.putString("favTable", selectedFavTable);
        editor.putString("favMeal", selectedFavMeal);
        editor.apply();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void signOut(){
        //clear the user
        SharedPreferences.Editor editor = getSharedPreferences("CurrentUser", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        //go to app open page
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}