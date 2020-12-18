package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CartAdapter extends RecyclerView.Adapter {
    /////coupan dialog
    private TextView coupanTitle, discountedPrice, originalPrice;
    private TextView coupanExpiryDate;
    private TextView coupanBody;
    private RecyclerView coupanRecyclerview;
    private LinearLayout selectedCoupan, applyORremoveBtnContainer;
    private TextView footerText;
    private LinearLayout coupanRedemLayout;
    private Button applyCoupanBtn, removeCoupanBtn;
    private Long productPriceValue;
    /////coupan dialog

    private List<CartItemModel> cartItemModelList;
    private int lastposition = -1;
    private TextView cartTotal;
    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotal, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotal = cartTotal;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewholder(view);
            case CartItemModel.TOTAL_AMOUNT:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new CartTotalAmoutnViewholder(view1);
            default:
                return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freecoupans = cartItemModelList.get(position).getFreeCoupen();
                String productprice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                Long productQty = cartItemModelList.get(position).getProductQuantity();
                Long maxQty = cartItemModelList.get(position).getMaxQuantity();
                Long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean inStock = cartItemModelList.get(position).isInStock();
                boolean codAvailable = cartItemModelList.get(position).isCOD();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIDs = cartItemModelList.get(position).getQtyIDs();

                ((CartItemViewholder) holder).setItemDetails(productID, resource, title, freecoupans, productprice, cuttedPrice, offersApplied, position, inStock, productQty, maxQty, qtyError, qtyIDs, stockQty, codAvailable);
                break;
            case CartItemModel.TOTAL_AMOUNT:

                int totalItems = 0, totalAmount, savedAmount = 0;
                int totalItemsPrice = 0;
                String deliveryPrice;

                for (int x = 0; x < cartItemModelList.size(); x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {
                        int qty = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + qty;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanID())) {
                            totalItemsPrice = totalItemsPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice()) * qty;
                        } else {
                            totalItemsPrice = totalItemsPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()) * qty;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())) {
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * qty;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanID())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * qty;
                            }
                        } else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupanID())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * qty;
                            }
                        }
                    }
                }
                if (totalItemsPrice > 10000000) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemsPrice;
                } else {
                    deliveryPrice = "14000";
                    totalAmount = totalItemsPrice + 14000;
                }
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemsPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmoutnViewholder) holder).setTotalAmount(totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount);
                break;
            default:
                return;
        }
        if (lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    public class CartItemViewholder extends RecyclerView.ViewHolder {

        private ImageView productImage, freeCoupanIcon, cod;
        private LinearLayout deleteBtn, coupanRedemptionLayout;
        private TextView productTitle, freeCoupans, productPrice, cuttedPrice, offersApplied, coupansApplied, productQuantity, coupanRedemptionBody;
        private Button reedemBtn;

        public CartItemViewholder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            freeCoupanIcon = itemView.findViewById(R.id.free_coupen_icon);
            productTitle = (TextView) itemView.findViewById(R.id.product_title_cart);
            freeCoupans = itemView.findViewById(R.id.tv_free_coupen);
            productPrice = itemView.findViewById(R.id.product_price_cart);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offers_applied);
            coupansApplied = itemView.findViewById(R.id.coupen_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
            coupanRedemptionLayout = itemView.findViewById(R.id.coupen_redemption_layout);
            reedemBtn = itemView.findViewById(R.id.coupan_redemption_btn);
            coupanRedemptionBody = itemView.findViewById(R.id.tv_coupan_redemption);
            cod = itemView.findViewById(R.id.cod_indicator);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setItemDetails(final String productID, final String resource, final String title, final Long freecoupansNo, final String productPriceText, final String cuttedPriceText, Long offersAppliedNo, final int position, boolean inStock, final Long productQty, final Long maxQty, boolean qtyError, final List<String> qtyIDs, final long stockQty, boolean codAvaiiable) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions()).placeholder(R.drawable.placeholder).into(productImage);
            productTitle.setText(title);

            final Dialog checkCoupanPricedialog = new Dialog(itemView.getContext());
            checkCoupanPricedialog.setCancelable(false);
            checkCoupanPricedialog.setContentView(R.layout.coupan_redeem_dialog);
            checkCoupanPricedialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (codAvaiiable) {
                cod.setVisibility(View.VISIBLE);
            } else {
                cod.setVisibility(View.INVISIBLE);
            }

            if (inStock) {

                if (freecoupansNo > 0) {
                    freeCoupans.setVisibility(View.VISIBLE);
                    freeCoupanIcon.setVisibility(View.VISIBLE);
                    if (freecoupansNo == 1) {
                        freeCoupans.setText("Free " + freecoupansNo + " Coupen");
                    } else {
                        freeCoupans.setText("Free " + freecoupansNo + " Coupens");
                    }
                } else {
                    freeCoupans.setVisibility(View.INVISIBLE);
                    freeCoupanIcon.setVisibility(View.INVISIBLE);
                }

                productPrice.setText(convertToVietnameseMoney(Integer.parseInt(productPriceText)));
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText(convertToVietnameseMoney(Integer.parseInt(cuttedPriceText)));
                coupanRedemptionLayout.setVisibility(View.VISIBLE);

                ImageView toogleRecyclerView = checkCoupanPricedialog.findViewById(R.id.toggle_recyclerview);
                coupanRecyclerview = checkCoupanPricedialog.findViewById(R.id.coupans_recyclerView);
                selectedCoupan = checkCoupanPricedialog.findViewById(R.id.selected_coupan);
                originalPrice = checkCoupanPricedialog.findViewById(R.id.original_price);
                discountedPrice = checkCoupanPricedialog.findViewById(R.id.discounted_price);
                coupanTitle = checkCoupanPricedialog.findViewById(R.id.coupan_title);
                coupanExpiryDate = checkCoupanPricedialog.findViewById(R.id.coupan_validity);
                coupanBody = checkCoupanPricedialog.findViewById(R.id.coupan_body);
                applyCoupanBtn = checkCoupanPricedialog.findViewById(R.id.apply_btn);
                removeCoupanBtn = checkCoupanPricedialog.findViewById(R.id.remove_btn);
                applyORremoveBtnContainer = checkCoupanPricedialog.findViewById(R.id.apply_or_remove_btn_container);
                footerText = checkCoupanPricedialog.findViewById(R.id.footer_text);

                footerText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                coupanRecyclerview.setLayoutManager(linearLayoutManager);


                toogleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogRecyclerView();
                    }
                });

                productQuantity.setText("Qty: " + String.valueOf(productQty));

                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.md_amber_500));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.md_red_500)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.setCancelable(false);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.btn_ok);
                        quantityNo.setHint("Còn lại " + String.valueOf(maxQty) + " sản phẩm");

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });
                    }
                });

                if (offersAppliedNo > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(cuttedPriceText) - Long.valueOf(productPriceText));
                    offersApplied.setText("offer applied -Rs." + offerDiscountedAmount + "/-");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            } else {
                productPrice.setText("Hết hàng");
                productPrice.setTextColor(itemView.getResources().getColor(R.color.md_red_500));
                cuttedPrice.setText("");
                coupanRedemptionLayout.setVisibility(View.GONE);
                freeCoupans.setVisibility(View.INVISIBLE);
                freeCoupanIcon.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                coupansApplied.setVisibility(View.INVISIBLE);
                offersApplied.setVisibility(View.INVISIBLE);
            }

            if (showDeleteBtn) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }
        }
    }

    public class CartTotalAmoutnViewholder extends RecyclerView.ViewHolder {

        private TextView totalItems;
        private TextView totalItemsPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public CartTotalAmoutnViewholder(@NonNull View itemView) {
            super(itemView);

            totalItems = (TextView) itemView.findViewById(R.id.total_items);
            totalItemsPrice = (TextView) itemView.findViewById(R.id.total_items_price);
            deliveryPrice = (TextView) itemView.findViewById(R.id.delivery_price);
            totalAmount = (TextView) itemView.findViewById(R.id.total_price);
            savedAmount = (TextView) itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Giá (" + totalItemText + " sản phẩm)");
            totalItemsPrice.setText(convertToVietnameseMoney(totalItemPriceText));
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            } else {
                deliveryPrice.setText(convertToVietnameseMoney(Integer.parseInt(deliveryPriceText)));
            }
            totalAmount.setText(convertToVietnameseMoney(totalAmountText));
            cartTotal.setText(convertToVietnameseMoney(totalAmountText));
            savedAmount.setText("Bạn đã lưu " + savedAmountText + " vào đơn hàng này.");

            LinearLayout parent = (LinearLayout) cartTotal.getParent().getParent();
            if (totalItemPriceText == 0) {
                if (showDeleteBtn) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }

    private String convertToVietnameseMoney(int t) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        return format.format(t);
    }

    private void showDialogRecyclerView() {
        if (coupanRecyclerview.getVisibility() == View.GONE) {
            coupanRecyclerview.setVisibility(View.VISIBLE);
            selectedCoupan.setVisibility(View.GONE);
        } else {
            coupanRecyclerview.setVisibility(View.GONE);
            selectedCoupan.setVisibility(View.VISIBLE);
        }
    }
}
