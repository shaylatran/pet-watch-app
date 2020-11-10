package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText eEmail;
    private EditText ePassword;
    private Button eLogin;
    private TextView register;
    private ProgressBar progressBar;
    private TextView forgotPassword;

    boolean isValid = false;
    private int counter = 3;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind them to the XML layout

        // findViewById attaches the particular value to the particular XML element
        eEmail = findViewById(R.id.etEmail);
        ePassword = findViewById(R.id.etPassword);
        eLogin = findViewById(R.id.btnLogin);
        register = findViewById(R.id.tvRegister);
        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        forgotPassword = findViewById(R.id.tvForgotPassword);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.tvRegister:
                        startActivity(new Intent(MainActivity.this, RegisterUser.class));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + v.getId());
                }
            }




        });

        eLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.btnLogin:
                    {
                        userLogin();
                        break;
                    }
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.tvForgotPassword:
                        startActivity(new Intent(MainActivity.this, ForgotPassword.class));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + v.getId());
                }
            }
        });

    }

    private void userLogin() {
        String email = eEmail.getText().toString().trim();
        String password = ePassword.getText().toString().trim();

        if (email.isEmpty())
        {
            eEmail.setError("Email is required!");
            eEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            eEmail.setError("Please enter a valid email address!");
            eEmail.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            ePassword.setError("Email is required!");
            ePassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    // direct to user profile
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    if (user.isEmailVerified())
                    {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        Toast.makeText(getApplicationContext(), "Login credentials was successful!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    else
                    {
                        user.sendEmailVerification();
                        Toast.makeText(getApplicationContext(), "Please check your email to verify your account.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                    }
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Failed to login! Recheck your credentials.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}