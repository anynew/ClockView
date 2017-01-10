package com.ui.anynew.clockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private ClockView clockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clockView = (ClockView)findViewById(R.id.clockView);
        clockView.startAnim();
    }
}
