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

import java.text.DecimalFormat;
import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> list;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
        String id = list.get(position).getProductID();
        String url = list.get(position).getProductImage();
        String tt = list.get(position).getProductTitle();
        String desc = list.get(position).getProductDesc();
        String price = list.get(position).getProductPrice();

        holder.setData(id, url, tt, desc, price);
    }

    @Override
    public int getItemCount() {
        if(list.size()>8){
            return 8;
        }
        else {
            return list.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView productDesc;
        private TextView productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.horizontal_scroll_item_img);
            productTitle = (TextView) itemView.findViewById(R.id.horizontal_scroll_item_tv);
            productDesc = (TextView) itemView.findViewById(R.id.horizontal_scroll_item_desc);
            productPrice = (TextView) itemView.findViewById(R.id.horizontal_scroll_item_price);
        }

        public void setData(String productID, String url, String title, String desc, String price) {
            Glide.with(itemView.getContext()).load(url).apply(new RequestOptions().placeholder(R.drawable.banner_placeholder)).into(productImage);
            productTitle.setText(title);
            productDesc.setText("Hàng chính hãng");
            productPrice.setText(vnMoney(Integer.parseInt(("0"+price).trim())));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(),ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productID);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }

    private String vnMoney(int s) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(s) + " đ";
    }
}
