package com.bsw.coursework2000.accountsettings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bsw.coursework2000.MainActivity;
import com.bsw.coursework2000.R;
import com.bsw.coursework2000.helper.FileHelper;

public class DeleteAccountDialogFragment extends DialogFragment {





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_delete_account, null);


        builder.setView(view)
                .setTitle("Are you sure?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    //delete user from json
                    SharedPreferences user = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
                    FileHelper.removeUser(requireContext(), user.getString("userId",""));
                    //sign out the user
                    logUserOut();

                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    //cancel
                    dialog.cancel();
                });

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
    }

    private void logUserOut(){
        //clear the user
        SharedPreferences.Editor editor = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        //go to app open page
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

}
