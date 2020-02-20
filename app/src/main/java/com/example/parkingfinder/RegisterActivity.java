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

public class RegisterActivity extends AppCompatActivity {

    public EditText emailId, password;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btnSignUp);
        signIn = findViewById(R.id.TVSignIn);
    }

    public void signUp(View view) {
        String mail = emailId.getText().toString();
        String pass = password.getText().toString();

        if (mail.isEmpty()) {
            emailId.setError("Provide your Email first!");
            emailId.requestFocus();
        } else if (pass.isEmpty()) {
            password.setError("Set your password");
            password.requestFocus();
        } else if (mail.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, "Fields Empty!", Toast.LENGTH_SHORT).show();
        } else if (!(mail.isEmpty() && pass.isEmpty())) {
            firebaseAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this.getApplicationContext(),
                                "SignUp unsuccessful: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
