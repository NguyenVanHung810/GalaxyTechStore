package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    private Toolbar toolbar;
    public static boolean ordered=false;  //my code
    public int hi = 1;
    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddAddress;
    private TextView cartTotalAmount;
    private TextView fullname;
    private TextView fulladdress;
    private TextView pincode;
    private Button continueBtn;
    public static CartAdapter cartAdapter;
    public static Dialog loaddialog;
    private Dialog paymentMethoddialog;
    private ImageView paytm;
    private ImageView cod;
    private TextView codBtnTitle;
    private String order_id;
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private String name, mobileNo,paymentMethod;
    private View divider;
    private Boolean successRespond = false;
    public static Boolean fromCart;
    public static Boolean codOrderConfirmed = false;
    public static final int SELECT_ADDRESS = 0;
    private FirebaseFirestore firebaseFirestore;
    public static boolean getQtyIDs = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;

        // Loading dialog
        loaddialog = new Dialog(DeliveryActivity.this);
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // Loading dialog

        // Payment Method dialog
        paymentMethoddialog = new Dialog(DeliveryActivity.this);
        paymentMethoddialog.setContentView(R.layout.payment_method);
        paymentMethoddialog.setCancelable(true);
        paymentMethoddialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        paymentMethoddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethoddialog.findViewById(R.id.paytm);
        cod = paymentMethoddialog.findViewById(R.id.cod_btn);
        codBtnTitle = paymentMethoddialog.findViewById(R.id.cod_tt);
        divider = paymentMethoddialog.findViewById(R.id.payment_divider);
        // Payment Method dialog

        order_id = UUID.randomUUID().toString().substring(0, 28);

        cartTotalAmount = findViewById(R.id.total_cart_amount);
        fullname = (TextView) findViewById(R.id.fullname);
        fulladdress = (TextView) findViewById(R.id.address);
        pincode = (TextView) findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delivery");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deliveryRecyclerView = (RecyclerView) findViewById(R.id.delivery_recyclerview);
        changeOrAddAddress = (Button) findViewById(R.id.change_or_add_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);


        cartAdapter = new CartAdapter(cartItemModelList, cartTotalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddAddress.setVisibility(View.VISIBLE);
        changeOrAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQtyIDs = false;
                Intent myAddressActivity = new Intent(DeliveryActivity.this, MyAddressesActivity.class);
                myAddressActivity.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressActivity);

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allProductsAvailable=true;
                for(CartItemModel cartItemModel:cartItemModelList){
                    if(cartItemModel.isQtyError()){
                        allProductsAvailable=false;
                        break;
                    }
                    if(cartItemModel.getType() == CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codBtnTitle.setAlpha(0.5f);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codBtnTitle.setAlpha(1f);
                        }
                    }
                }

                if(allProductsAvailable){
                    paymentMethoddialog.show();
                }
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrderDetails();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getQtyIDs) {
            loaddialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                    Map<String, Object> timeStamp = new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName)
                            .set(timeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);
                                if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {
                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<String> serverQuantity = new ArrayList<>();

                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                            serverQuantity.add(queryDocumentSnapshot.getId());
                                                        }
                                                        long availableQty = 0;
                                                        boolean noLongerAvailable = true;
                                                        for (String qtyID : cartItemModelList.get(finalX).getQtyIDs()) {
                                                            cartItemModelList.get(finalX).setQtyError(false);
                                                            if (!serverQuantity.contains(qtyID)) {
                                                                if(noLongerAvailable){
                                                                    cartItemModelList.get(finalX).setInStock(false);
                                                                }else {
                                                                    cartItemModelList.get(finalX).setQtyError(true);
                                                                    cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                    Toast.makeText(DeliveryActivity.this, "Sorry, Required amount of quantity is not available", Toast.LENGTH_SHORT).show();
                                                                }

                                                            } else {
                                                                availableQty++;
                                                                noLongerAvailable = false;
                                                            }
                                                        }
                                                        cartAdapter.notifyDataSetChanged();
                                                    }
                                                    else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    loaddialog.dismiss();
                                                }
                                            });
                                }
                            }
                            else {
                                loaddialog.dismiss();
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
        else {
            getQtyIDs = true;
        }
        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullname.setText(name + " - " + mobileNo);
        } else {
            fullname.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }

        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if (landmark.equals("")) {
            fulladdress.setText(flatNo + "," + locality + "," + city + "," + state);
        } else {
            fulladdress.setText(flatNo + "," + locality + "," + landmark + "," + city + "," + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

        if (codOrderConfirmed) {
            showConfirmationLayout();
        }

    }

    private void showConfirmationLayout() {
        codOrderConfirmed = false;
        successRespond = true;
        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++) {

            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());
            }

        }

        if (MainActivity.mainActivity != null) {
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showcart = false;
        } else {
            MainActivity.resetMainActivity = true;
        }

        if (ProductDetailsActivity.productDetailsActivity != null) {
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }

        if (fromCart) {
            loaddialog.show();
            Map<String, Object> updateCart = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < DBqueries.cartLists.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCart.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                } else {
                    indexList.add(x);
                }
            }
            updateCart.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART").set(updateCart)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    DBqueries.cartLists.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loaddialog.dismiss();
                        }
                    });

        }
        continueBtn.setEnabled(false);
        changeOrAddAddress.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order_ID " + order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeliveryActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loaddialog.dismiss();
        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if (!successRespond) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                        }
                                    }
                                });
                    }
                }
                else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(successRespond){
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void placeOrderDetails() {
        String userId = FirebaseAuth.getInstance().getUid();
        loaddialog.show();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER_ID", order_id);
                orderDetails.put("Product_Id", cartItemModel.getProductID());
                orderDetails.put("Product_Image", cartItemModel.getProductImage());
                orderDetails.put("Product_Title", cartItemModel.getProductTitle());
                orderDetails.put("User_Id", userId);
                orderDetails.put("Product_quantity", cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted_Price", cartItemModel.getCuttedPrice());
                } else {
                    orderDetails.put("Cutted_Price", "");
                }
                orderDetails.put("Product_Price", cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCoupanID() != null) {
                    orderDetails.put("Coupan_Id", cartItemModel.getSelectedCoupanID());
                } else {
                    orderDetails.put("Coupan_Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted_Price", cartItemModel.getDiscountedPrice());
                } else {
                    orderDetails.put("Discounted_Price", "");
                }
                orderDetails.put("Ordered_Date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped_Date", FieldValue.serverTimestamp());
                orderDetails.put("Packed_Date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered_Date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled_Date", FieldValue.serverTimestamp());
                orderDetails.put("Order_Status", "Ordered");
                orderDetails.put("Payment_Method", paymentMethod);
                orderDetails.put("Address", fulladdress.getText().toString());
                orderDetails.put("FullName", fullname.getText().toString());
                orderDetails.put("Pincode", pincode.getText().toString());
                orderDetails.put("Free_Coupens", cartItemModel.getFreeCoupen());
                orderDetails.put("Delivery_Price", cartItemModelList.get(cartItemModelList.size() - 1).getDeliveryPrice());
                orderDetails.put("Cancellation_requested", false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("ORDER_ITEMS").document(cartItemModel.getProductID())
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total_Items", cartItemModel.getTotalItems());
                orderDetails.put("Total_Items_Price", cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery_Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total_Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved_Amount", cartItemModel.getSavedAmount());
                orderDetails.put("Payment_status", "not paid");
                orderDetails.put("Order_Status", "Cancelled");

                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cod();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void cod(){
        getQtyIDs = false;
        paymentMethoddialog.dismiss();
        startActivity(new Intent(DeliveryActivity.this, OrderConfirmationActivity.class).putExtra("order_id",order_id));
    }
}