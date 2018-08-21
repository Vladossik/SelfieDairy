package com.vlada.selfie_app.activity;

import android.content.Intent;
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

public class CreatePasswordActivity extends AppCompatActivity {
    
    EditText etPassword, etPasswordCopy;
    Button btnSave;
    PasswordService passwordService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        
        etPassword = findViewById(R.id.etPassword);
        etPasswordCopy = findViewById(R.id.etPasswordCopy);
        btnSave = findViewById(R.id.btnSave);
        
        passwordService = new PasswordService(this);
        
        etPasswordCopy.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    btnSaveClick(null);
                }
                return false;
            }
        });
    }
    
    public void btnSaveClick(View v) {
        String password = etPassword.getText().toString();
        String passwordCopy = etPasswordCopy.getText().toString();
        
        if (password.equals("") || passwordCopy.equals("")) {
            //there is no password
            Toast.makeText(this, "No password entered!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(passwordCopy)) {
            //no match on the passwords
            Toast.makeText(this, "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        //save the password in sharedPrefs
        passwordService.createPassword(password);
        
        Intent intent = new Intent();
        intent.putExtra("password", password);
        setResult(RESULT_OK, intent);
        finish();
    }
    
}
