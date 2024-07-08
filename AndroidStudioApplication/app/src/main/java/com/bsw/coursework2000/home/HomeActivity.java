package com.bsw.coursework2000.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bsw.coursework2000.R;
import com.bsw.coursework2000.accountsettings.AccountSettingsActivity;
import com.bsw.coursework2000.booking.BookTableActivity;
import com.bsw.coursework2000.reservations.MyReservationsActivity;
import com.bsw.coursework2000.reviews.ReviewsActivity;

public class HomeActivity extends AppCompatActivity {

    public static final String MENU_FRAGMENT = "MenuFragment";
    public static final String FIND_US_FRAGMENT = "FindUsFragment";

    private FragmentManager fragmentManager = getSupportFragmentManager();

    public void manageFragmentTransaction(String selectedFragment){
        switch (selectedFragment){
            case MENU_FRAGMENT:
                if (fragmentManager.findFragmentByTag(MENU_FRAGMENT) != null){
                    //fragment exists
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(MENU_FRAGMENT)).commit();
                } else{
                    //fragment doesn't exist so add it to the fragment manager
                    fragmentManager.beginTransaction().add(R.id.main_container, new MenuFragment(), MENU_FRAGMENT).addToBackStack(null).commit();
                }

                //if other fragments are visible hide them
                if (fragmentManager.findFragmentByTag(FIND_US_FRAGMENT) != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(FIND_US_FRAGMENT)).commit();
                }

                break;
            case FIND_US_FRAGMENT:
                if (fragmentManager.findFragmentByTag(FIND_US_FRAGMENT) != null){
                    //fragment exists
                    fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(FIND_US_FRAGMENT)).commit();
                } else{
                    //fragment doesn't exist so add it to the fragment manager
                    fragmentManager.beginTransaction().add(R.id.main_container, new FindUsFragment(), FIND_US_FRAGMENT).addToBackStack(null).commit();
                }

                //if other fragments are visible hide them
                if (fragmentManager.findFragmentByTag(MENU_FRAGMENT) != null){
                    fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(MENU_FRAGMENT)).commit();
                }

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //display current user
        TextView textViewUsername = findViewById(R.id.username);
        SharedPreferences user = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        textViewUsername.setText(user.getString("username",""));





        //activities

        Button buttonBookTable = findViewById(R.id.btn_book_table);
        buttonBookTable.setOnClickListener(v -> openBookTablePage());

        Button buttonMyReservations = findViewById(R.id.btn_my_reservations);
        buttonMyReservations.setOnClickListener(v -> openMyReservationsPage());

        Button buttonReviews = findViewById(R.id.btn_reviews);
        buttonReviews.setOnClickListener(v -> openReviewsPage());

        ImageButton buttonAccount_settings = findViewById(R.id.btn_account_settings);
        buttonAccount_settings.setOnClickListener(v -> openAccountSettingsPage());


        //fragments

        Button buttonMenu = findViewById(R.id.btn_menu);
        ImageButton buttonFindUs = findViewById(R.id.btn_find_us);

        buttonMenu.setOnClickListener(view -> manageFragmentTransaction(MENU_FRAGMENT));
        buttonFindUs.setOnClickListener(view -> manageFragmentTransaction(FIND_US_FRAGMENT));


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //back button pressed
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    //if there are fragments in the back stack, pop the top one
                    fragmentManager.popBackStack();
                } else {
                    //no fragments in the back stack
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    private void openBookTablePage() {
        Intent intent = new Intent(this, BookTableActivity.class);
        startActivity(intent);
    }

    private void openMyReservationsPage() {
        Intent intent = new Intent(this, MyReservationsActivity.class);
        startActivity(intent);
    }

    private void openReviewsPage() {
        Intent intent = new Intent(this, ReviewsActivity.class);
        startActivity(intent);
    }

    private void openAccountSettingsPage() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

}