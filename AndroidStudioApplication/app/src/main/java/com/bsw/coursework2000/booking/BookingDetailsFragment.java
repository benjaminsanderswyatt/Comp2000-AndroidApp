package com.bsw.coursework2000.booking;

import static com.bsw.coursework2000.booking.BookTableActivity.API_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bsw.coursework2000.R;
import com.bsw.coursework2000.helper.NotificationHelper;
import com.bsw.coursework2000.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BookingDetailsFragment extends Fragment {

    private View view;

    JSONObject reservation = new JSONObject();

    private String reservationId = null;

    public BookingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //check editing status
        Bundle bundle = getArguments();
        //bundle is full when the user is editing
        if(bundle != null && !bundle.isEmpty()){
            reservationId = bundle.getString("editing");
        }


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_details, container, false);

        Button btnEditBooking = view.findViewById(R.id.btn_edit_booking);
        btnEditBooking.setOnClickListener(v -> navigateToBookTablePage());

        Button btnProceed = view.findViewById(R.id.btn_proceed);
        btnProceed.setOnClickListener(v -> checkIfEditing());

        return view;
    }

    private void navigateToBookTablePage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    private void checkIfEditing(){
        //always post the reservation
        try{
            postBooking();

            //when editing delete the old reservation
            if(reservationId != null){
                //delete the previous reservation
                deleteBooking();

            }
        } catch(Exception e){

            //when editing
            if(reservationId != null){
                goBackToHomePage();
            }else{
                restartBooking();
            }

        }

    }

    private void postBooking(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BookTableActivity.API_URL, reservation,
                    response -> handler.post(() -> {
                        Toast.makeText(requireContext(), "Booking made", Toast.LENGTH_LONG).show();
                        try {
                            NotificationHelper notificationHelper = new NotificationHelper();
                            notificationHelper.notifyBookingSuccess(requireContext().getApplicationContext(), reservation.getString("date"));
                        } catch (JSONException ignored) {

                        }
                        goBackToHomePage();
                    }),

                    error -> {
                        //An error occurred (this could include reservation being taken by another user)
                        handler.post(this::restartBooking);
                    });

            queue.add(jsonObjectRequest);
        });

    }

    private void deleteBooking(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            String deleteUrl = API_URL + "/" + reservationId;

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        //send notification
                        handler.post(() -> {
                            NotificationHelper notificationHelper = new NotificationHelper();
                            notificationHelper.notifyUpdatedBooking(requireContext().getApplicationContext());
                        });

                    },
                    error -> {
                        //error occurred to go back
                        handler.post(() -> {
                            Toast.makeText(requireContext(), "Couldn't update reservation. Please try again.", Toast.LENGTH_LONG).show();
                            goBackToHomePage();
                        });
                    }
            );
            queue.add(jsonArrayRequest);
        });
    }

    private void goBackToHomePage(){
        Intent intent = new Intent(requireActivity(), HomeActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public void fillDetails() {

        //fill in the details
        //get views
        TextView dateView = view.findViewById(R.id.reservation_date);
        TextView timeView = view.findViewById(R.id.reservation_time);
        TextView tableSizeView = view.findViewById(R.id.reservation_table_size);
        TextView tableNumView = view.findViewById(R.id.reservation_table_number);
        TextView tableLocView = view.findViewById(R.id.reservation_table_location);
        TextView accessibleView = view.findViewById(R.id.reservation_accessible);

        //get reservation details

        TextView dateChosenTextView = requireActivity().findViewById(R.id.date_chosen);
        String dateChosen = dateChosenTextView.getText().toString();

        Spinner tableSizeSpinner = requireActivity().findViewById(R.id.spn_table_size);
        String tableSize = tableSizeSpinner.getSelectedItem().toString();

        Spinner mealTimeSpinner= requireActivity().findViewById(R.id.spn_time);
        String mealTime = mealTimeSpinner.getSelectedItem().toString();

        Spinner tableNumSpinner = requireActivity().findViewById(R.id.spn_chosen_table);
        String tableNum = tableNumSpinner.getSelectedItem().toString();

        TextView tableLocation = requireActivity().findViewById(R.id.detail_table_location);
        TextView tableAccessible = requireActivity().findViewById(R.id.detail_accessible);


        //set text to the details
        dateView.setText(dateChosen);
        timeView.setText(mealTime);
        tableSizeView.setText(tableSize);
        tableNumView.setText(tableNum);

        //split the string
        int colonIndexLoc = tableLocation.getText().toString().indexOf(": ");
        String locationArea = tableLocation.getText().toString().substring(colonIndexLoc + 2);

        tableLocView.setText(locationArea);

        //get the Yes or No part of the string
        int colonIndexAccess = tableAccessible.getText().toString().indexOf(": ");
        String accessibilityStatus = tableAccessible.getText().toString().substring(colonIndexAccess + 2);

        accessibleView.setText(accessibilityStatus);


        //make into JSON Object
        createJsonObjectToPost(mealTime, tableNum, tableSize, dateChosen);
    }


    private void createJsonObjectToPost(String mealTime, String tableNum, String tableSize, String date) {
        try{
            JSONObject toPost = new JSONObject();
            //username
            SharedPreferences currentUser = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
            toPost.put("customerName", currentUser.getString("username", ""));

            //phone Number
            toPost.put("customerPhoneNumber", currentUser.getString("phoneNum", ""));

            //Meal Time
            toPost.put("meal", mealTime);

            //Seating Location (table number)
            toPost.put("seatingArea", tableNum);

            //table size
            toPost.put("tableSize", Integer.parseInt(tableSize));

            //date
            toPost.put("date", date);

            reservation = toPost;

        } catch(JSONException e){
            restartBooking();
        }
    }

    private void restartBooking(){
        Toast.makeText(requireContext(), "Error making booking. Please Try Again", Toast.LENGTH_LONG).show();
        //restart the book table activity to update reservations and clear choices
        Intent intent = new Intent(requireActivity(), BookTableActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}