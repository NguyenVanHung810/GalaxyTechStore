package com.example.galaxytechstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductSpecificationAdapter extends RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder> {

    private List<ProductSpecificationModel> list;

    public ProductSpecificationAdapter(List<ProductSpecificationModel> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).getType()){
            case 0:
                return ProductSpecificationModel.SPECIFICATION_TITLE;
            case 1:
                return ProductSpecificationModel.SPECIFICATION_BODY;
            default:
                return -1;
        }
    }

    @SuppressLint("Range")
    @NonNull
    @Override
    public ProductSpecificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case ProductSpecificationModel.SPECIFICATION_TITLE:
                TextView title = new TextView(parent.getContext());
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(setDp(16, parent.getContext())
                        , setDp(16, parent.getContext())
                        ,setDp(16, parent.getContext())
                        ,setDp(8, parent.getContext()));
                title.setLayoutParams(layoutParams);
                return new ViewHolder(title);
            case ProductSpecificationModel.SPECIFICATION_BODY:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_item_layout,parent, false);
                return new ViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecificationAdapter.ViewHolder holder, int position) {
        switch (list.get(position).getType()){
            case ProductSpecificationModel.SPECIFICATION_TITLE:
                holder.setTitle(list.get(position).getTitle());

            case ProductSpecificationModel.SPECIFICATION_BODY:
                String fnt = list.get(position).getFeatureName();
                String fnv = list.get(position).getFeatureValue();
                holder.setFeature(fnt, fnv);
                break;
            default:
                return ;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView featureName;
        private TextView featureValue;
        private TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        private void setTitle(String titletext){
            title = (TextView) itemView;
            title.setText(titletext);
        }
        private void setFeature(String fnt, String ftv){
            featureName = (TextView) itemView.findViewById(R.id.feature_name);
            featureValue = (TextView) itemView.findViewById(R.id.feature_value);
            featureName.setText(fnt);
            featureValue.setText(ftv);
        }
    }

    private int setDp(int dp, Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

