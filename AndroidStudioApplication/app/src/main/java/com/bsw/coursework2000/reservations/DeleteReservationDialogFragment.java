package com.bsw.coursework2000.reservations;

import static com.bsw.coursework2000.booking.BookTableActivity.API_URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bsw.coursework2000.R;
import com.bsw.coursework2000.helper.NotificationHelper;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteReservationDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_delete_reservation, null);


        builder.setView(view)
                .setTitle("Are you sure?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    //delete reservation
                    SharedPreferences reservation = requireContext().getSharedPreferences("CurrentReservationId", Context.MODE_PRIVATE);
                    String reservationId = reservation.getString("id", "");
                    deleteReservation(requireContext(),dialog, reservationId);
                    dialog.dismiss();


                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    //cancel
                    dialog.cancel();
                });

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.dimAmount = 0.0f;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(layoutParams);

        return dialog;
    }

    private void deleteReservation(Context context, DialogInterface dialog, String reservationId) {
        //cant cancel reservation if it is less than 24 hours before
        SharedPreferences reservation = context.getSharedPreferences(reservationId, Context.MODE_PRIVATE);
        String date = reservation.getString("date", "");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            String deleteUrl = API_URL + "/" + reservationId;

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        //send notification
                        handler.post(() -> {
                            NotificationHelper notificationHelper = new NotificationHelper();
                            notificationHelper.notifyCancelBooking(context.getApplicationContext(), date);
                        });

                    },
                    error -> {
                        //error occurred to go back
                        handler.post(() -> {
                            Toast.makeText(context,"Could not cancel reservation. Please try again.",Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        });
                    }
            );
            queue.add(jsonArrayRequest);
        });
    }

}
