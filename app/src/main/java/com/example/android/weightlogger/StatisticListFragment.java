package com.example.android.weightlogger;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Dmitry on 20.09.2016.
 */
public class StatisticListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private DB db;
    private android.widget.SimpleCursorAdapter scAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stat_fragment, null);

        db = new DB(getContext());
        db.open();

        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] from = new String[] { DB.COLUMN_DATE, DB.COLUMN_MIN_WEIGHT, DB.COLUMN_AVG_WEIGHT,
                DB.COLUMN_MAX_WEIGHT };

        int[] to = new int[] { R.id.stat_list_item_date, R.id.stat_list_item_min_weight,
                R.id.stat_list_item_avg_weight, R.id.stat_list_item_max_weight };

        scAdapter = new SimpleCursorAdapter(getContext(), R.layout.stat_list_item, null, from, to, 0);
        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == cursor.getColumnIndex(DB.COLUMN_DATE)) {

                    int date = cursor.getInt(i);

                    Calendar c = Calendar.getInstance();
                    c.set(date / 100, date % 100 + 1, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");

                    TextView tv = (TextView) view;
                    tv.setText(String.valueOf(sdf.format(c.getTime())));
                    return true;
                } else if(i == cursor.getColumnIndex(DB.COLUMN_MIN_WEIGHT)) {
                    TextView tv = (TextView) view;
                    tv.setText(String.format(Locale.getDefault(),"%.2f", cursor.getFloat(i)) +
                            " " + getResources().getString(R.string.list_item_weight_unit_text));
                    return true;
                } else if(i == cursor.getColumnIndex(DB.COLUMN_AVG_WEIGHT)) {
                    TextView tv = (TextView) view;
                    tv.setText(String.format(Locale.getDefault(),"%.2f", cursor.getFloat(i)) +
                            " " + getResources().getString(R.string.list_item_weight_unit_text));
                    return true;
                } else if(i == cursor.getColumnIndex(DB.COLUMN_MAX_WEIGHT)) {
                    TextView tv = (TextView) view;
                    tv.setText(String.format(Locale.getDefault(),"%.2f", cursor.getFloat(i)) +
                            " " + getResources().getString(R.string.list_item_weight_unit_text));
                    return true;
                }else
                    return false;
            }
        });

        setListAdapter(scAdapter);

        getLoaderManager().initLoader(0, null, this);

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
            Cursor cursor = db.getStatistic();
            return cursor;
        }

    }

}
