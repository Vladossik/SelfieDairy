package com.vlada.selfie_app;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vlada.selfie_app.activity.CreatePasswordActivity;
import com.vlada.selfie_app.activity.EnterPasswordActivity;
import com.vlada.selfie_app.activity.MainActivity;
import com.vlada.selfie_app.utils.BooleanCallback;

import java.util.HashMap;
import java.util.Map;

public class PasswordService {
    
    private FragmentActivity activity;
    private static final int ASK_OR_CREATE_REQUEST = 788;
    private static final int REPLACE_PASSWORD_REQUEST = 28;
    private static final int ASK_BEFORE_REPLACE_REQUEST = 92;
    
    private BooleanCallback lastCallback;
    
    private Map<Integer, BooleanCallback> callbackMap = new HashMap<>();
    
    
    public PasswordService(FragmentActivity activity) {
        this.activity = activity;
    }
    
    public @Nullable String getPassword() {
        SharedPreferences settings = activity.getSharedPreferences("settings", 0);
        return settings.getString("password", null);
    }
    
    
    public boolean hasPassword() {
        return getPassword() != null;
    }
    
    private void removePassword() {
        SharedPreferences settings = activity.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("password");
        editor.apply();
    }
    
    public void deletePasswordWithDialog(final @Nullable Runnable onSuccess) {
        String[] items = new String[]{"I agree, that all my private diaries will be deleted."};
        final boolean[] checkedItems = new boolean[]{false};
        
        new AlertDialog.Builder(activity)
                .setTitle("Password reset.")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                })
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user is not sure
                        if (!checkedItems[0]) {
                            Toast.makeText(activity, "Canceled.\nYou need to tap on the checkbox to succeed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        removePassword();
                        ViewModelProviders.of(activity).get(ViewModel.class)
                                .getRepo().deleteAllPrivateDiaries(activity, onSuccess);
                        
                    }
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }
    
    public void createPassword(String password) {
        SharedPreferences settings = activity.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("password", password);
        editor.commit();
    }
    
    /** Should be invoked in activity in onActivityResult to handle callbacks*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackMap.containsKey(requestCode)) {
            callbackMap.get(requestCode).onResult(resultCode == Activity.RESULT_OK);
            callbackMap.remove(requestCode);
        }
        
        if (requestCode == ASK_BEFORE_REPLACE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // creating new password with REPLACE_PASSWORD_REQUEST
                Intent intent = new Intent(activity, CreatePasswordActivity.class);
                activity.startActivityForResult(intent, REPLACE_PASSWORD_REQUEST);
            } else {
                // if asking before replace failed,
                // send unsuccessful replace result to invoke callback
                onActivityResult(REPLACE_PASSWORD_REQUEST, Activity.RESULT_CANCELED, null);
            }
        }
    }
    
    
//    public interface AskOrCreateCallback {
//        void onResult(Boolean succeed, Boolean deletedPassword);
//    }
    
    public void askPasswordOrCreate(BooleanCallback booleanCallback) {
        callbackMap.put(ASK_OR_CREATE_REQUEST, booleanCallback);
        
        String password = getPassword();
        if (password == null) {
            Intent intent = new Intent(activity, CreatePasswordActivity.class);
            activity.startActivityForResult(intent, ASK_OR_CREATE_REQUEST);
        } else {
            Intent intent = new Intent(activity, EnterPasswordActivity.class);
            intent.putExtra("password", password);
            activity.startActivityForResult(intent, ASK_OR_CREATE_REQUEST);
        }
    }
    
    public void changePasswordOrCreate(BooleanCallback booleanCallback) {
        callbackMap.put(REPLACE_PASSWORD_REQUEST, booleanCallback);
        
        String password = getPassword();
        if (password == null) {
            // if there was no password, just creating new password
            Intent intent = new Intent(activity, CreatePasswordActivity.class);
            activity.startActivityForResult(intent, REPLACE_PASSWORD_REQUEST);
        } else {
            // checking old password before creating new
            Intent intent = new Intent(activity, EnterPasswordActivity.class);
            intent.putExtra("password", password);
            activity.startActivityForResult(intent, ASK_BEFORE_REPLACE_REQUEST);
        }
    }
}
