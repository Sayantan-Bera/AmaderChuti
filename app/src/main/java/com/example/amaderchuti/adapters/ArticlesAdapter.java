package com.example.amaderchuti.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amaderchuti.Utils.CommonMethods;
import com.example.amaderchuti.activities.WriteArticleActivity;
import com.example.amaderchuti.databinding.EachDashboardArticleBinding;
import com.example.amaderchuti.models.ArticleModel;
import com.example.amaderchuti.models.ArticleModelWithDocId;

import java.util.ArrayList;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder> {
    Context context;
    List<ArticleModelWithDocId> articles = new ArrayList<>();

    public ArticlesAdapter(Context context, List<ArticleModelWithDocId> articles) {
        this.context = context;
        if (articles != null) {
            this.articles = articles;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        EachDashboardArticleBinding binding = EachDashboardArticleBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        ArticleModelWithDocId articleItem = articles.get(pos);
        holder.binding.tvTitle.setText(articleItem.getTitle());
        holder.binding.tvOverview.setText(articleItem.getOverview());
        if (CommonMethods.isNotEmpty(articleItem.getImageUrl())) {
            Glide.with(context).load(articleItem.getImageUrl()).into(holder.binding.ivArticleCover);
        }
        if ("0".equals(articleItem.getStatus())) {
            holder.binding.tvStatus.setText("Not Submitted");
            ViewCompat.setBackgroundTintList(holder.binding.tvStatus, ColorStateList.valueOf(Color.parseColor("#f4a261")));
        }
        else if ("1".equals(articleItem.getStatus())) {
            holder.binding.tvStatus.setText("Submitted");
            ViewCompat.setBackgroundTintList(holder.binding.tvStatus, ColorStateList.valueOf(Color.parseColor("#22577a")));
        }
        else if ("2".equals(articleItem.getStatus())) {
            holder.binding.tvStatus.setText("Published");
            ViewCompat.setBackgroundTintList(holder.binding.tvStatus, ColorStateList.valueOf(Color.parseColor("#2a9d8f")));
        }
        else if ("3".equals(articleItem.getStatus())) {
            holder.binding.tvStatus.setText("Rejected");
            ViewCompat.setBackgroundTintList(holder.binding.tvStatus, ColorStateList.valueOf(Color.parseColor("#e76f51")));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, WriteArticleActivity.class);
                intent.putExtra("singleArticle", articleItem);
                context.startActivity(intent);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final EachDashboardArticleBinding binding;

        public MyViewHolder(EachDashboardArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
