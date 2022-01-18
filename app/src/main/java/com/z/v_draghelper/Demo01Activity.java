package com.z.v_draghelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/*
    ViewDragHelper

    GestureDetector

    Scroller

    VelocityTracker
 */
public class Demo01Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo01);
        init();
    }

    private ListView mListView;
    private List<String> mViewItems;

    private void init() {
        mViewItems = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            mViewItems.add("index: " + index);
        }

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mViewItems.size();
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) LayoutInflater.from(Demo01Activity.this)
                        .inflate(R.layout.item_view, parent, false);
                view.setText(mViewItems.get(position));
                return view;
            }
        });
    }
}