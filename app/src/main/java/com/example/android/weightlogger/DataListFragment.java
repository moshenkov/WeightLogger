package com.example.android.weightlogger;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Dmitry on 19.09.2016.
 */
public class DataListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    final int IMG_POSITIVE = android.R.drawable.arrow_up_float;
    final int TEXT_COLOR_POSITIVE = Color.RED;
    final int IMG_NEGATIVE = android.R.drawable.arrow_down_float;
    final int TEXT_COLOR_NEGATIVE = Color.rgb(0,125,0);

    private DB db;
    private SimpleCursorAdapter scAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.data_list, null);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIEventsHandler.openListItemEditDialog(getFragmentManager(), null);
            }
        });

        db = new DB(getContext());
        db.open();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListView();
    }

    private void initListView() {

        String[] from = new String[] { DB.COLUMN_DATE, DB.COLUMN_WEIGHT, DB.COLUMN_DELTA,
                DB.COLUMN_ISPOSITIVE };

        int[] to = new int[] { R.id.list_item_date_text, R.id.list_item_weight_text,
                R.id.list_item_delta_text, R.id.list_item_image_arrow };

        scAdapter = new SimpleCursorAdapter(getContext(), R.layout.list_item, null, from, to, 0);
        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == cursor.getColumnIndex(DB.COLUMN_DATE)) {
                    TextView tv = (TextView) view;
                    tv.setText(ListItem.dateToString(cursor.getInt(i)));
                    return true;
                }else if (i == cursor.getColumnIndex(DB.COLUMN_WEIGHT)) {
                    TextView tv = (TextView) view;
                    tv.setText(String.format(Locale.getDefault(),"%.2f", cursor.getFloat(i)) +
                            " " + getResources().getString(R.string.list_item_weight_unit_text));
                    return true;
                }else if (i == cursor.getColumnIndex(DB.COLUMN_DELTA)) {
                    Float value = cursor.getFloat(i);
                    TextView tv = (TextView) view;
                    String delta_text;
                    if (value == 0) delta_text = "";
                    else if (value > 0) delta_text = "+" +
                            String.format(Locale.getDefault(), "%.2f", value) +
                            " " + getResources().getString(R.string.list_item_weight_unit_text);
                    else delta_text = String.format(Locale.getDefault(), "%.2f", value) +
                                " " + getResources().getString(R.string.list_item_weight_unit_text);
                    tv.setText(delta_text);
                    //tv.setTextColor(value > 0 ? TEXT_COLOR_POSITIVE : TEXT_COLOR_NEGATIVE);
                    return true;
                }else if (i == cursor.getColumnIndex(DB.COLUMN_ISPOSITIVE)) {
                    int value = cursor.getInt(i);
                    ImageView iv = (ImageView) view;
                    iv.setImageResource(value == 1 ? IMG_POSITIVE : IMG_NEGATIVE);
                    //iv.setBackgroundColor(value == 1 ? TEXT_COLOR_POSITIVE : TEXT_COLOR_NEGATIVE);
                    return true;
                }else
                    return false;
            }
        });

        setListAdapter(scAdapter);

        final ListView lv = getListView();

        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setTitle(""+lv.getCheckedItemCount());
                actionMode.setSubtitle(getResources().getString(R.string.action_mode_delete_subtitle));
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.context_menu_item_delete: {
                        int numberOfRows = 0;
                        SparseBooleanArray sbArray = lv.getCheckedItemPositions();
                        for (int i = 0; i < sbArray.size(); i++) {
                            int key = sbArray.keyAt(i);
                            if (sbArray.get(key)) numberOfRows++;
                        }
                        if (numberOfRows > 0)
                            UIEventsHandler.openConfirmationDialogDelete(getFragmentManager(),
                                    numberOfRows);
                    }
                }

                //actionMode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cur = scAdapter.getCursor();
                long id = cur.getLong(cur.getColumnIndex(DB.COLUMN_ID));
                ListItem listItem = db.getListItemByID(id);

                //открыть диалог редактирования записи
                if (listItem != null) {
                    UIEventsHandler.openListItemEditDialog(getFragmentManager(), listItem);
                }
            }
        });

        getLoaderManager().initLoader(0, null, this);

        //Cursor cur = scAdapter.getCursor();
        //if(cur.getCount()>0) cur.moveToLast();

    }

    public void addListItem(ListItem listItem) {

        if (db.isExistingRec(listItem)){

            UIEventsHandler.openConfirmationDialogUpdate(getFragmentManager(), listItem);

        } else {

            db.addRec(listItem);

            MyBackupAgent.requestBackup(getContext());

            String toastText = getResources().getString(R.string.record_added_toast);
            Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();

            getLoaderManager().getLoader(0).forceLoad();
        }
    }

    public void updateListItem(ListItem listItem){

        db.updateRec(listItem);

        MyBackupAgent.requestBackup(getContext());

        String toastText = getResources().getString(R.string.record_updated_toast);
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();

        getLoaderManager().getLoader(0).forceLoad();
    }

    public void deleteSelectedListItems(){

        int itemsDeleted = 0;

        Cursor cur = scAdapter.getCursor();
        SparseBooleanArray sbArray = getListView().getCheckedItemPositions();
        for (int i = 0; i < sbArray.size(); i++) {
            int key = sbArray.keyAt(i);
            if (sbArray.get(key)) {
                cur.moveToPosition(key);
                long id = cur.getLong(cur.getColumnIndex(DB.COLUMN_ID));
                db.deleteById(id);
                itemsDeleted++;
            }
        }
        if (itemsDeleted > 0) {
            MyBackupAgent.requestBackup(getContext());

            String toastText = getResources().getString(R.string.record_deleted_toast);
            Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();

            getLoaderManager().getLoader(0).forceLoad();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(getContext(), db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public  Cursor loadInBackground(){
            Cursor cursor = db.getAllData();
            return cursor;
        }

    }


}
