package com.bsw.coursework2000.helper;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SpinnerHelper implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private SpinnerCallback callback;

    // Interface to communicate spinner selections back to the activity
    public interface SpinnerCallback {
        //for handling spinner selections
        void onItemSelected(Spinner spinner, String selectedItem);
    }

    public SpinnerHelper(Spinner spinner, String[] dataArray, SpinnerCallback callback) {
        this.spinner = spinner;
        this.callback = callback;

        adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, dataArray);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void updateSpinnerData(String[] newDataArray) {
        adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, newDataArray);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        //generic string selected item
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        callback.onItemSelected(spinner, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}