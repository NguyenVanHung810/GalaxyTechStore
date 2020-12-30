package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.galaxytechstore.ui.Utility;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.galaxytechstore.ProductDetailsActivity.setRating;

public class OrderDetailsActivity extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat;
    private Dialog loadingDialog, cancelDialog;
    private int position;
    private TextView title, price, qty;
    private ImageView orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;
    private TextView orderedDate, shippedDate, packedDate, deliveredDate;
    private TextView orderedBody, shippedBody, packedBody, deliveredBody;
    private TextView fullname, fullladdress, phonenumber;
    private TextView totalItems, totalItemsPrice, deliveryPrice, savedAmount, totalAmount;
    private Button cancelOrderBtn;
    private Button changeoradd;

    private ListView listView;
    private ArrayList<OrderItemsModel> orderItemsModels;
    private OrderItemsAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Chi tiết đơn hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //////////loading dialog
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////loading dialog

        //////////cancel dialog

        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(true);
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //////////cancel dialog
        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        qty = findViewById(R.id.product_quantity);

        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipping_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        O_P_progress = findViewById(R.id.ordered_packed_progress);
        P_S_progress = findViewById(R.id.packed_shipping_progress);
        S_D_progress = findViewById(R.id.shipping_delivered_progress);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipping_title);
        deliveredTitle = findViewById(R.id.delivered_title);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipping_body);
        deliveredBody = findViewById(R.id.delivered_body);

        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipping_date);
        deliveredDate = findViewById(R.id.delivered_date);

        fullname = findViewById(R.id.fullname);
        fullladdress = findViewById(R.id.fulladdress);
        phonenumber = findViewById(R.id.phone_number);
        changeoradd = findViewById(R.id.change_or_add_address);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        savedAmount = findViewById(R.id.saved_amount);
        totalAmount = findViewById(R.id.total_price);
        cancelOrderBtn = findViewById(R.id.cancel_btn);

        listView = findViewById(R.id.product_list);

        orderItemsModels = new ArrayList<>();
        orderItemsModels.add(new OrderItemsModel("///", "Iphone 11", "12.000.000"));
        orderItemsModels.add(new OrderItemsModel("///", "Iphone 11", "12.000.000"));
        orderItemsModels.add(new OrderItemsModel("///", "Iphone 11", "12.000.000"));
        orderItemsModels.add(new OrderItemsModel("///", "Iphone 11", "12.000.000"));
        orderItemsModels.add(new OrderItemsModel("///", "Iphone 11", "12.000.000"));
        orderItemsModels.add(new OrderItemsModel("///", "Iphone 11", "12.000.000"));

        adapter = new OrderItemsAdapter(getApplicationContext(), R.layout.order_items_layout, orderItemsModels);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        position = getIntent().getIntExtra("position", -1);

        final MyOrderItemModel model = DBqueries.myOrderItemModelList.get(position);


        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
        switch (model.getOrderStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                break;

            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                O_P_progress.setProgress(100);

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);


                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                break;

            case "Shipped":
                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                break;
            case "out for Delivery":
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDelveredDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Đơn đặt hàng của bạn đã được giao");

                break;
            case "Delivered":
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDelveredDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                break;

            case "Cancelled":

                if (model.getPackedDate().after(model.getOrderedDate())) {

                    if (model.getShippedDate().after(model.getPackedDate())) {

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDelveredDate())));
                        deliveredBody.setText("Đơn hàng của bạn đã được hủy.");
                        deliveredTitle.setText("Đã hủy");

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);

                    } else {
                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));
                        shippedBody.setText("Đơn hàng của bạn đã được hủy.");
                        shippedTitle.setText("Đã hủy");

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                    }

                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                    packedBody.setText("Đơn hàng của bạn đã được hủy.");
                    packedTitle.setText("Đã hủy");

                    O_P_progress.setProgress(100);

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);


                    shippedIndicator.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                }
                break;
        }


        if (model.isCancellationrequested()) {
            cancelOrderBtn.setVisibility(View.VISIBLE);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setText("Đang hủy bỏ");
            cancelOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        } else {
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")) {
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelDialog.dismiss();
                            }
                        });

                        cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                Map<String, Object> map = new HashMap<>();
                                map.put("Order_Id", model.getOrderId());
                                map.put("Product_Id", model.getProductId());
                                map.put("Order_Cancelled", false);
                                FirebaseFirestore.getInstance().collection("CANCELLED_ORDERS").document(model.getOrderId()).set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderId()).collection("ORDER_ITEMS").document(model.getProductId()).update("Cancellation_requested", true)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        model.setCancellationrequested(true);
                                                                        cancelOrderBtn.setEnabled(false);
                                                                        cancelOrderBtn.setText("Đang hủy bỏ");
                                                                        cancelOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                                        cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });

                                                } else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        });
                        cancelDialog.show();
                    }
                });
            }
        }

        fullname.setText(model.getFullName());
        fullladdress.setText(model.getAddress());
        phonenumber.setText(model.getPincode());
        changeoradd.setVisibility(View.GONE);

        Long totalPrice;

        totalItems.setText("Giá (" + model.getProductQuantity() + " sản phẩm)");
        if (model.getDiscountedPrice().equals("")) {
            totalPrice = Long.valueOf(model.getProductPrice()) * model.getProductQuantity();
            totalItemsPrice.setText(vnMoney(Long.parseLong(String.valueOf(totalPrice))));
        } else {
            totalPrice = Long.valueOf(model.getDiscountedPrice()) * model.getProductQuantity();
            totalItemsPrice.setText(vnMoney(Long.parseLong(String.valueOf(totalPrice))));
        }

        if (model.getDeliveryPrice().equals("FREE")) {
            deliveryPrice.setText("FREE");
            totalAmount.setText(vnMoney(totalPrice));
        } else {
            deliveryPrice.setText(vnMoney(Long.parseLong(model.getDeliveryPrice())));
            totalAmount.setText(vnMoney(Long.parseLong(String.valueOf(totalPrice + Long.valueOf(model.getDeliveryPrice())))));
        }


        if (!model.getCuttedPrice().equals("")) {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved Rs." + (model.getProductQuantity()) * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order.");
            } else {
                savedAmount.setText("You saved Rs." + (model.getProductQuantity()) * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getProductPrice())) + "/- on this order.");
            }
        } else {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved Rs." + (model.getProductQuantity()) * (Long.valueOf(model.getProductPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order.");
            } else {
                savedAmount.setText("You saved Rs.0/- on this order.");
            }
        }


    }

    private String vnMoney(Long s) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(s) + " đ";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}