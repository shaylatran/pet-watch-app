package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView PetWatchTitle, registerUser;
    private EditText Password, Email, FullName, PetName, PetBreed, PetAge;
    private ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        PetWatchTitle = (TextView) findViewById(R.id.tvPetWatch);
        PetWatchTitle.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        FullName = findViewById(R.id.fullName);
        Password = findViewById(R.id.password);
        Email = findViewById(R.id.email);
        PetName = findViewById(R.id.petName);
        PetBreed = findViewById(R.id.petBreed);
        PetAge = findViewById(R.id.petAge);
        progressBar2 = findViewById(R.id.progressBar2);




    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.tvPetWatch:
            {
                startActivity(new Intent(this, MainActivity.class));
                break;
            }

            case R.id.registerUser: {
                registerUser();
                break;
            }
        }



    }

    private void registerUser() {
        String user_email = Email.getText().toString().trim();
        String user_password = Password.getText().toString().trim();
        String fullname = FullName.getText().toString().trim();
        String petname = PetName.getText().toString().trim();
        String petbreed = PetBreed.getText().toString().trim();
        String petage = PetAge.getText().toString().trim();

        if (fullname.isEmpty())
        {
            FullName.setError("Full name is required!");
            FullName.requestFocus();
            return;
        }

        if (user_email.isEmpty())
        {
            Email.setError("Email is required!");
            Email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(user_email).matches())
        {
            Email.setError("Please provide a valid email address!");
            Email.requestFocus();
            return;
        }

        if (user_password.isEmpty())
        {
            Password.setError("Password is required!");
            Password.requestFocus();
            return;
        }

        if (petname.isEmpty())
        {
            PetName.setError("Pet name is required!");
            PetName.requestFocus();
            return;
        }

        if (petbreed.isEmpty())
        {
            PetBreed.setError("Pet breed is required!");
            PetBreed.requestFocus();
            return;
        }

        if (petage.isEmpty())
        {
            PetAge.setError("Pet age is required!");
            PetAge.requestFocus();
            return;
        }

        progressBar2.setVisibility(View.VISIBLE);


        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            User user = new User (fullname, user_email, petname, petbreed, petage);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(), "User has been registered.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                    }

                                    Toast.makeText(getApplicationContext(), "User registration failed.", Toast.LENGTH_LONG).show();
                                    progressBar2.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });

    }
}