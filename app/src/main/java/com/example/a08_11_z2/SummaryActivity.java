package com.example.a08_11_z2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    // klucze definiuje ekran, który dane ODBIERA
    public static final String EXTRA_NAME = "com.example.a08_11_z2.NAME";
    public static final String EXTRA_PIZZA = "com.example.a08_11_z2.PIZZA";
    public static final String EXTRA_SIZE = "com.example.a08_11_z2.SIZE";
    public static final String EXTRA_TOPPINGS = "com.example.a08_11_z2.TOPPINGS";
    public static final String EXTRA_QUANTITY = "com.example.a08_11_z2.QUANTITY";
    public static final String EXTRA_DELIVERY = "com.example.a08_11_z2.DELIVERY";
    public static final String EXTRA_ADDRESS = "com.example.a08_11_z2.ADDRESS";
    public static final String EXTRA_NUMBER = "com.example.a08_11_z2.NUMBER";

    private static final String TAG = "STOS";
    private TextView tvSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SummaryActivity onCreate");
        setContentView(R.layout.activity_summary);

        tvSummary = findViewById(R.id.tvSummary);
        tvSummary.setText(buildSummary());

        findViewById(R.id.btnEdit).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirm).setOnClickListener(v -> confirm());
        findViewById(R.id.btnShare).setOnClickListener(v -> share());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "SummaryActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SummaryActivity onDestroy");
    }

    private String buildSummary() {
        Intent intent = getIntent();

        String name = intent.getStringExtra(EXTRA_NAME);
        String pizza = intent.getStringExtra(EXTRA_PIZZA);
        String size = intent.getStringExtra(EXTRA_SIZE);
        String toppings = intent.getStringExtra(EXTRA_TOPPINGS);
        int quantity = intent.getIntExtra(EXTRA_QUANTITY, 0);
        boolean delivery = intent.getBooleanExtra(EXTRA_DELIVERY, false);
        String address = intent.getStringExtra(EXTRA_ADDRESS);

        String deliveryText = delivery
                ? getString(R.string.delivery_to, address)
                : getString(R.string.pickup);

        return getString(R.string.summary_format,
                name, pizza, size, toppings, quantity, deliveryText);
    }

    private void confirm() {
        String number = "PZ/" + (1000 + new Random().nextInt(9000));

        Intent result = new Intent();
        result.putExtra(EXTRA_NUMBER, number);
        setResult(RESULT_OK, result);
        finish();                      // dopiero finish odsyła wynik
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, tvSummary.getText().toString());

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}