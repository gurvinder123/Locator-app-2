package com.example.phoenix.requestdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by phoenix on 10/5/18.
 */

public class splash1 extends AppCompatActivity {
    private static int timeout=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(splash1.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },timeout);
    }
}
