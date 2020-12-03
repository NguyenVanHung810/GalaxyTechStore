package com.example.galaxytechstore;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryModels;

    public CategoryAdapter(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModels.get(position);
        holder.setCategoryName(categoryModel.getCategoryName(), position);
        holder.setCategoryImage(categoryModel.getCategoryImage());
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView cateImage;
        private TextView cateName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cateImage = (ImageView) itemView.findViewById(R.id.category_image);
            cateName = (TextView) itemView.findViewById(R.id.category_name);
        }

        private void setCategoryImage(String iconUrl){
            if(!iconUrl.equals("null")){
                Glide.with(itemView.getContext()).load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.cart)).into(cateImage);
            }
        }

        private void setCategoryName(final String name, final int position){
            cateName.setText(name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position != 0){
                      /*  Intent categoryIntent = new Intent(itemView.getContext(),CategoryActivity.class);
                        categoryIntent.putExtra("CategoryName", name);
                        itemView.getContext().startActivity(categoryIntent);*/
                    }
                }
            });
        }
    }
}

