package com.vlada.selfie_app;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {
    
    
    private EditText etName;
    private EditText etDescription;
    private Button btnSetTime;
    
    private Calendar reminderTime = Calendar.getInstance();
    ;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create new event");
        setSupportActionBar(toolbar);
        
        
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        
        
        // initial reminder time setup
        
        btnSetTime = findViewById(R.id.btnSetTime);
        
        updateReminderTimeText();
    }
    
    /**
     * Updates btnSetTime text
     */
    private void updateReminderTimeText() {
        btnSetTime.setText(DateUtils.formatDateTime(this,
                reminderTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_event_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.itemDone:
                saveNewEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    /**
     * Creates new event in db and finishes the activity in case of success
     */
    private void saveNewEvent() {
        Toast.makeText(this, "in saveNewEvent:\nname = " + etName.getText()
                + "reminder time = " + btnSetTime.getText(), Toast.LENGTH_SHORT).show();
        
        // TODO add new selfie-Event in database
        
        
        finish();
    }
    
    
    public void btnSetTimeClick(View v) {
        new TimePickerDialog(CreateEventActivity.this, onTimeSetListener,
                reminderTime.get(Calendar.HOUR_OF_DAY),
                reminderTime.get(Calendar.MINUTE), true)
                .show();
    }
    
    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            reminderTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            reminderTime.set(Calendar.MINUTE, minute);
            updateReminderTimeText();
        }
    };
}
