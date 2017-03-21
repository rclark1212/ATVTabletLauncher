package com.example.rclark.atvtabletlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rclark on 3/21/17.
 */

public class GridAdapter extends BaseAdapter {

    private Context mCtx = null;
    private ArrayList<AppDetail> mValues = null;

    public GridAdapter(Context ctx, ArrayList<AppDetail> values) {
        this.mCtx = ctx;
        this.mValues = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            //create the view
            gridView = inflater.inflate(R.layout.grid_layout, null);

        } else {

            //recycling...
            gridView = (View) convertView;
        }

        // set values
        TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
        textView.setText(mValues.get(position).app_label);

        // set image based on selected text
        ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
        imageView.setImageDrawable(mValues.get(position).icon);


        return gridView;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
