package com.z.v_draghelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/*
    处理子View拖动的工具类
        DragViewHelper
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.demo00:
                startActivity(new Intent(this, Demo00Activity.class));
                break;
            case R.id.demo01:
                startActivity(new Intent(this, Demo01Activity.class));
                break;
        }
    }
}