package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        if(currentuser == null){
            Intent RegisterIntent = new Intent(SplashActivity.this, Login_Register_ResetPasswordActivity.class);
            startActivity(RegisterIntent);
            finish();
        }
        else {
            FirebaseFirestore.getInstance().collection("USERS").document(currentuser.getUid()).update("Last seen", FieldValue.serverTimestamp())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent MainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(MainIntent);
                                finish();
                            }else {
                                String error=task.getException().getMessage();
                                Toast.makeText(SplashActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}