package com.bsw.coursework2000.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bsw.coursework2000.R;
import com.bsw.coursework2000.accountsettings.AccountSettingsActivity;

public class FindUsFragment extends Fragment {

    public FindUsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate the layout for fragment
        View view = inflater.inflate(R.layout.fragment_find_us, container, false);

        //display current user
        TextView textViewUsername = view.findViewById(R.id.username);
        SharedPreferences user = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);

        textViewUsername.setText(user.getString("username",""));





        ImageButton btnHome = view.findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> navigateToHomePage());

        //activities
        ImageButton buttonAccount_settings = view.findViewById((R.id.btn_account_settings));
        buttonAccount_settings.setOnClickListener(v -> openAccountSettingsPage());

        return view;
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