package com.example.amaderchuti.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.amaderchuti.R;
import com.example.amaderchuti.Utils.CommonMethods;
import com.example.amaderchuti.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding mBinding;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        firebaseDb = FirebaseFirestore.getInstance();
        mBinding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                        loginUserAccount();
                }
            }
        });
        mBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUserAccount() {
        mAuth.signInWithEmailAndPassword(mBinding.emailEditText.getText().toString().trim(), mBinding.passwordEditText.getText().toString().trim())
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                                    startActivity(intent);
                                    SharedPreferences.Editor editor = getSharedPreferences("userdata", MODE_PRIVATE).edit();
                                    editor.putString("email", mBinding.emailEditText.getText().toString().trim());
                                    editor.apply();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Login failed! Please enter correct username or password ", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
    }

    private boolean isValid() {
        boolean isValid = true;
        if (!CommonMethods.isNotEmpty(mBinding.emailEditText.getText().toString().trim())) {
            mBinding.emailEditText.setError("Please Enter email");
            isValid = false;
        } else if (!CommonMethods.isNotEmpty(mBinding.passwordEditText.getText().toString().trim())) {
            mBinding.passwordEditText.setError("Please Enter password");
            isValid = false;
        }
        return isValid;
    }
}