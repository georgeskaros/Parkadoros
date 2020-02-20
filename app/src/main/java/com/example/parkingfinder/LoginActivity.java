package com.example.parkingfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    public EditText emailId, password;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btnLogin);
        signIn = findViewById(R.id.TVSignUp);
    }

    public void signIn(View view) {
        String mail = emailId.getText().toString();
        String pass = password.getText().toString();
        if (mail.isEmpty()) {
            emailId.setError("Provide your Email first!");
            emailId.requestFocus();
        } else if (pass.isEmpty()) {
            password.setError("Enter Password!");
            password.requestFocus();
        } else if (mail.isEmpty() && pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
        } else if (!(mail.isEmpty() && pass.isEmpty())) {
            firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Not sucessfull", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
