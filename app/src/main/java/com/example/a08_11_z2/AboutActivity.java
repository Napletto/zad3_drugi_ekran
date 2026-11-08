package com.example.a08_11_z2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "STOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.d(TAG, "AboutActivity onCreate");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "AboutActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AboutActivity onDestroy");
    }
}