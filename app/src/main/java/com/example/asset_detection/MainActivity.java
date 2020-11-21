package com.example.asset_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "EmailPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
               firebaseAuth.signInWithEmailAndPassword(email, password)
                       .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task)
                           {
                               if(task.isSuccessful()){
                               //    Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(getApplicationContext(),Console.class));
                               }
                               else {
                                   Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                   Toast.makeText(MainActivity.this, "Authentication failed.",
                                           Toast.LENGTH_SHORT).show();
                               }

                           }
                       });
            }

        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Signup.class));
            }
        });
    }
}