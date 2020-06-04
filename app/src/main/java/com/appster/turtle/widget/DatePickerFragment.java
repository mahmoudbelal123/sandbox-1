package com.appster.turtle.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created  on 07/03/18 .
 */

public class DatePickerFragment extends DialogFragment {
    private DatePickerDialog.OnDateSetListener onDateSetListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                onDateSetListener,
                Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR),
                Calendar.getInstance(Locale.getDefault()).get(Calendar.HOUR),
                Calendar.getInstance(Locale.getDefault()).get(Calendar.MINUTE));
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        return datePickerDialog;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }
}