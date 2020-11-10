package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Button logout;
    private ProgressBar progressBar3;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private TextView petAge, petBreed, petName, fullName, email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar3 = findViewById(R.id.progressBar3);

        logout = findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar3.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Signed out successfully!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView greetingTv = findViewById(R.id.tvWelcome);
        final TextView petNameTv = findViewById(R.id.tvPetNameInput);
        final TextView petBreedTv = findViewById(R.id.tvPetBreedInput);
        final TextView petAgeTv = findViewById(R.id.tvPetAgeInput);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);

                if (userProfile != null)
                {
                    String fullName = userProfile.fullname;
                    String email = userProfile.email;
                    String petName = userProfile.petname;
                    String petAge = userProfile.petage;
                    String petBreed = userProfile.petbreed;

                    greetingTv.setText("Welcome, " + fullName + "!");
                    petNameTv.setText(petName);
                    petAgeTv.setText(petAge);
                    petBreedTv.setText(petBreed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

        progressBar3.setVisibility(View.GONE);
    }
}