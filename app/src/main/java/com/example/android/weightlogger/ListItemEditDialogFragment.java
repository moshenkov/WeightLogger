package com.example.android.weightlogger;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Dmitry on 17.08.2016.
 */
public class ListItemEditDialogFragment extends DialogFragment {

    public static final int OPERATION_ADD = 0;
    public static final int OPERATION_EDIT = 1;
    public static final String OPERATION_STR = "operation";

    UIEventsHandler.EventHandler mEventHandler;

    private TextView tvDate, tvWeightError;
    private EditText etWeight;
    private Button btnSave;
    private ListItem listItem;
    private int operation;

    private void onDateChange() {
        tvDate.setText(listItem.getFormattedStringDate());
    }

    private void onWeightChange() {
        boolean isValid = listItem.isValid();
        btnSave.setEnabled(isValid);
        tvWeightError.setVisibility(isValid ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mEventHandler = (UIEventsHandler.EventHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UIEventsHandler.onClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_add, null);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        etWeight = (EditText) v.findViewById(R.id.etWeight);
        btnSave = (Button) v.findViewById(R.id.btnSave);
        tvWeightError = (TextView) v.findViewById(R.id.weight_error_text);

        if (savedInstanceState != null) {
            listItem = savedInstanceState.getParcelable(ListItem.class.getCanonicalName());
            operation = savedInstanceState.getInt(OPERATION_STR);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                listItem = args.getParcelable(ListItem.class.getCanonicalName());
                operation = args.getInt(OPERATION_STR);
            } else {
                listItem = new ListItem();
                operation = OPERATION_ADD;
            }
            float weight = listItem.getWeight();
            if (weight != 0) etWeight.setText(String.valueOf(weight));
        }
        onDateChange();
        getDialog().setTitle(operation == OPERATION_ADD ?
                R.string.add_dialog_title_add : R.string.add_dialog_title_edit);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();

                Bundle b = new Bundle();
                b.putInt("year",  listItem.getYear());
                b.putInt("month", listItem.getMonth());
                b.putInt("day",   listItem.getDay());

                newFragment.setArguments(b);

                newFragment.setCallBack(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        listItem.setDate(i, i1, i2);
                        onDateChange();
                    }
                });
                newFragment.show(getFragmentManager(), newFragment.getClass().getCanonicalName());
            }

        });

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.isEmpty()) {
                    listItem.setWeight(0);
                } else {
                    listItem.setWeight(Float.parseFloat(str));
                }
                onWeightChange();
            }
        });

        btnSave.setEnabled(listItem.isValid());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEventHandler.onListItemEdtDialogButtonSaveClick(listItem, operation);
                dismiss();
            }
        });


        Button btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ListItem.class.getCanonicalName(), listItem);
        outState.putInt(OPERATION_STR, operation);
    }

    public static class DatePickerFragment extends DialogFragment {

        DatePickerDialog.OnDateSetListener onDateSetListener;
        private int mYear, mMonth, mDay;

        public void setCallBack(DatePickerDialog.OnDateSetListener odsl) {
            onDateSetListener = odsl;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Bundle args = getArguments();
            mYear = args.getInt("year");
            mMonth = args.getInt("month");
            mDay = args.getInt("day");

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), onDateSetListener,
                    mYear, mMonth, mDay);
            DatePicker dp = dpd.getDatePicker();
            Calendar calendar = Calendar.getInstance();
            dp.setMaxDate(calendar.getTimeInMillis());

            return dpd;
        }
    }


}
