package com.vlada.selfie_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vlada.selfie_app.R;

public class CreatePasswordDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private final Runnable onSuccess;
    
    public CreatePasswordDialog(@NonNull Context context, Runnable onSuccess) {
        super(context);
        this.context = context;
        this.onSuccess = onSuccess;
    }
    EditText etPassword, etPasswordCopy;
    Button btnSave;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_password);
        
        etPassword = findViewById(R.id.etPassword);
        etPasswordCopy = findViewById(R.id.etPasswordCopy);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View v) {
        String password = etPassword.getText().toString();
        String passwordCopy = etPasswordCopy.getText().toString();
        
        if (password.equals("") || passwordCopy.equals("")) {
            //there is no password
            Toast.makeText(context, "No password entered!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!password.equals(passwordCopy)) {
            //no match on the passwords
            Toast.makeText(context, "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        //save the password
        SharedPreferences settings = context.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("password", password);
        editor.apply();
    
        dismiss();
        onSuccess.run();
    }
}
