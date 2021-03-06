package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DBqueries {

        public static boolean addressSelected = false;
        public static String email,fullname,profile, phone;
        public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        public static List<CategoryModel> list = new ArrayList<>();
        public static List<List<HomePageModel>> lists = new ArrayList<>();

        public static List<String> loadedCategoriesNames = new ArrayList<>();

        public static List<String> wishList = new ArrayList<>();
        public static List<WishlistModel> wishlistModelList = new ArrayList<>();

        public static List<String> myRatedIds = new ArrayList<>();
        public static List<Long> myRating = new ArrayList<>();

        public static List<String> cartLists = new ArrayList<>();
        public static List<CartItemModel> cartItemModelList = new ArrayList<>();

        public static List<RewardModel> rewardModelList = new ArrayList<>();

        public static int selectedAddress = 0;
        public static List<AddressesModel> addressesModelList = new ArrayList<>();

        public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();
        public static List<OrderItemsModel> orderItemsModelList = new ArrayList<>();

        public static List<NotificationModel> notificationModelList=new ArrayList<>();

        private static ListenerRegistration registration;


        public static void loadCategories(RecyclerView recyclerView, final Context context) {
            list.clear();
            firebaseFirestore.collection("CATEGORIES").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            list.add(new CategoryModel(documentSnapshot.getId(),documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                        }
                        CategoryAdapter categoryAdapter = new CategoryAdapter(list);
                        recyclerView.setAdapter(categoryAdapter);
                        categoryAdapter.notifyDataSetChanged();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public static void loadFragmentData(RecyclerView recyclerView, final Context context, final int index, String categoryName) {
            firebaseFirestore.collection("CATEGORIES")
                    .document(categoryName.toUpperCase())
                    .collection("TOP_DEALS")
                    .orderBy("index")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if ((long) documentSnapshot.get("view_type") == 0) {
                                        List<SliderModel> sliderModelList = new ArrayList<>();
                                        long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                        for (long x = 1; x < no_of_banners + 1; x++) {
                                            sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(),
                                                    documentSnapshot.get("banner_" + x + "_background").toString()));
                                        }
                                        lists.get(index).add(new HomePageModel(0, sliderModelList));
                                    } else if ((long) documentSnapshot.get("view_type") == 1) {

                                    } else if ((long) documentSnapshot.get("view_type") == 2) {
                                        firebaseFirestore.collection("CATEGORIES")
                                                .document("HOME")
                                                .collection("TOP_DEALS")
                                                .document(documentSnapshot.getId())
                                                .collection("ITEMS")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                                                            List<HorizontalProductScrollModel> productlist = new ArrayList<>();
                                                            for (DocumentSnapshot snapshot: task.getResult()) {
                                                                firebaseFirestore.collection("PRODUCTS").document(snapshot.getId())
                                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            productlist.add(new HorizontalProductScrollModel(
                                                                                    task.getResult().getId(),
                                                                                    task.getResult().get("product_image").toString(),
                                                                                    task.getResult().get("product_title").toString(),
                                                                                    task.getResult().get("product_description").toString(),
                                                                                    task.getResult().get("product_price").toString()));
                                                                            viewAllProductList.add(new WishlistModel(
                                                                                    task.getResult().getId(),
                                                                                    task.getResult().get("product_image").toString(),
                                                                                    task.getResult().get("product_title").toString(),
                                                                                    task.getResult().get("average_ratings").toString(),
                                                                                    (long) task.getResult().get("total_ratings"),
                                                                                    task.getResult().get("product_price").toString(),
                                                                                    task.getResult().get("cutted_price").toString(),
                                                                                    (boolean) task.getResult().get("COD"),
                                                                                    (boolean) task.getResult().get("in_stock")
                                                                            ));
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), productlist, viewAllProductList));
                                                        }
                                                    }
                                                });
                                    }
                                }
                                HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                                recyclerView.setAdapter(homePageAdapter);
                                homePageAdapter.notifyDataSetChanged();
                                HomeFragment.swipeRefreshLayout.setRefreshing(false);
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public static void loadCategoryActivityData(RecyclerView recyclerView, final Context context, final int index, String id) {
            firebaseFirestore.collection("CATEGORIES")
                    .document(id)
                    .collection("BRAND")
                    .orderBy("index")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    firebaseFirestore.collection("CATEGORIES")
                                            .document(id)
                                            .collection("BRAND")
                                            .document(documentSnapshot.getId())
                                            .collection("ITEMS")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        List<WishlistModel> viewAllProductList = new ArrayList<>();
                                                        List<HorizontalProductScrollModel> productlist = new ArrayList<>();
                                                        for (DocumentSnapshot snapshot: task.getResult()) {
                                                            firebaseFirestore.collection("PRODUCTS").document(snapshot.getId())
                                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        productlist.add(new HorizontalProductScrollModel(
                                                                                task.getResult().getId(),
                                                                                task.getResult().get("product_image").toString(),
                                                                                task.getResult().get("product_title").toString(),
                                                                                task.getResult().get("product_description").toString(),
                                                                                task.getResult().get("product_price").toString()));
                                                                        viewAllProductList.add(new WishlistModel(
                                                                                task.getResult().getId(),
                                                                                task.getResult().get("product_image").toString(),
                                                                                task.getResult().get("product_title").toString(),
                                                                                task.getResult().get("average_ratings").toString(),
                                                                                (long) task.getResult().get("total_ratings"),
                                                                                task.getResult().get("product_price").toString(),
                                                                                task.getResult().get("cutted_price").toString(),
                                                                                (boolean) task.getResult().get("COD"),
                                                                                (boolean) task.getResult().get("in_stock")
                                                                        ));
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), productlist, viewAllProductList));
                                                    }
                                                }
                                            });
                                }
                                HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                                recyclerView.setAdapter(homePageAdapter);
                                homePageAdapter.notifyDataSetChanged();
                                HomeFragment.swipeRefreshLayout.setRefreshing(false);
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public static void loadWishList(Context context, Dialog dialog, final boolean loadProductData) {
            wishList.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_WISHLIST").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            wishList.add(String.valueOf(task.getResult().get("product_ID_" + x)));

                            if (DBqueries.wishList.contains(ProductDetailsActivity.productID)) {
                                ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                                if (ProductDetailsActivity.addWishList != null) {
                                    ProductDetailsActivity.addWishList.setImageResource(R.drawable.like);
                                }
                            } else {
                                if (ProductDetailsActivity.addWishList != null) {
                                    ProductDetailsActivity.addWishList.setImageResource(R.drawable.heart);
                                }
                                ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                            }

                            if (loadProductData) {
                                wishlistModelList.clear();
                                final String productID = task.getResult().get("product_ID_" + x).toString();
                                firebaseFirestore.collection("PRODUCTS").document(task.getResult().get("product_ID_" + x).toString())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            wishlistModelList.add(new WishlistModel(
                                                    productID,
                                                    task.getResult().get("product_image").toString(),
                                                    task.getResult().get("product_title").toString(),
                                                    task.getResult().get("average_ratings").toString(),
                                                    (long) task.getResult().get("total_ratings"),
                                                    task.getResult().get("product_price").toString(),
                                                    task.getResult().get("cutted_price").toString(),
                                                    (boolean) task.getResult().get("COD")
                                                    ,(boolean) task.getResult().get("in_stock")
                                            ));
                                            MyWishlistFragment.wishListAdapter.notifyDataSetChanged();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
        }

        public static void removeFromWishList(int index, Context context) {
            String removedProductID = wishList.get(index);
            wishList.remove(index);
            Map<String, Object> updateWishList = new HashMap<>();

            for (int x = 0; x < wishList.size(); x++) {
                updateWishList.put("product_ID_" + x, wishList.get(x));
            }
            updateWishList.put("list_size", (long) wishList.size());

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_WISHLIST")
                    .set(updateWishList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (wishlistModelList.size() != 0) {
                            wishlistModelList.remove(index);
                            MyWishlistFragment.wishListAdapter.notifyDataSetChanged();
                        }
                        ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        Toasty.success(context, "Đã xóa thành công !!!", Toasty.LENGTH_SHORT).show();
                    } else {
                        if (ProductDetailsActivity.addWishList != null) {
                            ProductDetailsActivity.addWishList.setImageResource(R.drawable.like);
                        }
                        wishList.add(index, removedProductID);
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_wishlist_query = false;
                }
            });
        }

        public static void loadCartList(final Context context, Dialog dialog, boolean loadProductData, final TextView badge_count, final TextView cartTotal) {
            cartLists.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_CART").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            cartLists.add(task.getResult().get("product_ID_" + x).toString());

                            if (DBqueries.cartLists.contains(ProductDetailsActivity.productID)) {
                                ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                            } else {
                                ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                            }

                            if (loadProductData) {
                                cartItemModelList.clear();
                                final String productID = task.getResult().get("product_ID_" + x).toString();
                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            final DocumentSnapshot documentSnapshot = task.getResult();
                                            firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                int index = 0;
                                                                if (cartLists.size() >= 100) {
                                                                    index = cartLists.size() - 100;
                                                                }

                                                                if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                    cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM,
                                                                            productID,
                                                                            documentSnapshot.get("product_image").toString(),
                                                                            documentSnapshot.get("product_title").toString(),
                                                                            documentSnapshot.get("product_price").toString(),
                                                                            documentSnapshot.get("cutted_price").toString(),
                                                                            (long) 1,
                                                                            (long) documentSnapshot.get("offers_applied"),
                                                                            (long) 0,
                                                                            (long) documentSnapshot.get("max_quantity"),
                                                                            (long) documentSnapshot.get("stock_quantity"),
                                                                            true,
                                                                            (boolean) documentSnapshot.get("COD")
                                                                    ));
                                                                } else {
                                                                    cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM,
                                                                            productID,
                                                                            documentSnapshot.get("product_image").toString(),
                                                                            documentSnapshot.get("product_title").toString(), documentSnapshot.get("product_price").toString(),
                                                                            documentSnapshot.get("cutted_price").toString(),
                                                                            (long) 1,
                                                                            (long) documentSnapshot.get("offers_applied"),
                                                                            (long) 0,
                                                                            (long) documentSnapshot.get("max_quantity"),
                                                                            (long) documentSnapshot.get("stock_quantity"),
                                                                            true,
                                                                            (boolean) documentSnapshot.get("COD")
                                                                    ));
                                                                }
                                                                if (cartLists.size() == 1) {
                                                                    cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                                    LinearLayout parent = (LinearLayout) cartTotal.getParent().getParent();
                                                                    parent.setVisibility(View.VISIBLE);
                                                                }
                                                                if (cartLists.size() == 0) {
                                                                    cartItemModelList.clear();
                                                                }
                                                                MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        if (cartLists.size() != 0) {
                            badge_count.setVisibility(View.VISIBLE);
                        } else {
                            badge_count.setVisibility(View.INVISIBLE);
                        }
                        if (DBqueries.cartLists.size() < 10) {
                            badge_count.setText(String.valueOf(DBqueries.cartLists.size()));
                        } else {
                            badge_count.setText("10");
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            });
        }

        public static void removeFromCart(int index, Context context, final TextView cartTotal) {
            final String removedProductID = cartLists.get(index);
            cartLists.remove(index);
            Map<String, Object> updateCartList = new HashMap<>();

            for (int x = 0; x < cartLists.size(); x++) {
                updateCartList.put("product_ID_" + x, cartLists.get(x));
            }
            updateCartList.put("list_size", (long) cartLists.size());

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (cartItemModelList.size() != 0) {
                            cartItemModelList.remove(index);
                            MyCartFragment.cartAdapter.notifyDataSetChanged();
                        }
                        if (cartLists.size() == 0) {
                            LinearLayout parent = (LinearLayout) cartTotal.getParent().getParent();
                            parent.setVisibility(View.GONE);
                            cartItemModelList.clear();
                        }
                        Toasty.success(context, "Đã xóa thành công !!!", Toasty.LENGTH_SHORT).show();
                    } else {
                        cartLists.add(index, removedProductID);
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_cart_query = false;
                }
            });
        }

        public static void loadRewards(final Context context, final Dialog loadingDialog, final boolean onRewardFragment){
            rewardModelList.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                final Date lastseendate=task.getResult().getDate("Last seen");
                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){

                                                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                                        if(documentSnapshot.get("type").toString().equals("Discount") && lastseendate.before(documentSnapshot.getDate("validaty"))){
                                                            rewardModelList.add(new RewardModel(
                                                                    documentSnapshot.getId()
                                                                    ,documentSnapshot.get("type").toString()
                                                                    ,documentSnapshot.get("upper_limit").toString()
                                                                    ,documentSnapshot.get("lower_limit").toString()
                                                                    ,documentSnapshot.get("percentage").toString()
                                                                    ,documentSnapshot.get("body").toString()
                                                                    ,documentSnapshot.getTimestamp("validaty").toDate()
                                                                    ,(boolean)documentSnapshot.get("already_used")
                                                            ));
                                                        }else if(documentSnapshot.get("type").toString().equals("Cashback") && lastseendate.before(documentSnapshot.getDate("validaty"))){
                                                            rewardModelList.add(new RewardModel(
                                                                    documentSnapshot.getId()
                                                                    ,documentSnapshot.get("type").toString()
                                                                    ,documentSnapshot.get("upper_limit").toString()
                                                                    ,documentSnapshot.get("lower_limit").toString()
                                                                    ,documentSnapshot.get("amount").toString()
                                                                    ,documentSnapshot.get("body").toString()
                                                                    ,documentSnapshot.getTimestamp("validaty").toDate()
                                                                    ,(boolean)documentSnapshot.get("already_used")
                                                            ));
                                                        }
                                                    }
                                                    if(onRewardFragment) {
                                                        MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();
                                                    }
                                                }else {
                                                    String error=task.getException().getMessage();
                                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });

                            } else {
                                loadingDialog.dismiss();
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public static void loadAddresses(final Context context, Dialog loadDialog, final boolean gotoDeliveryActivity) {
            addressesModelList.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_ADDRESSES").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if ((long) task.getResult().get("list_size") == 0) {
                            context.startActivity(new Intent(context, AddAddressActivity.class).putExtra("INTENT", "deliveryIntent"));
                        } else {
                            for (long x = 1; x <= (long) task.getResult().get("list_size"); x++) {
                                addressesModelList.add(new AddressesModel(
                                        task.getResult().get("name_" + x).toString()
                                        , task.getResult().get("phone_number_" + x).toString()
                                        , task.getResult().get("city_" + x).toString()
                                        , task.getResult().get("district_" + x).toString()
                                        , task.getResult().get("ward_" + x).toString()
                                        , task.getResult().get("address_" + x).toString()
                                        , (boolean) task.getResult().get("selected_" + x)
                                ));
                                if ((boolean) task.getResult().get("selected_" + x)) {
                                    selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                                }
                            }
                            if (gotoDeliveryActivity) {
                                context.startActivity(new Intent(context, DeliveryActivity.class));
                            }
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    loadDialog.dismiss();
                }
            });

        }

        public static void loadRatingList(Context context) {
            if (!ProductDetailsActivity.running_rating_query) {
                ProductDetailsActivity.running_rating_query = true;
                myRatedIds.clear();
                myRating.clear();
                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                        .document("MY_RATINGS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> orderProductIds=new ArrayList<>();
                            for (int x = 0; x < myOrderItemModelList.size(); x++) {
                                orderProductIds.add(myOrderItemModelList.get(x).getProductId());
                            }

                            for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                                myRating.add((long) task.getResult().get("rating_" + x));
                                if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                                    ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x)));
                                    if (ProductDetailsActivity.rateNowContainer != null) {
                                        ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                    }
                                }

                                if(orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())){
                                    myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                                }
                            }
                            if(MyOrdersFragment.myOrderAdapter != null){
                                MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_rating_query = false;
                    }
                });
            }
        }

        public static void loadOrders(final Context context, @Nullable final MyOrderAdapter myOrderAdapter, final Dialog loadingDialog) {
            myOrderItemModelList.clear();
            orderItemsModelList.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!task.getResult().isEmpty()) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                        ArrayList<ProductsInOrderModel> products = new ArrayList<>();
                                        firebaseFirestore.collection("ORDERS").document(documentSnapshot.get("order_id").toString()).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            DocumentSnapshot snapshot = task.getResult();
                                                            firebaseFirestore.collection("ORDERS").document(documentSnapshot.get("order_id").toString())
                                                                    .collection("ORDER_ITEMS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if(task.isSuccessful()) {
                                                                        MyOrderItemModel myOrderItemModel = null;
                                                                        for (DocumentSnapshot snapshot1 : task.getResult()) {
                                                                            products.add(new ProductsInOrderModel(
                                                                                    snapshot1.get("Product_Image").toString(),
                                                                                    snapshot1.get("Product_Title").toString(),
                                                                                    snapshot1.get("Product_Price").toString(),
                                                                                    (long)snapshot1.get("Product_quantity"),
                                                                                    snapshot1.get("Cutted_Price").toString()));
                                                                            myOrderItemModel = new MyOrderItemModel(
                                                                                    snapshot1.getString("Product_Id")
                                                                                    , snapshot.getString("Order_Status")
                                                                                    , snapshot.getString("Address")
                                                                                    , snapshot1.getString("Coupan_Id")
                                                                                    , snapshot1.getString("Product_Price")
                                                                                    , snapshot1.getString("Cutted_Price")
                                                                                    , snapshot.get("Discounted_Price").toString()
                                                                                    , (Date) snapshot.getDate("Ordered_Date")
                                                                                    , (Date) snapshot.getDate("Packed_Date")
                                                                                    , (Date) snapshot.getDate("Shipped_Date")
                                                                                    , (Date) snapshot.getDate("Delivered_Date")
                                                                                    , (Date) snapshot.getDate("Cancelled_Date")
                                                                                    , snapshot.getLong("Free_Coupens")
                                                                                    , snapshot1.getLong("Product_quantity")
                                                                                    , snapshot.getString("FullName")
                                                                                    , snapshot1.getString("ORDER_ID")
                                                                                    , snapshot.getString("Payment_Method")
                                                                                    , snapshot.getString("Pincode")
                                                                                    , snapshot.getString("User_Id")
                                                                                    , snapshot1.getString("Product_Title")
                                                                                    , snapshot1.getString("Product_Image")
                                                                                    , snapshot.getString("Delivery_Price")
                                                                                    , products
                                                                                    );
                                                                        }
                                                                        myOrderItemModelList.add(myOrderItemModel);
                                                                        loadRatingList(context);
                                                                        if (myOrderAdapter != null) {
                                                                            myOrderAdapter.notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            String error = task.getException().getMessage();
                                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                        }
                                                        loadingDialog.dismiss();
                                                    }
                                                });

                                    }
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                loadingDialog.dismiss();
                            }
                        }
                    });

        }

        public static void clearData() {
            list.clear();
            lists.clear();
            loadedCategoriesNames.clear();
            wishList.clear();
            wishlistModelList.clear();
            cartLists.clear();
            cartItemModelList.clear();
            myRatedIds.clear();
            myRating.clear();
            addressesModelList.clear();
            rewardModelList.clear();
            myOrderItemModelList.clear();
        }

        public static void checkNotifications(boolean remove,@Nullable final TextView notifycount){
            if(remove){
                registration.remove();
            }
            else {
                registration=firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATIONS")
                        .collection("MY_ORDER_NOTIFICATIONS")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                notificationModelList.clear();
                                int unread=0;
                                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                                    notificationModelList.add(0,new NotificationModel(
                                            documentSnapshot.getId()
                                            ,documentSnapshot.getString("image")
                                            ,documentSnapshot.getString("content")
                                            ,documentSnapshot.getBoolean("readed")
                                    ));
                                    if(!documentSnapshot.getBoolean("readed")){
                                        unread++;
                                        if(notifycount != null){
                                            if(unread>0) {
                                                notifycount.setVisibility(View.VISIBLE);
                                                if (unread < 99) {
                                                    notifycount.setText(String.valueOf(unread));
                                                } else {
                                                    notifycount.setText("99");
                                                }
                                            }else {
                                                notifycount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                                if(NotificationActivity.adapter != null){
                                    NotificationActivity.adapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        }
    }
