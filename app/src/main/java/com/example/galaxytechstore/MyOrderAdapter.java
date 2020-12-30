package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private List<MyOrderItemModel> myOrderItemModelList;
    private Dialog loadingDialog;

    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList, Dialog loadingDialog) {
        this.myOrderItemModelList = myOrderItemModelList;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {

        String productId = myOrderItemModelList.get(position).getProductId();
        String address = myOrderItemModelList.get(position).getAddress();
        String coupanId = myOrderItemModelList.get(position).getCoupanId();
        String productPrice = myOrderItemModelList.get(position).getProductPrice();
        String cuttedPrice = myOrderItemModelList.get(position).getCuttedPrice();
        String discountedPrice = myOrderItemModelList.get(position).getDiscountedPrice();
        Date orderedDate = myOrderItemModelList.get(position).getOrderedDate();
        Date packedDate = myOrderItemModelList.get(position).getPackedDate();
        Date shippedDate = myOrderItemModelList.get(position).getShippedDate();
        Date delveredDate = myOrderItemModelList.get(position).getDelveredDate();
        Date cancelleddate = myOrderItemModelList.get(position).getCancelleddate();
        Long productQuantity = myOrderItemModelList.get(position).getProductQuantity();
        String fullName = myOrderItemModelList.get(position).getFullName();
        String orderId = myOrderItemModelList.get(position).getOrderId();
        String paymentMethod = myOrderItemModelList.get(position).getPaymentMethod();
        String userId = myOrderItemModelList.get(position).getUserId();
        String productTitle = myOrderItemModelList.get(position).getProductTitle();
        String orderStatus = myOrderItemModelList.get(position).getOrderStatus();
        String productImage = myOrderItemModelList.get(position).getProductImage();
        Date date;
        switch (orderStatus) {
            case "Ordered":
                date = myOrderItemModelList.get(position).getOrderedDate();
                break;
            case "Packed":
                date = myOrderItemModelList.get(position).getPackedDate();
                break;
            case "Shipped":
                date = myOrderItemModelList.get(position).getShippedDate();
                break;
            case "Delivered":
                date = myOrderItemModelList.get(position).getDelveredDate();
                break;
            case "Cancelled":
                date = myOrderItemModelList.get(position).getCancelleddate();
                break;
            default:
                date = myOrderItemModelList.get(position).getCancelleddate();
                break;

        }
        int rating = myOrderItemModelList.get(position).getRating();
        holder.setdata(productImage, productTitle, orderStatus, date, rating, productId, position);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage, orderIndicator;
        private TextView productTitle, deliveryStatus;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_my_oders_item);
            orderIndicator = itemView.findViewById(R.id.delivery_indicator);
            productTitle = itemView.findViewById(R.id.product_title);
            deliveryStatus = itemView.findViewById(R.id.order_delivered_date);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setdata(String resource, String productTitleText, String orderStatus, Date date, final int rating, final String productId, final int position) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.no_image)).into(productImage);
            productTitle.setText(productTitleText);
            if (orderStatus.equals("Cancelled")) {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.md_red_500)));
            } else {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.md_green_500)));
            }
            deliveryStatus.setText(orderStatus + " " + simpleDateFormat.format(date));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), OrderDetailsActivity.class).putExtra("position", position));
                }
            });
        }
    }
}
