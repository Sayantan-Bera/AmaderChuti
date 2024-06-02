package com.example.amaderchuti.adapters;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amaderchuti.Utils.CommonMethods;
import com.example.amaderchuti.activities.WriteArticleActivity;
import com.example.amaderchuti.databinding.EachArticleListImageItemBinding;
import com.example.amaderchuti.databinding.QuestionItemInputFieldBinding;
import com.example.amaderchuti.models.ArticleSection;
import com.bumptech.glide.Glide;

public class ArticleSectionAdapter extends RecyclerView.Adapter {

    WriteArticleActivity mBase;
    private final int INPUT = 1,IMAGE=2;
    public ArticleSectionAdapter(WriteArticleActivity mBase) {
        this.mBase = mBase;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case IMAGE:
                return new ImageViewHolder(EachArticleListImageItemBinding.inflate(layoutInflater, parent, false));
            default:
                return new TextInputViewHolder(QuestionItemInputFieldBinding.inflate(layoutInflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ArticleSection question = mBase.sectionList.get(position);
        switch (holder.getItemViewType()) {
            case IMAGE:
                ((ImageViewHolder) holder).imageBind(question);
                break;
            case INPUT:
                ((TextInputViewHolder) holder).textInputBind(question);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mBase.sectionList.get(position).getId()) {
            case "1":
                return INPUT;
            case "2":
                return IMAGE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mBase.sectionList.size();
    }

    public class TextInputViewHolder extends RecyclerView.ViewHolder {

        private final QuestionItemInputFieldBinding questionItemInputFieldBinding;

        public TextInputViewHolder(@NonNull QuestionItemInputFieldBinding questionItemInputFieldBinding) {
            super(questionItemInputFieldBinding.getRoot());
            this.questionItemInputFieldBinding = questionItemInputFieldBinding;;
        }

        public void textInputBind(ArticleSection question) {

            questionItemInputFieldBinding.etAnswerInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String answerInput = null;
                    if (editable != null && editable.toString().trim().length() > 0) {
                        answerInput = editable.toString().trim();
                    }
                    question.setContent(answerInput);
                }
            });
            if (question.getContent() != null && question.getContent().trim().length() > 0) {
                questionItemInputFieldBinding.etAnswerInput.setText(question.getContent());
            }
        }
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private final EachArticleListImageItemBinding imageBinding;

        public ImageViewHolder(@NonNull EachArticleListImageItemBinding imageBinding) {
            super(imageBinding.getRoot());
            this.imageBinding = imageBinding;
        }

        public void imageBind(ArticleSection question) {
            if(CommonMethods.isNotEmpty(question.getContent())){
                Glide.with(mBase).load(question.getContent()).into(imageBinding.imageView);
            }
        }
    }
}
