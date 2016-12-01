package com.example.ait.time_managementadmin;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class PickDate extends AppCompatActivity implements
        View.OnClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private TextView timeTextView;
    private TextView dateTextView;
    private CheckBox mode24Hours;
    private CheckBox modeDarkTime;
    private CheckBox modeDarkDate;
    private CheckBox modeCustomAccentTime;
    private CheckBox modeCustomAccentDate;
    private CheckBox vibrateTime;
    private CheckBox vibrateDate;
    private CheckBox dismissTime;
    private CheckBox dismissDate;
    private CheckBox titleTime;
    private CheckBox titleDate;
    private CheckBox showYearFirst;
    private CheckBox enableSeconds;
    private CheckBox enableMinutes;
    private CheckBox limitTimes;
    private CheckBox limitDates;
    private CheckBox disableDates;
    private CheckBox highlightDates;
    private String dateString;
    private String timeString;
    private String hourTime;
    private String minuteTime;
    private String secondTime;
    private String yearDate;
    private String monthDate;
    private String dayDate;


    private String eventTitle;
    private String eventDetail;
    private Toolbar toolbar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuthListener;
    private FirebaseUser user;
    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickdate);
        //getEvent();
        // Find our View instances
        timeTextView = (TextView) findViewById(R.id.time_textview);
        dateTextView = (TextView) findViewById(R.id.date_textview);
        Button timeButton = (Button) findViewById(R.id.time_button);
        Button dateButton = (Button) findViewById(R.id.date_button);
        mode24Hours = (CheckBox) findViewById(R.id.mode_24_hours);
        modeDarkTime = (CheckBox) findViewById(R.id.mode_dark_time);
        modeDarkDate = (CheckBox) findViewById(R.id.mode_dark_date);
        modeCustomAccentTime = (CheckBox) findViewById(R.id.mode_custom_accent_time);
        modeCustomAccentDate = (CheckBox) findViewById(R.id.mode_custom_accent_date);
        vibrateTime = (CheckBox) findViewById(R.id.vibrate_time);
        vibrateDate = (CheckBox) findViewById(R.id.vibrate_date);
        dismissTime = (CheckBox) findViewById(R.id.dismiss_time);
        dismissDate = (CheckBox) findViewById(R.id.dismiss_date);
        titleTime = (CheckBox) findViewById(R.id.title_time);
        titleDate = (CheckBox) findViewById(R.id.title_date);
        showYearFirst = (CheckBox) findViewById(R.id.show_year_first);
        enableSeconds = (CheckBox) findViewById(R.id.enable_seconds);
        enableMinutes = (CheckBox) findViewById(R.id.enable_minutes);
        limitTimes = (CheckBox) findViewById(R.id.limit_times);
        limitDates = (CheckBox) findViewById(R.id.limit_dates);
        disableDates = (CheckBox) findViewById(R.id.disable_dates);
        highlightDates = (CheckBox) findViewById(R.id.highlight_dates);
        timeString = new String();
        dateString = new String();
        eventDetail = new String();
        eventTitle = new String();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addConfermation(view);
                }


            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventTitle = extras.getString("eventTitle");
            eventDetail = extras.getString("eventDetail");
        }
        mAuthListener = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        user = mAuthListener.getCurrentUser();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
        // Check if picker mode is specified in Style.xml
        modeDarkTime.setChecked(Utils.isDarkTheme(this, modeDarkTime.isChecked()));
        modeDarkDate.setChecked(Utils.isDarkTheme(this, modeDarkDate.isChecked()));

        // Ensure a consistent state between enableSeconds and enableMinutes
        enableMinutes.setOnClickListener(this);
        enableSeconds.setOnClickListener(this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        PickDate.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        mode24Hours.isChecked()
                );
                tpd.setThemeDark(modeDarkTime.isChecked());
                tpd.vibrate(vibrateTime.isChecked());
                tpd.dismissOnPause(dismissTime.isChecked());
                tpd.enableSeconds(enableSeconds.isChecked());
                tpd.enableMinutes(enableMinutes.isChecked());
                if (modeCustomAccentTime.isChecked()) {
                    tpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (titleTime.isChecked()) {
                    tpd.setTitle("TimePicker Title");
                }
                if (limitTimes.isChecked()) {
                    tpd.setTimeInterval(2, 5, 10);
                }
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PickDate.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(modeDarkDate.isChecked());
                dpd.vibrate(vibrateDate.isChecked());
                dpd.dismissOnPause(dismissDate.isChecked());
                dpd.showYearPickerFirst(showYearFirst.isChecked());
                if (modeCustomAccentDate.isChecked()) {
                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
                }
                if (titleDate.isChecked()) {
                    dpd.setTitle("DatePicker Title");
                }
                if (limitDates.isChecked()) {
                    Calendar[] dates = new Calendar[13];
                    for (int i = -6; i <= 6; i++) {
                        Calendar date = Calendar.getInstance();
                        date.add(Calendar.MONTH, i);
                        dates[i + 6] = date;
                    }
                    dpd.setSelectableDays(dates);
                }
                if (highlightDates.isChecked()) {
                    Calendar[] dates = new Calendar[13];
                    for (int i = -6; i <= 6; i++) {
                        Calendar date = Calendar.getInstance();
                        date.add(Calendar.WEEK_OF_YEAR, i);
                        dates[i + 6] = date;
                    }
                    dpd.setHighlightedDays(dates);
                }
                if (disableDates.isChecked()) {
                    Calendar[] dates = new Calendar[3];
                    for (int i = -1; i <= 1; i++) {
                        Calendar date = Calendar.getInstance();
                        date.add(Calendar.DAY_OF_MONTH, i);
                        dates[i + 1] = date;
                    }
                    dpd.setDisabledDays(dates);
                }
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (enableSeconds.isChecked() && view.getId() == R.id.enable_seconds)
            enableMinutes.setChecked(true);
        if (!enableMinutes.isChecked() && view.getId() == R.id.enable_minutes)
            enableSeconds.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if (tpd != null) tpd.setOnTimeSetListener(this);
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        hourTime = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        minuteTime = minute < 10 ? "0" + minute : "" + minute;
        secondTime = second < 10 ? "0" + second : "" + second;
        timeString = hourTime + "h" + minuteTime + "m" + secondTime + "s";
        timeString = hourTime + "h" + minuteTime + "m" + secondTime + "s";
        String time = "You picked the following time: " + timeString;
        timeTextView.setText(time);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        yearDate = year + "";
        monthDate = (++monthOfYear) + "";
        dayDate = dayOfMonth + "";

        dateString = dayDate + "/" + monthDate + "/" + yearDate;
        String date = "You picked the following date: " + dateString;
        dateTextView.setText(date);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

//    public void getEvent() {
//        TextView eventT = (TextView) findViewById(R.id.EventTitle);
//        TextView eventD = (TextView) findViewById(R.id.EventDetails);
//        String event = eventT.getText() + "--" + eventD.getText();
//        System.out.print(" ");
//    }

    private boolean validate() {
        boolean value = true;
        if (dayDate == null || monthDate == null || dayDate == null) {
            dateTextView.setError("Please enter the date first");
            value = false;

        }
        if (hourTime == null || minuteTime == null || secondTime == null) {
            timeTextView.setError("Please enter the Time first");
            value = false;

        }
        return value;
    }

    private void addConfermation(View view) {

        Snackbar snackbar = Snackbar.make(view, "are you sure you want to add this event?", Snackbar.LENGTH_LONG)
                .setAction("Add", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveToDataBase();
                    }
                });
        snackbar.show();
    }

    public String getDate()

    {

        return dateString;
    }

    public String getTime()

    {

        return timeString;
    }

    public void saveToDataBase() {
        DatabaseReference myref = databaseReference.child(user.getUid());
        //myref.child("Events").setValue(yearDate + "/" + monthDate + "/" + dayDate  + " : "+ timeString+ "-"  +  eventTitle);
        //myref.child("Events").child(yearDate + " " + monthDate + " " + dayDate).setValue(" : " + timeString + "-" + eventTitle);
        myref.child("Events").child(yearDate + " " + monthDate + " " + dayDate).child(timeString).setValue(eventTitle + "_" +eventDetail);
    }

}