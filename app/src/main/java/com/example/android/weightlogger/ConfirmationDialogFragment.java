package com.example.android.weightlogger;

import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Dmitry on 06.09.2016.
 */
public class ConfirmationDialogFragment extends DialogFragment {

    public static final int MODE_DELETE = 0;
    public static final int MODE_UPDATE = 1;

    UIEventsHandler.EventHandler mEventHandler;

    //private ListItem listItem;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mEventHandler = (UIEventsHandler.EventHandler) activity;
        } catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +
                    " must implement UIEventsHandler.onClickListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        int mode = bundle.getInt("mode");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        switch (mode){
            case MODE_DELETE: {
                int numberOfRows = bundle.getInt("numberOfRows");
                builder.setMessage(R.string.delete_record_confirmation_text)
                        .setPositiveButton(R.string.btn_delete_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mEventHandler.onListItemDeleteConfirmed();
                            }
                        });
                break;
            }
            case MODE_UPDATE: {
                final ListItem listItem = bundle.getParcelable("listItem");
                builder.setMessage(R.string.update_record_confirmation_text)
                        .setPositiveButton(R.string.btn_update_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mEventHandler.onListItemUpdateConfirmed(listItem);
                            }
                        });
                break;
            }
        }
        builder.setNegativeButton(R.string.btn_cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEventHandler.onListItemOperationNotConfirmed();
                    }
                });
        return builder.create();
    }

}
