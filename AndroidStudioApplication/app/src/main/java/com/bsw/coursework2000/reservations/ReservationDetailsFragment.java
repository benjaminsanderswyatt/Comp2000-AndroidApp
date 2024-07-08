package com.bsw.coursework2000.reservations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bsw.coursework2000.R;
import com.bsw.coursework2000.booking.BookTableActivity;
import com.bsw.coursework2000.helper.FileHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ReservationDetailsFragment extends Fragment {

    private View view;

    private FragmentManager fragmentManager;

    private static final String DELETE_RESERVATION_FRAGMENT = "DeleteReservationDialogFragment";



    public ReservationDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reservation_details, container, false);

        Button buttonMyReservations = view.findViewById(R.id.btn_my_reservations);
        buttonMyReservations.setOnClickListener(v -> navigateMyReservationsPage());

        //edit button
        Button buttonEditReservation = view.findViewById(R.id.btn_edit_reservation);
        buttonEditReservation.setOnClickListener(v -> doEditReservation());




        fragmentManager = requireActivity().getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(DELETE_RESERVATION_FRAGMENT) == null) {
            //fragment doesn't exist so add it to the fragment manager
            DeleteReservationDialogFragment deleteReservationDialogFragment = new DeleteReservationDialogFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.reservation_main_content, deleteReservationDialogFragment, DELETE_RESERVATION_FRAGMENT)
                    .hide(deleteReservationDialogFragment)
                    .commit();
        }

        //delete button
        Button buttonDeleteReservation = view.findViewById(R.id.btn_cancel_reservation);
        buttonDeleteReservation.setOnClickListener(v -> doDeleteReservation());

        return view;
    }

    private void doEditReservation(){
        if(checkIfCan("editing")){
            Intent intent = new Intent(requireContext(), BookTableActivity.class);
            SharedPreferences currentReservationId = requireContext().getSharedPreferences("CurrentReservationId", Context.MODE_PRIVATE);
            String reservationId = currentReservationId.getString("id", "");
            intent.putExtra("editing",reservationId);
            startActivity(intent);
        } else{
            Toast.makeText(requireContext(), "Cannot edit reservation this close to it.", Toast.LENGTH_SHORT).show();
        }
    }


    private void doDeleteReservation() {
        if(checkIfCan("delete")){
            new DeleteReservationDialogFragment().show(fragmentManager, DELETE_RESERVATION_FRAGMENT);
        } else{
            Toast.makeText(requireContext(), "Cannot delete reservation this close to it.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkIfCan(String action){
        SharedPreferences currentReservationId = requireContext().getSharedPreferences("CurrentReservationId", Context.MODE_PRIVATE);
        String reservationId = currentReservationId.getString("id", "");

        SharedPreferences reservation = requireContext().getSharedPreferences(reservationId, Context.MODE_PRIVATE);
        String date = reservation.getString("date", "");

        Calendar currentTime = Calendar.getInstance();
        Calendar inputTime = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault() );

        try{
            inputTime.setTime(Objects.requireNonNull(sdf.parse(date)));
        } catch (Exception ignored) {

        }

        if(Objects.equals(action, "delete")){
            currentTime.add(Calendar.HOUR_OF_DAY, 24);
        }else{
            currentTime.add(Calendar.WEEK_OF_YEAR, 1);
        }

        //current time + added time is after the date
        return inputTime.after(currentTime);
    }

    private void navigateMyReservationsPage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }




    public void fillDetails(){
        //get the reservationID
        Bundle bundle = getArguments();
        String reservationId = "";
        if (bundle != null) {
            //get the reservationID
            reservationId = bundle.getString("reservationID", "");

            //fill in the details
            //get views
            TextView dateView = view.findViewById(R.id.reservation_date);
            TextView timeView = view.findViewById(R.id.reservation_time);
            TextView tableSizeView = view.findViewById(R.id.reservation_table_size);
            TextView tableNumView = view.findViewById(R.id.reservation_table_number);
            TextView tableLocView = view.findViewById(R.id.reservation_table_location);
            TextView accessibleView = view.findViewById(R.id.reservation_accessible);

            //get reservation details
            SharedPreferences reservation = requireContext().getSharedPreferences(reservationId, Context.MODE_PRIVATE);
            String date = reservation.getString("date","");
            String mealTime = reservation.getString("meal","");
            String tableSize = String.valueOf(reservation.getInt("tableSize",0));
            String tableNum = reservation.getString("seatingArea","");

            //set text to the details
            dateView.setText(date);
            timeView.setText(mealTime);
            tableSizeView.setText(tableSize);
            tableNumView.setText(tableNum);

            try{
                //get the details of the specific table number
                JSONObject allTables = Objects.requireNonNull(FileHelper.readJsonFile(requireContext(), FileHelper.TABLES_JSON)).getJSONObject("tables");
                JSONObject table = allTables.getJSONObject(tableNum);

                String tableLoc = table.getString("location");
                String tableAccessible = table.getString("accessible");

                tableLocView.setText(tableLoc);
                accessibleView.setText(tableAccessible);

            }catch(Exception e){
                //error occurred so go back
                Toast.makeText(requireContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                navigateMyReservationsPage();
            }

        }else{
            //error occurred so go back
            Toast.makeText(requireContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            navigateMyReservationsPage();
        }

        SharedPreferences.Editor editor = requireContext().getSharedPreferences("CurrentReservationId", Context.MODE_PRIVATE).edit();
        editor.putString("id", reservationId);
        editor.apply();
    }

}