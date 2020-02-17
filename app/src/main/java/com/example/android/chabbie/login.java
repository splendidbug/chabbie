package com.example.android.chabbie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {

    EditText phone, otp;
    Button sendOtp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        sendOtp=findViewById(R.id.sendOtp);

        sendOtp.setEnabled(false);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(phone.getText().toString().length()==10){
                    sendOtp.setAlpha(1);
                    sendOtp.setEnabled(true);
                }
                else{
                        sendOtp.setAlpha(0.7f);
                        sendOtp.setEnabled(false);
                    }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}
