package com.example.ait.time_managementadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private Button signupButton;
    private EditText emailTextField;
    private EditText passwordTextField;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        progressDialog = new ProgressDialog(this);
        progressBar = new ProgressBar(this);

        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signupButton);
        emailTextField = (EditText) findViewById(R.id.emailTextField);
        passwordTextField = (EditText) findViewById(R.id.passwordTextField);

        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                visibility(true);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    updateUI(user);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
           // loginButton.setEnabled(false);
            registerUser();
        } else if (v == signupButton) {
            finish();
            Intent signup = new Intent(LoginActivity.this, SignUpActivity.class);
            //SignUpActivity.addFlags(getIntent().FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signup);
        }

    }

    private void visibility(boolean visible) {
        int value = 0;
        if (visible == true) {
            value = View.VISIBLE;
        } else {
            value = View.INVISIBLE;

        }
        loginButton.setVisibility(value);
        emailTextField.setVisibility(value);
        passwordTextField.setVisibility(value);
        signupButton.setVisibility(value);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuthListener = null;
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private boolean validate() {
        boolean valid = true;

        String email = emailTextField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTextField.setError("Required.");
            valid = false;
        } else {
            emailTextField.setError(null);
        }

        String pass = passwordTextField.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            passwordTextField.setError("Required.");
            valid = false;
        } else {
            passwordTextField.setError(null);
        }

        return valid;
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    private void registerUser() {
        String email = emailTextField.getText().toString();
        String pass = passwordTextField.getText().toString();

        Log.d(TAG, "signIn:" + email);
        if (!validate()) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(passwordTextField.getWindowToken(), 0);

        progressDialog.setMessage("Signing In...");
        progressDialog.show();

        visibility(false);
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(LoginActivity.this, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                    visibility(true);
                } else {
                    updateUI(firebaseAuth.getCurrentUser());
                }

                progressDialog.hide();
                visibility(true);
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        visibility(false);
        if (!(progressDialog.isShowing())) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (user != null) {
            // Read from the database
            //visibility(true);
            //progressBar.set
            String uid = user.getUid().toString();
            DatabaseReference myRef = databaseReference.child(uid);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        Log.d(TAG, "User Signed in");
                        finish();
                        Intent i = new Intent(LoginActivity.this, MainBoardUser.class);
                        startActivity(i);
                    } else {
                        if (firebaseAuth.getCurrentUser() != null) {
                            firebaseAuth.signOut();
                            // visibility(true);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                    if (firebaseAuth.getCurrentUser() != null) {
                        firebaseAuth.signOut();
                    }
                }
            });
        }
    }


}
