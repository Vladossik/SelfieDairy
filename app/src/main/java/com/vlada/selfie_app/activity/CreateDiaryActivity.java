package com.vlada.selfie_app.activity;

import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vlada.selfie_app.PasswordService;
import com.vlada.selfie_app.R;
import com.vlada.selfie_app.database.entity.Diary;
import com.vlada.selfie_app.enums.RemindFrequency;
import com.vlada.selfie_app.notification.NotificationScheduler;
import com.vlada.selfie_app.utils.BooleanCallback;

import java.io.Serializable;
import java.util.Calendar;

public class CreateDiaryActivity extends AppCompatActivity {
    
    
    private EditText etName;
    private EditText etDescription;
    private Button btnReminderTime;
    private Spinner spnRemindFrequency;
    private Switch swchPrivate;
    
    private PasswordService passwordService;
    
    /**
     * True if the password was entered in MainActivity
     */
    private boolean passwordEntered;
    /**
     * Diary, which has been sent for editing or new created diary.
     */
    Diary diary;
    private boolean inEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create new diary");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        
        passwordService = new PasswordService(this);
        
        passwordEntered = getIntent().getBooleanExtra("passwordEntered", false);
        
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        
        
        // initial reminder time setup
        
        btnReminderTime = findViewById(R.id.btnReminderTime);
        
        // remindTime spinner setup
        spnRemindFrequency = findViewById(R.id.spnRemindFrequency);
        spnRemindFrequency.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, RemindFrequency.values()));
        spnRemindFrequency.setPrompt("Frequency");
        spnRemindFrequency.setSelection(0);
        
        
        swchPrivate = findViewById(R.id.swchPrivate);
        
        // check if we have old diary to edit
        
        if (getIntent().hasExtra("oldDiary")) {
            inEditMode = true;
            diary = (Diary) getIntent().getSerializableExtra("oldDiary");
            
            // restore all values from old Diary
            
            etName.setText(diary.getName());
            etDescription.setText(diary.getDescription());
            
            spnRemindFrequency.setSelection(diary.getRemindFrequency().ordinal());
            
            swchPrivate.setChecked(diary.isPrivate());
        } else {
            diary = new Diary();
        }
        
        updateReminderTimeText();
    }
    
    /**
     * Updates btnReminderTime text
     */
    private void updateReminderTimeText() {
        btnReminderTime.setText(DateUtils.formatDateTime(this,
                diary.getReminder().getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_diary_menu, menu);
        
        // change color for icon 0
        Drawable myIcon = menu.getItem(0).getIcon();
        myIcon.mutate();
        myIcon.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemDone:
                saveDiary();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Builds diary and returns it in case of success to result intent and finishes activity.
     */
    private void saveDiary() {
        
        if (etName.getText().toString().equals("")) {
            Toast.makeText(this, "Name shouldn't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        final Intent intent = new Intent();
        
        boolean privacyChanged = diary.isPrivate() != swchPrivate.isChecked();
        if (privacyChanged) {
            intent.putExtra("privacyChanged", true);
        }
    
        // (if we had changed diary.isPrivate here we would have broken privacyChanged checker after unsuccessful password request)
        
        // if we should ask password
        if (swchPrivate.isChecked() && !passwordEntered && (privacyChanged || !inEditMode)) {
            passwordService.askPasswordOrCreate(new BooleanCallback() {
                @Override
                public void onResult(boolean result) {
                    // if password entered or created correctly
                    if (result) {
                        intent.putExtra("passwordEntered", true);
                        continueSavingDiary(intent);
                    }
                }
            });
        } else {
            continueSavingDiary(intent);
        }
    }
    
    private void continueSavingDiary(Intent intent) {
        
        // update diary fields
        diary.setName(etName.getText().toString());
        diary.setDescription(etDescription.getText().toString());
        
        int selectedFrequency = spnRemindFrequency.getSelectedItemPosition();
        diary.setRemindFrequency(RemindFrequency.values()[selectedFrequency]);
        // Reminder time is updated in time picker handler
        
        // privacy should be changed only just before saving!!
        // because we compare it with switch to check for default value
        diary.setPrivate(swchPrivate.isChecked());
        intent.putExtra("diary", (Serializable) diary);
        // we shouldn't ask password.
        setResult(RESULT_OK, intent);
        finish();
    }
    
    
    public void btnReminderTimeClick(View v) {
        new TimePickerDialog(CreateDiaryActivity.this, onTimeSetListener,
                diary.getReminder().get(Calendar.HOUR_OF_DAY),
                diary.getReminder().get(Calendar.MINUTE), true)
                .show();
    }
    
    /**
     * Time picker choice handler
     */
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            diary.getReminder().set(Calendar.HOUR_OF_DAY, hourOfDay);
            diary.getReminder().set(Calendar.MINUTE, minute);
            updateReminderTimeText();
        }
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        passwordService.onActivityResult(requestCode, resultCode, data);
    }
}
