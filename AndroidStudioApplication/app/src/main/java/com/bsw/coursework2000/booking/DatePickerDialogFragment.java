package com.bsw.coursework2000.booking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //book a week in advance
        calendar.add(Calendar.DAY_OF_MONTH, 7);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //date has been chosen


        //format the date
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(selectedDate.getTime());
        dateSelectedListener.onDateSelected(formattedDate);
    }

    private DateSelectedListener dateSelectedListener;

    public void setDateSelectedListener(DateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    public interface DateSelectedListener {
        void onDateSelected(String formattedDate);
    }

}