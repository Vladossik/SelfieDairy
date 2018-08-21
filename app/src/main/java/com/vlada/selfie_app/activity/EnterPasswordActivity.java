package com.vlada.selfie_app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vlada.selfie_app.PasswordService;
import com.vlada.selfie_app.R;

public class EnterPasswordActivity extends AppCompatActivity {
    
    private static final int CREATE_PASSWORD_REQUEST = 252;
    EditText etPassword;
    Button btnEnter;
    String correctPassword;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        
        correctPassword = getIntent().getStringExtra("password");
        
        etPassword = findViewById(R.id.etPassword);
        btnEnter = findViewById(R.id.btnEnter);
    }
    
    public void btnEnterClick(View v) {
        String text = etPassword.getText().toString();
        
        if (!text.equals(correctPassword)) {
            Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        setResult(RESULT_OK);
        finish();
    }
}
