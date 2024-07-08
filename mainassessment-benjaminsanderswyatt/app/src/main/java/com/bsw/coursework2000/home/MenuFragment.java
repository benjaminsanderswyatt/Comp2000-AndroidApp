package com.bsw.coursework2000.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bsw.coursework2000.R;
import com.bsw.coursework2000.accountsettings.AccountSettingsActivity;
import com.bsw.coursework2000.helper.FileHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class MenuFragment extends Fragment {

    private LinearLayout scrollHolder;

    public MenuFragment() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate the layout for fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //display current user
        TextView textViewUsername = view.findViewById(R.id.username);
        SharedPreferences user = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);

        textViewUsername.setText(user.getString("username",""));


        //populate the menu with meals
        scrollHolder = view.findViewById(R.id.holder_scroll);
        displayAllMeals(inflater);


        //go back to home
        ImageButton btnHome = view.findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> navigateToHomePage());

        //activities
        ImageButton buttonAccount_settings = view.findViewById((R.id.btn_account_settings));
        buttonAccount_settings.setOnClickListener(v -> openAccountSettingsPage());


        return view;
    }

    public void displayAllMeals(LayoutInflater inflater){
        try {
            JSONObject jsonObject = FileHelper.readJsonFile(requireContext(), FileHelper.MEALS_JSON);

            if (jsonObject != null) {
                JSONObject mealsObject = jsonObject.getJSONObject("meals");

                //check if the email and password match a user
                Iterator<String> keys = mealsObject.keys();
                while (keys.hasNext()) {
                    String mealID = keys.next();
                    JSONObject meal = mealsObject.getJSONObject(mealID);

                    String name = meal.getString("name");
                    String diet = meal.getString("diet");
                    String allergies = meal.getString("allergies");
                    String description = meal.getString("description");

                    displayMeal(inflater, name, diet, allergies, description);
                }

            } else {
                //JSON file is empty or doesn't exist
                Toast.makeText(requireContext(), "No meals found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //JSON cant read
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error reading JSON", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayMeal(LayoutInflater inflater, String name, String diet, String allergies, String description){
        View mealView = inflater.inflate(R.layout.meal_template, scrollHolder, false);

        //display the meal info
        TextView titleTextView = mealView.findViewById(R.id.title);
        TextView dietTextView = mealView.findViewById(R.id.diet);
        ImageView mealImageView = mealView.findViewById(R.id.meal_image);
        TextView allergiesTextView = mealView.findViewById(R.id.allergies);
        TextView descriptionTextView = mealView.findViewById(R.id.description);

        titleTextView.setText(name);
        dietTextView.setText(diet);
        allergiesTextView.setText(allergies);
        descriptionTextView.setText(description);

        //get the corresponding image
        String resourceName = "meal_" + name.toLowerCase();
        int resourceId = getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());

        if (resourceId != 0) {
            mealImageView.setImageResource(resourceId);
        }


        scrollHolder.addView(mealView);
    }



    private void navigateToHomePage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    private void openAccountSettingsPage() {
        Intent intent = new Intent(requireContext(), AccountSettingsActivity.class);
        startActivity(intent);
    }
}