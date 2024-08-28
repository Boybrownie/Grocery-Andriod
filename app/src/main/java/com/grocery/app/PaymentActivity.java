package com.grocery.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private EditText etCardNumber, etCvv, etExpiry;
    private ProgressBar progressBar;
    private static final String EXTRA_PRICE = "extra_price";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        etCardNumber = findViewById(R.id.et_card_number);
        etCvv = findViewById(R.id.et_cvv);
        etExpiry = findViewById(R.id.et_expiry);
        TextView tvPrice = findViewById(R.id.tv_price);
        progressBar = findViewById(R.id.progress_bar);

        Button btnPay = findViewById(R.id.btn_pay);
        Button btnCancel = findViewById(R.id.btn_cancel);

        // Retrieve price from intent
        Intent intent = getIntent();
        long price = intent.getLongExtra(EXTRA_PRICE, 25);
        tvPrice.setText("Total Price: CAD " + String.format("%.2f", (double) price));

        // Set text watchers
        etCardNumber.addTextChangedListener(new CardNumberTextWatcher());
        etCvv.addTextChangedListener(new CvvTextWatcher());
        etExpiry.addTextChangedListener(new ExpiryDateTextWatcher());

        btnPay.setOnClickListener(v -> {
            String cardNumber = etCardNumber.getText().toString().trim();
            String cvv = etCvv.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();

            if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(cvv) || TextUtils.isEmpty(expiry)) {
                Toast.makeText(PaymentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (cardNumber.replaceAll(" ", "").length() != 16) { // Considering space in format
                Toast.makeText(PaymentActivity.this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
            } else {
                // Start payment process
                processPayment();
            }
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void processPayment() {
        // Show progress bar
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Simulate payment processing
        new android.os.Handler().postDelayed(() -> {
            // Hide progress bar
            progressBar.setVisibility(ProgressBar.GONE);

            // Simulate payment result
            Intent resultIntent = new Intent();
            resultIntent.putExtra("payment_status", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 2000); // Simulated delay for payment processing
    }

    private class CardNumberTextWatcher implements TextWatcher {
        private boolean isFormatting;

        public CardNumberTextWatcher() {
            this.isFormatting = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isFormatting) return;

            String text = s.toString().replaceAll(" ", "");
            if (text.length() > 0) {
                isFormatting = true;
                String formatted = text.substring(0, Math.min(text.length(), 16));
                StringBuilder builder = new StringBuilder(formatted);
                while (builder.length() < 16) {
                    builder.append(' ');
                }
                for (int i = 4; i < builder.length(); i += 5) {
                    builder.insert(i, ' ');
                }
                String result = builder.toString().trim();
                etCardNumber.setText(result);
                etCardNumber.setSelection(result.length());
                isFormatting = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class CvvTextWatcher implements TextWatcher {
        private boolean isFormatting;

        public CvvTextWatcher() {
            this.isFormatting = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isFormatting) return;

            String text = s.toString();
            if (text.length() > 3) {
                isFormatting = true;
                etCvv.setText(text.substring(0, 3));
                etCvv.setSelection(3);
                isFormatting = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class ExpiryDateTextWatcher implements TextWatcher {
        private boolean isFormatting;

        public ExpiryDateTextWatcher() {
            this.isFormatting = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isFormatting) return;

            String text = s.toString();
            if (text.length() == 2 && !text.contains("/")) {
                isFormatting = true;
                etExpiry.setText(text + "/");
                etExpiry.setSelection(3);
                isFormatting = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
