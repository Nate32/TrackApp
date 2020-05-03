package com.example.trackapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

public class RegisterPage extends AppCompatActivity {

    EditText mFullName, mEmail, mPassword, mPhone;
    Button mRegisterbtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar2;
    FirebaseFirestore fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterbtn = findViewById(R.id.JoinTrackBtn);
        mLoginBtn = findViewById(R.id.LogInTextBtn);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        progressBar2 = findViewById(R.id.progressBar);



        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String name = mFullName.getText().toString();


                if(TextUtils.isEmpty(email)){
                    mEmail.setError("email is not valid");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Must Enter Password");
                    return;
                }

                if(password.length() < 8){
                    mPassword.setError("Password must be greater than 8");
                    return;
                }

                progressBar2.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterPage.this, "User created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("email", email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   Log.d("TAG", "success");
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else{
                            Toast.makeText(RegisterPage.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });



        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });


    }
}

