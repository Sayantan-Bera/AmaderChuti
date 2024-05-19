package com.example.amaderchuti.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.amaderchuti.R;
import com.example.amaderchuti.Utils.CommonMethods;
import com.example.amaderchuti.databinding.ActivityWriteArticleBinding;
import com.example.amaderchuti.models.ArticleModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class WriteArticleActivity extends AppCompatActivity {

    private String authorName, coverImageUrl, userEmail;
    private ActivityWriteArticleBinding mBinding;
    FirebaseFirestore firebaseDb;
    ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri coverImage = result.getData().getData();
                        mBinding.imageProduct.setImageURI(coverImage);
                        mBinding.imageProduct.setVisibility(View.VISIBLE);
                        uploadProductImage(coverImage);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_write_article);
        authorName = getIntent().getStringExtra("username");
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
        firebaseDb= FirebaseFirestore.getInstance();
        setUpCategorySpinner();
        mBinding.tvAuthor.setText("Author " + authorName);
        mBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    storeData();
                }
            }
        });
    }

    private void setUpCategorySpinner() {
        ArrayList<String> categoryList = new ArrayList<>();
        categoryList.add("Select Category");
        categoryList.add("Adventure");
        categoryList.add("Hill Station");
        categoryList.add("Sea Beach");
        categoryList.add("Remote Areas");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_item_layout, categoryList);
        mBinding.spCategory.setAdapter(adapter);
    }

    private boolean isValid() {
        boolean isValid = true;
        if (!CommonMethods.isNotEmpty(mBinding.titleEditText.getText().toString().trim())) {
            mBinding.titleEditText.setError("Please Enter Title");
            isValid = false;
        } else if (!CommonMethods.isNotEmpty(mBinding.overviewEditText.getText().toString().trim())) {
            mBinding.overviewEditText.setError("Please Enter Overview");
            isValid = false;
        } else if (mBinding.spCategory.getSelectedItem().toString().equals("Select Category")) {
            Toast.makeText(this, "Please add category", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if (coverImageUrl==null) {
            Toast.makeText(this, "Please add cover image", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }

    private void storeData() {
        CollectionReference db=firebaseDb.collection("users");
        String key = db.document().getId();
        ArticleModel articleModel = new ArticleModel();
        articleModel.setAuthor(authorName.substring(1));
        articleModel.setTitle(mBinding.titleEditText.getText().toString().trim());
        articleModel.setAuthorEmail(userEmail);
        articleModel.setCategory(mBinding.spCategory.getSelectedItem().toString());
        articleModel.setOverview(mBinding.overviewEditText.getText().toString().trim());
        articleModel.setStatus("0");//0 means not published
        articleModel.setIsEditable("0");//0 means editable
        articleModel.setImageUrl(coverImageUrl);
        articleModel.setIsEditorsChoice("0");//0 means not editors choice
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

    private void uploadProductImage(Uri imageFile) {
        System.out.println("%%%%"+imageFile);

        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference refStorage = FirebaseStorage.getInstance().getReference().child("articleImages/" + fileName);

        refStorage.putFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri image) {
                                coverImageUrl = image.toString();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteArticleActivity.this, "Something went wrong with storage", Toast.LENGTH_LONG).show();
                    }
                });
    }
}