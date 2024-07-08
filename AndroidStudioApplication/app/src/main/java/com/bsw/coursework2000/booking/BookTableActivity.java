package com.bsw.coursework2000.booking;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.bsw.coursework2000.helper.FileHelper;
import com.bsw.coursework2000.helper.SpinnerHelper;
import com.bsw.coursework2000.home.HomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookTableActivity extends AppCompatActivity implements SpinnerHelper.SpinnerCallback, DatePickerDialogFragment.DateSelectedListener {

    public static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2000/ReservationApi/api/Reservations";

    private static final String BOOKING_DETAILS_FRAGMENT = "BookingDetailsFragment";

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private SpinnerHelper spinnerHelperChosenTable;

    JSONArray allReservations;

    RequestQueue queue;
    ExecutorService executor;





    private float lastX, lastY;
    private float scale = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);

        //get API reservations and store them in allReservations
        getApiReservations();

        //display current user
        TextView textViewUsername = findViewById(R.id.username);
        SharedPreferences user = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        textViewUsername.setText(user.getString("username",""));

        //fragments
        if (fragmentManager.findFragmentByTag(BOOKING_DETAILS_FRAGMENT) == null) {
            //fragment doesn't exist so add it to the fragment manager
            BookingDetailsFragment bookingDetailsFragment = new BookingDetailsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.book_table_main_content, bookingDetailsFragment, BOOKING_DETAILS_FRAGMENT)
                    .hide(bookingDetailsFragment)
                    .commit();
        }

        // activities

        ImageButton buttonHome = findViewById(R.id.btn_home);
        buttonHome.setOnClickListener(v -> openHomePage());

        ImageButton buttonAccount_settings = findViewById(R.id.btn_account_settings);
        buttonAccount_settings.setOnClickListener(v -> openAccountSettingsPage());

        //fullscreen map
        Button buttonFullScreenMap = findViewById(R.id.btn_map_fullscreen);
        buttonFullScreenMap.setOnClickListener(v -> showFullscreenImageDialog());

        //Fav Table
        TextView favouriteTable = findViewById(R.id.fav_table);
        favouriteTable.setText(user.getString("spn_fav_table",""));


        //USER CHOICES

        //calender
        Button buttonCalender = findViewById(R.id.btn_pick_date);
        buttonCalender.setOnClickListener(view -> {
            DatePickerDialogFragment calender = new DatePickerDialogFragment();
            calender.setDateSelectedListener(this);
            calender.show(fragmentManager, "datePicker");
            checkStartingUserInputs();
        });

        //choose size
        Spinner chosenTableSize = findViewById(R.id.spn_table_size);
        new SpinnerHelper(chosenTableSize, getResources().getStringArray(R.array.table_size), this);

        //choose meal time
        Spinner chosenMealTime = findViewById(R.id.spn_time);
        new SpinnerHelper(chosenMealTime, getResources().getStringArray(R.array.time), this);

        //chosen table
        Spinner chosenTable = findViewById(R.id.spn_chosen_table);
        spinnerHelperChosenTable = new SpinnerHelper(chosenTable, new String[]{""},this);




        //If the user is editing when they continue i want to pass on their editing status
        Bundle editingBundle = new Bundle();
        //Check if you are editing a already made reservation
        if(getIntent().hasExtra("editing")){
            //if the edit exists you are editing
            String editingReservationId = getIntent().getStringExtra("editing");
            editingBundle.putString("editing", editingReservationId);

        }


        //Continue button
        Button buttonBookingDetails = findViewById(R.id.btn_continue_booking);
        buttonBookingDetails.setOnClickListener(view -> {
                //when chosen table == "" the user has filled in all details
                if (!chosenTable.getSelectedItem().toString().equals("")){
                    BookingDetailsFragment bookingDetailsFragment = (BookingDetailsFragment) fragmentManager.findFragmentByTag(BOOKING_DETAILS_FRAGMENT);
                    assert bookingDetailsFragment != null;
                    bookingDetailsFragment.setArguments(editingBundle);
                    bookingDetailsFragment.fillDetails();
                    fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag(BOOKING_DETAILS_FRAGMENT))).addToBackStack(null).commit();
                } else {
                    Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                }

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

    //gets all the reservations to check if the table has already been booked for that time and date
    public void getApiReservations(){
        //api request on another thread
        executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            queue = Volley.newRequestQueue(this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                    response -> handler.post(() -> allReservations = response),
                    error -> {

                    }
            );
            queue.add(jsonArrayRequest);
        });
    }

    //changes the textview to the date chosen on the calendar
    @Override
    public void onDateSelected(String formattedDate) {
        //set the date in the textview
        TextView dateChosen = findViewById(R.id.date_chosen);
        dateChosen.setText(formattedDate);

        checkStartingUserInputs();
    }

    //when the user has inputted all three the available tables are shown
    public void checkStartingUserInputs(){
        TextView dateChosenTextView = findViewById(R.id.date_chosen);
        Spinner tableSizeTextView = findViewById(R.id.spn_table_size);
        Spinner mealTimeTextView = findViewById(R.id.spn_time);

        String dateChosen = dateChosenTextView.getText().toString();
        String tableSizeChosen = tableSizeTextView.getSelectedItem().toString();
        String mealTimeChosen = mealTimeTextView.getSelectedItem().toString();

        if (!dateChosen.equals("") &&
                !tableSizeChosen.equals("") &&
                !mealTimeChosen.equals("")){

            //set the valid tables
            setValidTables(dateChosen,tableSizeChosen,mealTimeChosen);

            //clear the table details
            setTableDetails(null); //passing through null clears the table details
        }
    }
    //finds the tables which are available for the users inputs
    public void setValidTables(String dateChosen, String tableSizeChosen, String mealTimeChosen){
        //run tasks on another thread to lighten load on main
        executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //check API for taken tables
            List<String> takenTables = getTakenTables(dateChosen, mealTimeChosen);

            //check JSON file table for valid sized tables
            List<String> validTablesForSize = findValidTablesForSize(tableSizeChosen);

            //remove taken tables from valid size tables from json
            assert validTablesForSize != null;
            assert takenTables != null;
            validTablesForSize.removeAll(takenTables);

            //reformat array with "" as array index 0 (so the default chosen spinner is "")
            String[] finalValidTables = new String[validTablesForSize.size() + 1];
            finalValidTables[0] = "";
            for (int i = 1; i < validTablesForSize.size() + 1; i++) {
                finalValidTables[i] = validTablesForSize.get(i - 1);
            }

            //update spinner values to the free tables
            handler.post(() -> spinnerHelperChosenTable.updateSpinnerData(finalValidTables));
        });
    }
    //if the table has already been claimed by someone else add it to the list
    private List<String> getTakenTables(String dateChosen, String mealTimeChosen){
        try {
            List<String> takenTables = new ArrayList<>();
            //Iterate through array
            for (int i = 0; i < allReservations.length(); i++) {
                JSONObject jsonObject = allReservations.getJSONObject(i);

                //get details for other reservation
                String meal = jsonObject.getString("meal");
                String date = jsonObject.getString("date");

                //Checks Conflicting reservation
                if(meal.equalsIgnoreCase(mealTimeChosen) && date.equals(dateChosen)){
                    //get the seating location
                    String seatingArea = jsonObject.getString("seatingArea");

                    //check the seating location is a table number
                    String regexPattern = "^table \\d+$";
                    Pattern pattern = Pattern.compile(regexPattern);
                    Matcher matcher = pattern.matcher(seatingArea);
                    if (matcher.matches() && !takenTables.contains(seatingArea)){
                        //it is so add it to the taken tables
                        takenTables.add(seatingArea);
                    }
                }
            }
            return takenTables;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    //checks through the tables JSON and finds all tables that have a size > than the users inputted size
    private List<String> findValidTablesForSize(String tableSizeChosen){
        try{
            List<String> validTablesForSize = new ArrayList<>();
            JSONObject allTables = Objects.requireNonNull(FileHelper.readJsonFile(this, FileHelper.TABLES_JSON)).getJSONObject("tables");

            Iterator<String> tableNames = allTables.keys();
            while (tableNames.hasNext()){
                String tableName = tableNames.next();
                JSONObject table = allTables.getJSONObject(tableName);

                int jsonSize = table.getInt("size");
                if (Integer.parseInt(tableSizeChosen) <= jsonSize) {
                    validTablesForSize.add(tableName);
                }
            }
            return validTablesForSize;
        }catch(Exception e){
            Toast.makeText(this,"Error getting table size.",Toast.LENGTH_LONG).show();
        }
        return null;
    }

    //Button to go to another activity
    private void openHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    //Button to go to another activity
    private void openAccountSettingsPage() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }


    //for handling spinner selections
    @Override
    public void onItemSelected(Spinner spinner, String selectedItem) {

        Spinner whichSpinner = findViewById(spinner.getId());
        if (whichSpinner == findViewById(R.id.spn_table_size) || (whichSpinner == findViewById(R.id.spn_time))){
            //the user changed a input
            checkStartingUserInputs();

        } else if (whichSpinner == findViewById(R.id.spn_chosen_table)) {
            //the user chose a table
            try {
                JSONObject allTables = Objects.requireNonNull(FileHelper.readJsonFile(this, FileHelper.TABLES_JSON)).getJSONObject("tables");
                JSONObject table = allTables.getJSONObject(selectedItem);
                setTableDetails(table);
            }catch (Exception ignored){

            }
        }
    }

    //change the table details depending on what table has been selected
    private void setTableDetails(JSONObject table){
        //fill in the details about the table
        try {
            TextView tableLocation = findViewById(R.id.detail_table_location);
            TextView tableMaxSize = findViewById(R.id.detail_table_max_size);
            TextView tableAccessible = findViewById(R.id.detail_accessible);

            String location;
            String size;
            String accessible;
            //if the table is null clear the details
            if (table != null){
                location = "Location: " + table.getString("location");
                size = "Maximum table size: " + table.getInt("size");
                accessible = "Wheelchair accessible: " + table.getString("accessible");
            }else{
                location = "Please pick a table";
                size = "";
                accessible = "";
            }

            tableLocation.setText(location);
            tableMaxSize.setText(size);
            tableAccessible.setText(accessible);


        } catch (JSONException ignored) {

        }
    }




    //Display and create functionality for the fullscreen map
    @SuppressLint("ClickableViewAccessibility")
    private void showFullscreenImageDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullscreenDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fullscreen_map);

        Button btnZoomIn = dialog.findViewById(R.id.btn_zoom_in);
        Button btnZoomOut = dialog.findViewById(R.id.btn_zoom_out);

        ImageView fullscreenImage = dialog.findViewById(R.id.fullscreen_image);
        Button buttonContinue = dialog.findViewById(R.id.btn_continue);

        fullscreenImage.setImageResource(R.drawable.restaurant_map);

        //dragging image on screen
        fullscreenImage.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            float sensitivity = 1.0f;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = (x - lastX) * sensitivity;
                    float deltaY = (y - lastY) * sensitivity;

                    // Move image
                    float newX = fullscreenImage.getTranslationX() + deltaX;
                    float newY = fullscreenImage.getTranslationY() + deltaY;

                    fullscreenImage.setTranslationX(newX);
                    fullscreenImage.setTranslationY(newY);

                    lastX = x;
                    lastY = y;
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }

            return true;
        });

        btnZoomIn.setOnClickListener(v -> {
            scale *= 1.2f;
            updateScaleAndPosition(fullscreenImage);
        });

        btnZoomOut.setOnClickListener(v -> {
            scale /= 1.2f;
            updateScaleAndPosition(fullscreenImage);
        });

        buttonContinue.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    //change the map scale
    private void updateScaleAndPosition(ImageView imageView) {
        //setting max and min image scale
        if (scale > 3.0f) {
            scale = 3.0f;
        } else if (scale < 1.0f) {
            scale = 1.0f;
        }

        imageView.setScaleX(scale);
        imageView.setScaleY(scale);
    }


}