package com.example.amaderchuti.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.amaderchuti.R;
import com.example.amaderchuti.Utils.CommonMethods;
import com.example.amaderchuti.databinding.ActivitySignupBinding;
import com.example.amaderchuti.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.Utils;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding mBinding;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_signup);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        firebaseDb= FirebaseFirestore.getInstance();
        mBinding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                    registerNewUser();
                }
            }
        });
        mBinding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
        private void registerNewUser()
    {
        mAuth.createUserWithEmailAndPassword(mBinding.emailEditText.getText().toString().trim(), mBinding.confirmPasswordEditText.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            uploadUserData();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "User with this email already exists.\n Please use different email id", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
    private void uploadUserData(){
        UserModel user=new UserModel();
        user.setName(mBinding.nameEditText.getText().toString().trim());
        user.setEmail(mBinding.emailEditText.getText().toString().trim());
        user.setMobile(mBinding.mobileEditText.getText().toString().trim());
        user.setLocation(mBinding.locationEditText.getText().toString().trim());
        //1->author
        //2->admin
        user.setType("1");
        firebaseDb.collection("users").document(mBinding.emailEditText.getText().toString().trim())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("%%%%", "Error adding document", e);
                    }
                });
    }
    private boolean isValid(){
        boolean isValid=true;
        if (!CommonMethods.isNotEmpty(mBinding.nameEditText.getText().toString().trim())) {
            mBinding.nameEditText.setError("Please Enter Name");
            isValid=false;
        } else if (!CommonMethods.isNotEmpty(mBinding.emailEditText.getText().toString().trim())) {
            mBinding.emailEditText.setError("Please Enter email");
            isValid=false;
        } else if (!CommonMethods.isNotEmpty(mBinding.mobileEditText.getText().toString().trim())) {
            mBinding.emailEditText.setError("Please Enter mobile number");
            isValid=false;
        } else if (!CommonMethods.isNotEmpty(mBinding.locationEditText.getText().toString().trim())) {
            mBinding.emailEditText.setError("Please Enter location");
            isValid=false;
        } else if (!CommonMethods.isNotEmpty(mBinding.passwordEditText.getText().toString().trim())) {
            mBinding.passwordEditText.setError("Please Enter password");
            isValid=false;
        }else if (!CommonMethods.isNotEmpty(mBinding.confirmPasswordEditText.getText().toString().trim())) {
                mBinding.confirmPasswordEditText.setError("Please Enter password again");
            isValid=false;
        }else if (!mBinding.confirmPasswordEditText.getText().toString().trim().equals(mBinding.passwordEditText.getText().toString().trim())) {
            Toast.makeText(this, "Passwords does not match ", Toast.LENGTH_SHORT).show();
            isValid=false;
        }
        return isValid;
    }
}