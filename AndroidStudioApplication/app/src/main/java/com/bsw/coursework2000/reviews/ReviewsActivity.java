package com.bsw.coursework2000.reviews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bsw.coursework2000.R;
import com.bsw.coursework2000.accountsettings.AccountSettingsActivity;
import com.bsw.coursework2000.helper.FileHelper;
import com.bsw.coursework2000.home.HomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ReviewsActivity extends AppCompatActivity {

    private LinearLayout scrollHolder;

    private static final String LEAVE_REVIEW_FRAGMENT = "LeaveReviewFragment";

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        //display current user
        TextView textViewUsername = findViewById(R.id.username);
        SharedPreferences user = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        textViewUsername.setText(user.getString("username",""));


        //populate scroll with reviews
        scrollHolder = findViewById(R.id.holder_scroll);
        displayAllReviews();



        fragmentManager = getSupportFragmentManager();

        //fragments

        if (fragmentManager.findFragmentByTag(LEAVE_REVIEW_FRAGMENT) == null) {
            //fragment doesn't exist so add it to the fragment manager
            LeaveReviewDialogFragment leaveReviewDialogFragment = new LeaveReviewDialogFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.reviews_main_container, leaveReviewDialogFragment, LEAVE_REVIEW_FRAGMENT)
                    .hide(leaveReviewDialogFragment)
                    .commit();
        }

        Button buttonLeaveReview = findViewById(R.id.btn_leave_review);
        buttonLeaveReview.setOnClickListener(view -> new LeaveReviewDialogFragment().show(fragmentManager, LEAVE_REVIEW_FRAGMENT));


        // activities

        ImageButton buttonHome = findViewById(R.id.btn_home);
        buttonHome.setOnClickListener(v -> openHomePage());

        ImageButton buttonAccount_settings = findViewById(R.id.btn_account_settings);
        buttonAccount_settings.setOnClickListener(v -> openAccountSettingsPage());

    }

    public void displayAllReviews(){
        try {
            JSONObject jsonObject = FileHelper.readJsonFile(this, FileHelper.REVIEWS_JSON);

            if (jsonObject != null) {
                JSONObject reviewsObject = jsonObject.getJSONObject("reviews");

                Iterator<String> keys = reviewsObject.keys();
                while (keys.hasNext()) {
                    String reviewID = keys.next();
                    JSONObject review = reviewsObject.getJSONObject(reviewID);

                    String username = review.getString("username");
                    String content = review.getString("content");
                    Integer rating = review.getInt("rating");

                    displayReview(username, content, rating);
                }

            } else {
                //JSON file is empty or doesn't exist
                Toast.makeText(this, "No meals found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //JSON cant read
            e.printStackTrace();
            Toast.makeText(this, "Error reading JSON", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayReview(String username, String content, Integer rating){
        LayoutInflater inflater = getLayoutInflater();
        View reviewView = inflater.inflate(R.layout.review_template, scrollHolder, false);

        //display the review info
        TextView usernameTextView = reviewView.findViewById(R.id.username);
        TextView contentTextView = reviewView.findViewById(R.id.content);
        RatingBar ratingRatingBar = reviewView.findViewById(R.id.rating);

        usernameTextView.setText(username);
        contentTextView.setText(content);
        ratingRatingBar.setRating(rating);

        scrollHolder.addView(reviewView, 0); //index 0 puts the review at the top (so newest first)
    }









    private void openHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void openAccountSettingsPage() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

}