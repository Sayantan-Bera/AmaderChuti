package com.example.amaderchuti.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amaderchuti.databinding.EachDashboardArticleBinding;

import java.util.ArrayList;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder>{
    Context context;
    List<String> s = new ArrayList<>();
    public ArticlesAdapter(Context context, List<String> s){
        this.context=context;
        if(s!=null) {
            this.s = s;
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
        return 5;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int pos=holder.getAdapterPosition();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final EachDashboardArticleBinding binding;
        public MyViewHolder(EachDashboardArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
