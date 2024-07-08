package com.bsw.coursework2000.reviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bsw.coursework2000.R;
import com.bsw.coursework2000.helper.FileHelper;
import com.bsw.coursework2000.helper.NotificationHelper;

import java.util.Objects;

public class LeaveReviewDialogFragment extends DialogFragment {

    public LeaveReviewDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_leave_review, null);

        builder.setView(view)
                .setTitle("Leave a Review")
                .setPositiveButton("OK", (dialog, id) -> {
                    //make a review
                    makeReview(view);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.dimAmount = 0.0f;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(layoutParams);

        return dialog;
    }

    private void makeReview(View view){
        TextView contentTextView = view.findViewById(R.id.edit_txt_review);
        String content = contentTextView.getText().toString();

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        Integer rating = (int) ratingBar.getRating();

        //get current user
        SharedPreferences user = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String username = user.getString("username","");

        FileHelper.addReview(requireContext(), username, content, rating);

        //send notification
        NotificationHelper notificationHelper = new NotificationHelper();
        notificationHelper.notifyReviewThanks(requireContext().getApplicationContext());

        //Tell the reviews activity to update its reviews
        if (getActivity() instanceof ReviewsActivity) {
            ((ReviewsActivity) getActivity()).displayReview(username,content,rating);
        }

    }

}
