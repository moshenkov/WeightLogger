package com.example.android.weightlogger;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Dmitry on 20.09.2016.
 */
public class GrafFragment extends Fragment {

    private DB db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.graf_fragment, null);

        GraphView graph = (GraphView) v.findViewById(R.id.graph);


        db = new DB(getContext());
        db.open();

        Cursor cur = db.getGrafData();

        DataPoint[] mDataPoints = new DataPoint[cur.getCount()];

        cur.moveToFirst();
        for (int i = 0; i < cur.getCount(); i++) {
            ListItem li = new ListItem(
                    cur.getLong(cur.getColumnIndex(DB.COLUMN_ID)),
                    cur.getInt(cur.getColumnIndex(DB.COLUMN_DATE)),
                    cur.getFloat(cur.getColumnIndex(DB.COLUMN_WEIGHT))
            );
            mDataPoints[i] = new DataPoint(li.getCalendarDate(), li.getWeight());
            cur.moveToNext();
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(mDataPoints);
        series.setDrawBackground(true);

        graph.addSeries(series);

        // set date label formatter
        // horizontalLabelsAngle
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        //graph.getGridLabelRenderer().setNumVerticalLabels(5);

        graph.getViewport().setScalable(true);

        //graph.getViewport().setScrollable(true);

        return v;
    }
}
