package com.example.galaxytechstore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.Viewholder> {

    private List<WishlistModel> list;
    private Boolean wishlist;
    private int lastposition = -1;

    public WishListAdapter(List<WishlistModel> list, boolean wl) {
        this.list = list;
        this.wishlist = wl;
    }

    @NonNull
    @Override
    public WishListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.Viewholder holder, int position) {
        String id = list.get(position).getProductID();
        String url = list.get(position).getProdcutImage();
        String tt = list.get(position).getProductTitle();
        long fc = list.get(position).getFreecoupen();
        String rating = list.get(position).getRating();
        long tr = list.get(position).getTotal_Ratings();
        String pp = list.get(position).getProductPrice();
        String cp = list.get(position).getCuttedPrice();
        boolean pm = list.get(position).getCOD();
        boolean is = list.get(position).getInStock();

        holder.setData(id, url, tt, fc, rating, tr, pp, cp, pm, position, is);

        if(lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupens;
        private ImageView coupenIcon;
        private TextView rating;
        private TextView totalRatings;
        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private ImageButton deleteBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.product_image_wishlist_item);
            productTitle = (TextView) itemView.findViewById(R.id.product_title_wishlist_item);
            freeCoupens = (TextView) itemView.findViewById(R.id.free_coupen);
            coupenIcon = (ImageView) itemView.findViewById(R.id.coupen_ic);
            rating = (TextView) itemView.findViewById(R.id.rating_wishlist_item);
            totalRatings = (TextView) itemView.findViewById(R.id.total_rating_wishlist_item);
            priceCut = (View) itemView.findViewById(R.id.price_cut);
            productPrice = (TextView) itemView.findViewById(R.id.product_price_wishlist_item);
            cuttedPrice = (TextView) itemView.findViewById(R.id.cutted_price_wishlist_item);
            paymentMethod = (TextView) itemView.findViewById(R.id.shipping_method);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.btn_del);
        }
        private void setData(String id, String url, String title, long freeCoupenNo, String averageRate, long tr, String pp, String cp, boolean pm, int index, boolean instock){
            Glide.with(itemView.getContext()).load(url).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(coupenIcon);
            productTitle.setText(title);
            if(freeCoupenNo != 0 && instock){
                coupenIcon.setVisibility(View.VISIBLE);
                if(freeCoupenNo == 1){
                    freeCoupens.setText("free "+ freeCoupenNo+" coupen");
                }
                else {
                    freeCoupens.setText("free "+ freeCoupenNo+" coupen");
                }
            }
            else {
                coupenIcon.setVisibility(View.INVISIBLE);
                freeCoupens.setVisibility(View.INVISIBLE);
            }
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if(instock) {
                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                productPrice.setText("Hết hàng");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);

                rating.setText(averageRate);
                totalRatings.setText("("+tr + " ratings)");
                productPrice.setText(convertToVietnameseMoney(Integer.parseInt(pp)));
                cuttedPrice.setText(convertToVietnameseMoney(Integer.parseInt(cp)));
                if (pm) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }
            else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Hết hàng");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.md_red_500));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);

            }

            if(wishlist){
                deleteBtn.setVisibility(View.VISIBLE);
            }
            else {
                deleteBtn.setVisibility(View.GONE);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBqueries.removeFromWishList(index, itemView.getContext());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailIntent.putExtra("PRODUCT_ID", id);
                    itemView.getContext().startActivity(productDetailIntent);
                }
            });
        }

        private String convertToVietnameseMoney(int t) {
            Locale locale = new Locale("vi", "VN");
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            return format.format(t);
        }
    }
}