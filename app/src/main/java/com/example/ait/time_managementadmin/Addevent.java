package com.example.ait.time_managementadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by alialsaeedi on 11/18/16.
 */

public class Addevent extends AppCompatActivity implements View.OnClickListener {
    private TextView eventTitle;
    private TextView eventDetail;
    private Button pickTime;

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private static final String TAG = "EmailPassword";
    private Toolbar toolbar;

//    @Override
//    public void onBackPressed() {
//
//    }
//
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent);
        eventTitle = (TextView) findViewById(R.id.EventTitle);
        eventDetail = (TextView) findViewById(R.id.EventDetails);
        pickTime = (Button) findViewById(R.id.PickTime);
        pickTime.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    firebaseUser = firebaseAuth.getCurrentUser();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
//        hideKeyboardFrom(null,eventDetail);
//        hideKeyboardFrom(null , eventTitle);

    }


    @Override
    public void onClick(View view) {
        if (view == pickTime) {
            if (validate()) {
              //  finish();
                Intent pickTime = new Intent(Addevent.this, PickDate.class);
                pickTime.putExtra("eventTitle", eventTitle.getText().toString());
                pickTime.putExtra("eventDetail", eventDetail.getText().toString());
                startActivity(pickTime);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        authStateListener = null;
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void createDataBaseOnline() {


    }

    private boolean validate()

    {
        boolean value = true;

        if (TextUtils.isEmpty(eventDetail.getText().toString())) {
            value = false;
            eventDetail.setError("Required!");
        }
        if (TextUtils.isEmpty(eventTitle.getText().toString())) {
            value = false;
            eventTitle.setError("Required!");
        }
        return value;
    }

    public void saveEvent() {
        String event = eventTitle.getText() + "--" + eventDetail.getText();


    }

}
