package com.vlada.selfie_app.activity;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.vlada.selfie_app.R;
import com.vlada.selfie_app.database.entity.RemindFrequency;

import java.util.Calendar;

public class CreateDiaryActivity extends AppCompatActivity {
    
    
    private EditText etName;
    private EditText etDescription;
    private Button btnReminderTime;
    private Spinner spnRemindFrequency;
    
    private Calendar reminderTime = Calendar.getInstance();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create new diary");
        setSupportActionBar(toolbar);
        
        
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        
        
        // initial reminder time setup
        
        btnReminderTime = findViewById(R.id.btnReminderTime);
        
        updateReminderTimeText();
        
        // remindTime spinner setup
        spnRemindFrequency = findViewById(R.id.spnRemindFrequency);
        spnRemindFrequency.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, RemindFrequency.values()));
        spnRemindFrequency.setSelection(0);
        
    }
    
    /**
     * Updates btnReminderTime text
     */
    private void updateReminderTimeText() {
        btnReminderTime.setText(DateUtils.formatDateTime(this,
                reminderTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_diary_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemDone:
                saveNewDiary();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    /**
     * Creates new diary in db and finishes the activity in case of success
     */
    private void saveNewDiary() {
        Toast.makeText(this, "in saveNewDiary:\nname = " + etName.getText()
                + "reminder time = " + btnReminderTime.getText(), Toast.LENGTH_SHORT).show();
        
        // TODO add new selfie-Diary in database
        
        
        finish();
    }
    
    
    public void btnReminderTimeClick(View v) {
        new TimePickerDialog(CreateDiaryActivity.this, onTimeSetListener,
                reminderTime.get(Calendar.HOUR_OF_DAY),
                reminderTime.get(Calendar.MINUTE), true)
                .show();
    }
    
    /**
     * Time picker choice handler
     */
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            reminderTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            reminderTime.set(Calendar.MINUTE, minute);
            updateReminderTimeText();
        }
    };
}
