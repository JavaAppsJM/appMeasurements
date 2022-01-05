package be.hvwebsites.metingen.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import be.hvwebsites.metingen.NewDatePickerInterface;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public static DatePickerFragment newInstance(String param1, String param2) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // bepalen meegegeven bundle arguments
        Bundle parmBundle = getArguments();
        String caller = (String) parmBundle.get("Caller");
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Geef resultaat aan de calling activity
        NewDatePickerInterface callingActivity = (NewDatePickerInterface) getActivity();
        callingActivity.processDatePickerResult(year, month, day);

    }
}
