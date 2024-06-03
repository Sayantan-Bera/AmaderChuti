package com.example.amaderchuti.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TableRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.amaderchuti.R;
import com.example.amaderchuti.adapters.ArticlesAdapter;
import com.example.amaderchuti.databinding.ActivityAuthorDashboardBinding;
import com.example.amaderchuti.models.ArticleModel;
import com.example.amaderchuti.models.ArticleModelWithDocId;
import com.example.amaderchuti.models.ArticleSection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class DashBoardActivity extends AppCompatActivity {

    ActivityAuthorDashboardBinding mBinding;
    ArticlesAdapter mArticlesAdapter;
    ArrayList<ArticleModelWithDocId> articles = new ArrayList<>();
    private String userEmail;
    private AlertDialog loading;
    FirebaseFirestore firebaseDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_author_dashboard);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDb = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("userdata", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        loading = new AlertDialog.Builder(this).setMessage("Loading...").setCancelable(false).create();
        getAndSetUserData();
        getArticlesByUser();
        mBinding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this);
                builder.setMessage("Are you sure you want to Log Out ? ");
                builder.setTitle("Attention !");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(DashBoardActivity.this, LoginActivity.class));
                    finish();
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        mBinding.tvCreateArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, WriteArticleActivity.class);
                intent.putExtra("username", mBinding.tvName.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    private void getAndSetUserData() {
        loading.show();
        firebaseDb.collection("users").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mBinding.tvName.setText(": " + document.getString("name"));
                        mBinding.tvEmail.setText(": " + document.getString("email"));
                        mBinding.tvDesignation.setText(": " + ("1".equals(document.getString("type")) ? "Author" : "Editor"));
                        loading.dismiss();
                    } else {
                        Toast.makeText(DashBoardActivity.this, "No data found", Toast.LENGTH_SHORT);
                        loading.dismiss();
                    }
                } else {
                    loading.dismiss();
                    Toast.makeText(DashBoardActivity.this, "Failed to load data", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void getArticlesByUser() {
        articles.clear();
        firebaseDb.collection("articles").whereEqualTo("authorEmail", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ArticleModelWithDocId articleModel = new ArticleModelWithDocId();
                        articleModel.setDocId(document.getId());
                        articleModel.setAuthor(document.getData().get("author").toString());
                        articleModel.setTitle(document.getData().get("title").toString());
                        articleModel.setAuthorEmail(document.getData().get("authorEmail").toString());
                        articleModel.setCategory(document.getData().get("category").toString());
                        articleModel.setOverview(document.getData().get("overview").toString());
                        articleModel.setStatus(document.getData().get("status").toString());
                        articleModel.setIsEditable(document.getData().get("isEditable").toString());
                        articleModel.setImageUrl(document.getData().get("imageUrl").toString());
                        articleModel.setPostTime(document.getData().get("postTime").toString());
                        articleModel.setIsEditorsChoice(document.getData().get("isEditorsChoice").toString());
                        articleModel.setDate(document.getData().get("date").toString());
                        ArrayList<HashMap> arr = new ArrayList<>();
                        arr.addAll((Collection<? extends HashMap>) document.get("sectionList"));
                        ArrayList<ArticleSection> sectionList = new ArrayList<>();
                        for (HashMap h : arr) {
                            ArticleSection articleSection = new ArticleSection();
                            articleSection.setId(h.get("id").toString());
                            articleSection.setContent(h.get("content").toString());
                            sectionList.add(articleSection);
                        }
                        articleModel.setSectionList(sectionList);
                        articles.add(articleModel);
                    }
                    Collections.sort(articles, (o1, o2) -> o2.getPostTime().compareTo(o1.getPostTime()));
                    mArticlesAdapter = new ArticlesAdapter(DashBoardActivity.this, articles);
                    mBinding.rvArticles.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this));
                    mBinding.rvArticles.setAdapter(mArticlesAdapter);
                } else {
                    System.out.println("%%%% Failed");
                }
            }
        });

    }
}