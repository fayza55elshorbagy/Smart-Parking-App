package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class complain extends AppCompatActivity {


     EditText Feedback;
     DatabaseReference databaseReference;
     FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        Feedback = (EditText) findViewById(R.id.feedBack);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void send(View view) {
        String feed = Feedback.getText().toString();
        databaseReference.child("ParkirApp").child("users").child("FCI_Garage").child(firebaseAuth.getCurrentUser().getUid()).child("FeedBack").setValue(feed);
        Toast.makeText(this,"Thanks",Toast.LENGTH_LONG).show();
    }
}
