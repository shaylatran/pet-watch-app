package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;


import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEt;
    private Button resetBtn;
    private ProgressBar progressbar;

    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEt = findViewById(R.id.etEmail);
        resetBtn = findViewById(R.id.btnReset);
        progressbar = findViewById(R.id.progressBar4);

        auth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        
        
    }

    private void resetPassword() {
        String email = emailEt.getText().toString().trim();

        if (email.isEmpty())
        {
            emailEt.setError("Email is required!");
            emailEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEt.setError("Please provide a valid email address!");
            emailEt.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Check your email to reset your password.", Toast.LENGTH_LONG).show();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Try again. Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}