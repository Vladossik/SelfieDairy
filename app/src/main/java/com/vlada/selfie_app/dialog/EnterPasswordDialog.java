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

public class EnterPasswordDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private final Runnable onSuccess;
    
    EditText etPassword;
    Button btnEnter;
    String restoredPassword;
    
    public EnterPasswordDialog(@NonNull Context context, Runnable onSuccess) {
        super(context);
        this.context = context;
        this.onSuccess = onSuccess;
    }
    
    @Override
    public void show() {
    
        //load the password or open CreatePasswordDialog
        SharedPreferences settings = context.getSharedPreferences("settings", 0);
        if (!settings.contains("password")) {
            dismiss();
            new CreatePasswordDialog(context, onSuccess).show();
            return;
        }
        restoredPassword = settings.getString("password", "");
        
        super.show();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_enter_password);
        
        etPassword = findViewById(R.id.editText);
        btnEnter = findViewById(R.id.btnEnter);
        btnEnter.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        String text = etPassword.getText().toString();
        
        if (!text.equals(restoredPassword)) {
            Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        dismiss();
        onSuccess.run();
    }
}
