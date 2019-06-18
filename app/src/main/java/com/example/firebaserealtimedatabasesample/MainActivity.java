package com.example.firebaserealtimedatabasesample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firebaserealtimedatabasesample.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText nameText, emailText;
    private TextView displayText;
    private Button btnSaved;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Displaying toolbar icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        nameText = findViewById(R.id.etName);
        emailText = findViewById(R.id.etEmail);
        displayText = findViewById(R.id.tvDisplay);
        btnSaved = findViewById(R.id.btnSaved);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        myRef = firebaseDatabase.getReference("users");

        //store app title to 'app_title' node
        firebaseDatabase.getReference("app_title").setValue("Realtime Database");

        // app_title change listener
        firebaseDatabase.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                //update toolbar title
                getSupportActionBar().setTitle(appTitle);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", databaseError.toException());

            }
        });


        //Save / Update the user
        btnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameText.getText().toString();
                String email = emailText.getText().toString();

                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, email);
                } else {
                    updateUser(name,email);
                }
            }
        });

        toggleButton();
    }
    // Changing button text
    private void toggleButton() {

        if (TextUtils.isEmpty(userId)){
            btnSaved.setText("Save");
        } else {
            btnSaved.setText("Update");
        }

    }

    private void createUser(String name, String email) {

        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth

        if (TextUtils.isEmpty(userId)){
            userId = myRef.push().getKey();
        }

        User user = new User(name,email);
        myRef.child(userId).setValue(user);

        addUserChangeListener();

    }

    /**
     * User data change listener
     */

    private void addUserChangeListener() {
        // User data change listener
        myRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                Log.e(TAG, "User data is changed!" + user.name + ", " + user.email);

                // Display newly updated name and email
                displayText.setText(user.name + "," + user.email);

                //clear edit text

                emailText.setText("");
                nameText.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Failed to read value
                Log.e(TAG, "Failed to read user", databaseError.toException());

            }
        });


    }

    private void updateUser(String name, String email) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name)){
            myRef.child(userId).child("name").setValue(name);
        }
        if (!TextUtils.isEmpty(email)){
            myRef.child(userId).child("email").setValue(email);
        }

    }


}
