package com.example.amaderchuti.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.media.tv.TableRequest;
import android.os.Bundle;
import android.widget.Toast;

import com.example.amaderchuti.R;
import com.example.amaderchuti.adapters.ArticlesAdapter;
import com.example.amaderchuti.databinding.ActivityAuthorDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DashBoardActivity extends AppCompatActivity {

    ActivityAuthorDashboardBinding mBinding;
    ArticlesAdapter mArticlesAdapter;
    private String userEmail;
    FirebaseFirestore firebaseDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_author_dashboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDb= FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("userdata", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        getAndSetUserData();
        mArticlesAdapter=new ArticlesAdapter(this,new ArrayList<String>());
        mBinding.rvArticles.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvArticles.setAdapter(mArticlesAdapter);
    }
    private void getAndSetUserData(){
                firebaseDb.collection("users").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                               mBinding.tvName.setText(": "+document.getString("name"));
                               mBinding.tvEmail.setText(": "+document.getString("email"));
                               mBinding.tvDesignation.setText(": "+("1".equals(document.getString("type"))?"Author":"Editor"));
                            } else {
                                Toast.makeText(DashBoardActivity.this,"No data found",Toast.LENGTH_SHORT);
                            }
                        } else {
                            Toast.makeText(DashBoardActivity.this,"Failed to load data",Toast.LENGTH_SHORT);
                        }
                    }
                });

    }
}