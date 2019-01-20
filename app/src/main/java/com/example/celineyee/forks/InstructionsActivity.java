package com.example.celineyee.forks;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class InstructionsActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (0.8 * width), (int) (0.6 * height));
    }
}
