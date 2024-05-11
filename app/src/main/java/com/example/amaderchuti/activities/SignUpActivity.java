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
    }
        private void registerNewUser()
    {
        mBinding.progressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(mBinding.emailEditText.getText().toString().trim(), mBinding.confirmPasswordEditText.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            mBinding.progressbar.setVisibility(View.GONE);
                            uploadUserData();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(SignUpActivity.this, "User with this email already exists.\n Please use different email id", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                            }
                            mBinding.progressbar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void uploadUserData(){
        UserModel user=new UserModel();
        user.setName(mBinding.nameEditText.getText().toString().trim());
        user.setEmail(mBinding.emailEditText.getText().toString().trim());
        //1->author
        //2->admin
        user.setType("1");
        firebaseDb.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("%%%%", "DocumentSnapshot added with ID: " + documentReference.getId());
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
        }else if (!CommonMethods.isNotEmpty(mBinding.passwordEditText.getText().toString().trim())) {
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