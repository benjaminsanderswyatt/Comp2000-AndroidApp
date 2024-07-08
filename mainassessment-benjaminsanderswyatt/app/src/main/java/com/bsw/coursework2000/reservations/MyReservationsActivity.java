package com.bsw.coursework2000.reservations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bsw.coursework2000.R;
import com.bsw.coursework2000.accountsettings.AccountSettingsActivity;
import com.bsw.coursework2000.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyReservationsActivity extends AppCompatActivity {

    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2000/ReservationApi/api/Reservations";

    private static final String RESERVATIONS_DETAILS_FRAGMENT = "ReservationsDetailsFragment";

    private LinearLayout scrollHolder;

    RequestQueue queue;

    ExecutorService executor;

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        scrollHolder = findViewById(R.id.holder_scroll);

        apiRequest();




        //display current user
        TextView textViewUsername = findViewById(R.id.username);
        SharedPreferences user = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        textViewUsername.setText(user.getString("username",""));


        // activities

        ImageButton buttonHome = findViewById(R.id.btn_home);
        buttonHome.setOnClickListener(v -> openHomePage());

        ImageButton buttonAccount_settings = findViewById(R.id.btn_account_settings);
        buttonAccount_settings.setOnClickListener(v -> openAccountSettingsPage());
    }


    private void addFragment(){
        if (fragmentManager.findFragmentByTag(RESERVATIONS_DETAILS_FRAGMENT) == null) {
            //fragment doesn't exist so add it to the fragment manager
            ReservationDetailsFragment reservationDetailsFragment = new ReservationDetailsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.reservation_main_content, reservationDetailsFragment, RESERVATIONS_DETAILS_FRAGMENT)
                    .hide(reservationDetailsFragment)
                    .commit();
        }
    }


    private void openHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openAccountSettingsPage() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

    public void apiRequest(){
        executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Handle background work here

            queue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                    response -> {
                        try {

                            //Iterate through array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                //Current User
                                SharedPreferences userSP = getSharedPreferences("CurrentUser", MODE_PRIVATE);
                                String currentUser = userSP.getString("username","");
                                String currentUserPhoneNum = userSP.getString("phoneNum","");

                                //Reservation from the API
                                String customerName = jsonObject.getString("customerName");
                                String customerPhoneNumber = jsonObject.getString("customerPhoneNumber");

                                if (currentUser.equals(customerName) && currentUserPhoneNum.equals(customerPhoneNumber)){
                                    //get reservation details
                                    String reservationID = String.valueOf(jsonObject.getInt("id"));
                                    String meal = jsonObject.getString("meal");
                                    String seatingArea = jsonObject.getString("seatingArea");
                                    int tableSize = jsonObject.getInt("tableSize");
                                    String date = jsonObject.getString("date");

                                    //save current reservation
                                    SharedPreferences.Editor editor = getSharedPreferences(reservationID, MODE_PRIVATE).edit();
                                    editor.putString("name", customerName);
                                    editor.putString("phoneNum", customerPhoneNumber);
                                    editor.putString("meal", meal);
                                    editor.putString("seatingArea", seatingArea);
                                    editor.putInt("tableSize", tableSize);
                                    editor.putString("date", date);
                                    editor.apply();

                                    //Handle the UI here
                                    handler.post(() -> displayReservation(reservationID));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> handleVolleyError(this)
            );
            queue.add(jsonArrayRequest);


        });
    }

    @Override
    protected void onDestroy() {
        if (queue != null) {
            queue.stop();
        }
        if (executor != null) {
            executor.shutdown();
        }

        super.onDestroy();
    }

    public void displayReservation(String reservationID){
        LayoutInflater inflater = getLayoutInflater();
        View reservationView = inflater.inflate(R.layout.my_reservation_template, scrollHolder, false);

        TextView dateTextView = reservationView.findViewById(R.id.date);
        TextView timeTextView = reservationView.findViewById(R.id.time);
        TextView tableSizeTextView = reservationView.findViewById(R.id.table_size);
        Button viewDetailsButton = reservationView.findViewById(R.id.btn_see_details);

        //get details from shared preferences
        SharedPreferences reservation = getSharedPreferences(reservationID, MODE_PRIVATE);
        String date = reservation.getString("date","");
        String mealTime = reservation.getString("meal","");
        String tableSize = String.valueOf(reservation.getInt("tableSize",0));

        dateTextView.setText(date);
        timeTextView.setText(mealTime);
        tableSizeTextView.setText(tableSize);

        //ensure the fragment exists for the button to navigate to
        addFragment();
        //add the views button
        viewDetailsButton.setOnClickListener(v -> {

            // Handle button click, for example, start a new activity
            Bundle bundle = new Bundle();
            bundle.putString("reservationID", reservationID);
            ReservationDetailsFragment fragment = (ReservationDetailsFragment) fragmentManager.findFragmentByTag(RESERVATIONS_DETAILS_FRAGMENT);
            assert fragment != null;
            fragment.setArguments(bundle);

            //fill in the fragments details before showing
            fragment.fillDetails();

            fragmentManager.beginTransaction().show(fragment).addToBackStack(null).commit();

        });



        scrollHolder.addView(reservationView, 0); //index 0 puts the review at the top (so newest first)
    }




    private void handleVolleyError(Context context) {
        Toast.makeText(context, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        openHomePage();
    }



}