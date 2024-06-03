package com.example.amaderchuti.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.amaderchuti.R;
import com.example.amaderchuti.Utils.CommonMethods;
import com.example.amaderchuti.adapters.ArticleSectionAdapter;
import com.example.amaderchuti.databinding.ActivityWriteArticleBinding;
import com.example.amaderchuti.models.ArticleModel;
import com.example.amaderchuti.models.ArticleModelWithDocId;
import com.example.amaderchuti.models.ArticleSection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class WriteArticleActivity extends AppCompatActivity {

    private String authorName, coverImageUrl, userEmail;
    private ActivityWriteArticleBinding mBinding;
    FirebaseFirestore firebaseDb;
    AlertDialog loading;
    private ArticleModelWithDocId articleModelFromPreviousScreen;
    private ArticleSectionAdapter articleSectionAdapter;
    private boolean isSubmit=false,isEditable=true;
    public ArrayList<ArticleSection> sectionList = new ArrayList<>();
    ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri coverImage = result.getData().getData();
                        mBinding.imageProduct.setImageURI(coverImage);
                        mBinding.imageProduct.setVisibility(View.VISIBLE);
                        uploadCoverImage(coverImage);
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> launchGalleryActivityForArticleImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri articleImage = result.getData().getData();
                        uploadArticleImages(articleImage);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_write_article);
        authorName = getIntent().getStringExtra("username");
        articleModelFromPreviousScreen = (ArticleModelWithDocId) getIntent().getSerializableExtra("singleArticle");
        SharedPreferences prefs = getSharedPreferences("userdata", MODE_PRIVATE);
        userEmail = prefs.getString("email", "");

        mBinding.selectCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                launchGalleryActivity.launch(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDb = FirebaseFirestore.getInstance();
        setUpCategorySpinner();
        mBinding.tvAuthor.setText("Author " + authorName);
        setUpRecyclerView();
        if (articleModelFromPreviousScreen != null) {
            setUpEditData();
        }
        loading = new AlertDialog.Builder(this)
                .setMessage("Loading...")
                .setCancelable(false).create();
        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    if(articleModelFromPreviousScreen==null) {
                        storeData();
                    }else{
                        updateData();
                    }
                }
            }
        });
        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WriteArticleActivity.this);
                    builder.setMessage("Do you want to submit article ? ");
                    builder.setTitle("Attention !");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        isSubmit=true;
                        if(articleModelFromPreviousScreen==null) {
                            storeData();
                        }else{
                            updateData();
                        }
                    });
                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        mBinding.btAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                launchGalleryActivityForArticleImage.launch(intent);
            }
        });
        mBinding.btText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleSection sectionItem = new ArticleSection();
                sectionItem.setId("1");
                sectionItem.setContent("");
                sectionList.add(sectionItem);
                setUpRecyclerView();
            }
        });
    }

    private void setUpEditData() {
        if(!"0".equalsIgnoreCase(articleModelFromPreviousScreen.getStatus()) && !"3".equalsIgnoreCase(articleModelFromPreviousScreen.getStatus())){
            isEditable=false;
            mBinding.btnSave.setVisibility(View.GONE);
            mBinding.btnSubmit.setVisibility(View.GONE);
            mBinding.titleEditText.setEnabled(false);
            mBinding.titleEditText.setEnabled(false);
            mBinding.overviewEditText.setEnabled(false);
            mBinding.selectCoverImage.setVisibility(View.GONE);
            mBinding.tvWarning.setVisibility(View.GONE);
            mBinding.spCategory.setEnabled(false);
            mBinding.btText.setVisibility(View.GONE);
            mBinding.btAddImage.setVisibility(View.GONE);
        }
        mBinding.tvAuthor.setText("Author :"+articleModelFromPreviousScreen.getAuthor());
        mBinding.titleEditText.setText(articleModelFromPreviousScreen.getTitle());
        mBinding.overviewEditText.setText(articleModelFromPreviousScreen.getOverview());
        mBinding.imageProduct.setVisibility(View.VISIBLE);
        coverImageUrl=articleModelFromPreviousScreen.getImageUrl();
        Glide.with(this).load(coverImageUrl).into(mBinding.imageProduct);
        sectionList.addAll(articleModelFromPreviousScreen.getSectionList());
        setUpRecyclerView();
    }

    private void setUpCategorySpinner() {
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("Select Category *");
        categoryList.add("Adventure");
        categoryList.add("Hill Station");
        categoryList.add("Sea Beach");
        categoryList.add("Remote Areas");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_item_layout, categoryList);
        mBinding.spCategory.setAdapter(adapter);
        if (articleModelFromPreviousScreen != null) {
            mBinding.spCategory.setSelection(adapter.getPosition(articleModelFromPreviousScreen.getCategory()));
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        if (!CommonMethods.isNotEmpty(mBinding.titleEditText.getText().toString().trim())) {
            mBinding.titleEditText.setError("Please Enter Title");
            isValid = false;
        } else if (!CommonMethods.isNotEmpty(mBinding.overviewEditText.getText().toString().trim())) {
            mBinding.overviewEditText.setError("Please Enter Overview");
            isValid = false;
        } else if (mBinding.spCategory.getSelectedItem().toString().equals("Select Category *")) {
            Toast.makeText(this, "Please add category", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if (coverImageUrl == null) {
            Toast.makeText(this, "Please add cover image", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if (sectionList.size()==0) {
            Toast.makeText(this, "Please add body", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }

    public void setUpRecyclerView() {
        if (sectionList.size() > 0) {
            mBinding.rvSections.setVisibility(View.VISIBLE);
            mBinding.tvAddWhenNoData.setVisibility(View.GONE);
        } else {
            mBinding.rvSections.setVisibility(View.GONE);
            mBinding.tvAddWhenNoData.setVisibility(View.VISIBLE);
        }
        articleSectionAdapter = new ArticleSectionAdapter(this,isEditable);
        mBinding.rvSections.setLayoutManager(new LinearLayoutManager(WriteArticleActivity.this));
        mBinding.rvSections.setAdapter(articleSectionAdapter);
    }

    private void storeData() {
        CollectionReference db = firebaseDb.collection("users");
        String key = db.document().getId();
        ArticleModel articleModel = new ArticleModel();
        articleModel.setAuthor(authorName.substring(1));
        articleModel.setTitle(mBinding.titleEditText.getText().toString().trim());
        articleModel.setAuthorEmail(userEmail);
        articleModel.setCategory(mBinding.spCategory.getSelectedItem().toString());
        articleModel.setOverview(mBinding.overviewEditText.getText().toString().trim());
        articleModel.setPostTime(System.currentTimeMillis()/1000+"");
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String date = ft.format(new Date());
        articleModel.setDate(date);
        if(isSubmit){
            articleModel.setStatus("1");//1 means submitted
        }else {
            articleModel.setStatus("0");//0 means not published
        }
        articleModel.setIsEditable("0");//0 means editable
        articleModel.setImageUrl(coverImageUrl);
        articleModel.setIsEditorsChoice("0");//0 means not editors choice
        articleModel.setSectionList(sectionList);
        firebaseDb.collection("articles").document(key).set(articleModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(WriteArticleActivity.this, "Article Saved Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("%%%%", "Error adding document", e);
                    }
                });
    }

    private void updateData() {
        ArticleModel articleModel = new ArticleModel();
        articleModel.setAuthor(articleModelFromPreviousScreen.getAuthor());
        articleModel.setTitle(mBinding.titleEditText.getText().toString().trim());
        articleModel.setAuthorEmail(articleModelFromPreviousScreen.getAuthorEmail());
        articleModel.setCategory(mBinding.spCategory.getSelectedItem().toString());
        articleModel.setOverview(mBinding.overviewEditText.getText().toString().trim());
        articleModel.setPostTime(System.currentTimeMillis()/1000+"");
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String date = ft.format(new Date());
        articleModel.setDate(date);
        if(isSubmit){
            articleModel.setStatus("1");//1 means submitted
        }else {
            articleModel.setStatus("3".equalsIgnoreCase(articleModelFromPreviousScreen.getStatus())?"0":articleModelFromPreviousScreen.getStatus());
        }
        articleModel.setIsEditable(articleModelFromPreviousScreen.getIsEditable());
        articleModel.setImageUrl(coverImageUrl);
        articleModel.setIsEditorsChoice(articleModelFromPreviousScreen.getIsEditorsChoice());
        articleModel.setSectionList(sectionList);
        firebaseDb.collection("articles").document(articleModelFromPreviousScreen.getDocId()).set(articleModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(WriteArticleActivity.this, "Article Updated Successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("%%%%", "Error adding document", e);
                    }
                });
    }
    private void uploadCoverImage(Uri imageFile) {
        loading.show();
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference refStorage = FirebaseStorage.getInstance().getReference().child("articleImages/" + fileName);
        refStorage.putFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri image) {
                                loading.dismiss();
                                coverImageUrl = image.toString();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(WriteArticleActivity.this, "Something went wrong with storage", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadArticleImages(Uri imageFile) {
        loading.show();
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference refStorage = FirebaseStorage.getInstance().getReference().child("articleBodyImages/" + fileName);
        refStorage.putFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri image) {
                                ArticleSection sectionItem = new ArticleSection();
                                sectionItem.setId("2");
                                sectionItem.setContent(image.toString());
                                sectionList.add(sectionItem);
                                setUpRecyclerView();
                                loading.dismiss();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.dismiss();
                        Toast.makeText(WriteArticleActivity.this, "Something went wrong with storage", Toast.LENGTH_LONG).show();
                    }
                });
    }
}