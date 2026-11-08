package com.example.a08_11_z2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    // rejestracja wyniku jako pole klasy - musi powstać przed STARTED
    private final ActivityResultLauncher<Intent> summaryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String number = result.getData()
                            .getStringExtra(SummaryActivity.EXTRA_NUMBER);
                    clearForm();                                        // zamówienie poszło
                    showMessage(getString(R.string.registered, number));
                } else {
                    showMessage(getString(R.string.cancelled));         // RESULT_CANCELED
                }
            });

    private EditText etName, etAddress;
    private Spinner spPizza;
    private RadioGroup rgSize;
    private CheckBox cbCheese, cbMushrooms, cbOlives;
    private SeekBar sbQuantity;
    private TextView tvQuantityValue;
    private MaterialSwitch swDelivery;
    private LinearLayout llAddress;
    private Button btnOrder, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        spPizza = findViewById(R.id.spPizza);
        rgSize = findViewById(R.id.rgSize);
        cbCheese = findViewById(R.id.cbCheese);
        cbMushrooms = findViewById(R.id.cbMushrooms);
        cbOlives = findViewById(R.id.cbOlives);
        sbQuantity = findViewById(R.id.sbQuantity);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        swDelivery = findViewById(R.id.swDelivery);
        llAddress = findViewById(R.id.llAddress);
        btnOrder = findViewById(R.id.btnOrder);
        btnClear = findViewById(R.id.btnClear);

        setUpPizzaSpinner();
        setUpListeners();
        tvQuantityValue.setText(String.valueOf(sbQuantity.getProgress()));
    }

    private void setUpPizzaSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.pizzas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPizza.setAdapter(adapter);
    }

    private void setUpListeners() {
        // zależność: przełącznik dostawy pokazuje i ukrywa pole adresu
        swDelivery.setOnCheckedChangeListener((buttonView, isChecked) ->
                llAddress.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        sbQuantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvQuantityValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnOrder.setOnClickListener(v -> {
            if (validateForm()) {
                openSummary();
            }
        });
        btnClear.setOnClickListener(v -> askForClear());
        findViewById(R.id.btnAbout).setOnClickListener(
                v -> startActivity(new Intent(this, AboutActivity.class)));
    }

    private void askForClear() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_title)
                .setMessage(R.string.clear_message)
                .setPositiveButton(R.string.clear_yes, (dialog, which) -> clearFormWithUndo())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // "zdjęcie" stanu formularza, żeby dało się go odtworzyć
    private static class FormState {
        String name, address;
        int pizza, checkedId, quantity;
        boolean cheese, mushrooms, olives, delivery;
    }

    private FormState saveState() {
        FormState s = new FormState();
        s.name = etName.getText().toString();
        s.address = etAddress.getText().toString();
        s.pizza = spPizza.getSelectedItemPosition();
        s.checkedId = rgSize.getCheckedRadioButtonId();     // -1 = nic nie zaznaczono
        s.quantity = sbQuantity.getProgress();
        s.cheese = cbCheese.isChecked();
        s.mushrooms = cbMushrooms.isChecked();
        s.olives = cbOlives.isChecked();
        s.delivery = swDelivery.isChecked();
        return s;
    }

    private void restoreState(FormState s) {
        etName.setText(s.name);
        etAddress.setText(s.address);
        spPizza.setSelection(s.pizza);
        if (s.checkedId != -1) {
            rgSize.check(s.checkedId);
        }
        sbQuantity.setProgress(s.quantity);
        cbCheese.setChecked(s.cheese);
        cbMushrooms.setChecked(s.mushrooms);
        cbOlives.setChecked(s.olives);
        swDelivery.setChecked(s.delivery);                  // listener sam pokaże adres
    }

    private void clearFormWithUndo() {
        FormState previous = saveState();       // najpierw zapamiętujemy, potem czyścimy
        clearForm();

        Snackbar.make(findViewById(R.id.main), R.string.cleared, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> restoreState(previous))
                .show();
    }

    private void clearForm() {
        etName.setText("");
        etName.setError(null);

        etAddress.setText("");
        etAddress.setError(null);

        spPizza.setSelection(0);
        rgSize.clearCheck();

        cbCheese.setChecked(false);
        cbMushrooms.setChecked(false);
        cbOlives.setChecked(false);

        sbQuantity.setProgress(0);
        swDelivery.setChecked(false);       // listener sam ukryje pole adresu
    }

    private boolean validateForm() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError(getString(R.string.error_name));
            etName.requestFocus();
            return false;
        }

        if (spPizza.getSelectedItemPosition() == 0) {       // pozycja 0 = brak wyboru
            Toast.makeText(this, R.string.error_pizza, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rgSize.getCheckedRadioButtonId() == -1) {       // -1 = nic nie zaznaczono
            Toast.makeText(this, R.string.error_size, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sbQuantity.getProgress() == 0) {
            Toast.makeText(this, R.string.error_quantity, Toast.LENGTH_SHORT).show();
            return false;
        }

        // adres sprawdzamy tylko, gdy włączona jest dostawa
        if (swDelivery.isChecked()
                && etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError(getString(R.string.error_address));
            etAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void openSummary() {
        RadioButton checkedSize = findViewById(rgSize.getCheckedRadioButtonId());

        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.EXTRA_NAME, etName.getText().toString().trim());
        intent.putExtra(SummaryActivity.EXTRA_PIZZA, spPizza.getSelectedItem().toString());
        intent.putExtra(SummaryActivity.EXTRA_SIZE, checkedSize.getText().toString());
        intent.putExtra(SummaryActivity.EXTRA_TOPPINGS, getToppings());
        intent.putExtra(SummaryActivity.EXTRA_QUANTITY, sbQuantity.getProgress());
        intent.putExtra(SummaryActivity.EXTRA_DELIVERY, swDelivery.isChecked());
        intent.putExtra(SummaryActivity.EXTRA_ADDRESS, etAddress.getText().toString().trim());

        summaryLauncher.launch(intent);        // zamiast startActivity
    }

    private String getToppings() {
        StringBuilder sb = new StringBuilder();
        if (cbCheese.isChecked()) {
            sb.append(cbCheese.getText()).append(", ");
        }
        if (cbMushrooms.isChecked()) {
            sb.append(cbMushrooms.getText()).append(", ");
        }
        if (cbOlives.isChecked()) {
            sb.append(cbOlives.getText()).append(", ");
        }

        if (sb.length() == 0) {
            return getString(R.string.none);
        }
        return sb.substring(0, sb.length() - 2);    // obcinamy ostatni ", "
    }

    private void showMessage(String text) {
        Snackbar.make(findViewById(R.id.main), text, Snackbar.LENGTH_LONG).show();
    }
}