package com.example.android.weightlogger;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseBooleanArray;

/**
 * Created by Dmitry on 21.09.2016.
 */

class UIEventsHandler{

    interface EventHandler {
        void onListItemEdtDialogButtonSaveClick(ListItem listItem, int operation);
        void onListItemDeleteConfirmed();
        void onListItemUpdateConfirmed(ListItem listItem);
        void onListItemOperationNotConfirmed();
    }

    static void openListItemEditDialog(FragmentManager fragmentManager, ListItem listItem){

        ListItem mListItem;
        int operation;

        if (listItem == null) {
            mListItem = new ListItem();
            operation = ListItemEditDialogFragment.OPERATION_ADD;
        } else {
            mListItem = listItem;
            operation = ListItemEditDialogFragment.OPERATION_EDIT;
        }

        ListItemEditDialogFragment listItemEditDialog = new ListItemEditDialogFragment();
        listItemEditDialog.show(fragmentManager, listItemEditDialog.getClass().getCanonicalName());

        Bundle bundle = new Bundle();
        bundle.putInt(ListItemEditDialogFragment.OPERATION_STR, operation);
        bundle.putParcelable(ListItem.class.getCanonicalName(), mListItem);

        listItemEditDialog.setArguments(bundle);

    }

    static void openConfirmationDialogDelete(FragmentManager fragmentManager, int numberOfRows) {

        DialogFragment dialog = new ConfirmationDialogFragment();
        dialog.show(fragmentManager,dialog.getClass().getCanonicalName());

        Bundle bundle = new Bundle();
        bundle.putInt("mode", ConfirmationDialogFragment.MODE_DELETE);
        bundle.putInt("numberOfRows", numberOfRows);

        dialog.setArguments(bundle);

    }
    static void openConfirmationDialogUpdate(FragmentManager fragmentManager, ListItem listItem) {

        DialogFragment dialog = new ConfirmationDialogFragment();
        dialog.show(fragmentManager,dialog.getClass().getCanonicalName());

        Bundle bundle = new Bundle();
        bundle.putInt("mode", ConfirmationDialogFragment.MODE_UPDATE);
        bundle.putParcelable("listItem", listItem);

        dialog.setArguments(bundle);

    }




}
