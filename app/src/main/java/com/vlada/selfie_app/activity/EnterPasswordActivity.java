package com.vlada.selfie_app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
        
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    btnEnterClick(null);
                }
                return false;
            }
        });
        
        findViewById(R.id.btnRemovePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PasswordService(EnterPasswordActivity.this).deletePasswordWithDialog(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
            }
        });
        
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
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
