package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galaxytechstore.ui.home.HomeFragment;
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

    public class DBqueries {

        public static String email,fullname,profile;
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

        public static List<NotificationModel> notificationModelList=new ArrayList<>();

        private static ListenerRegistration registration;


        public static void loadCategories(RecyclerView recyclerView, final Context context) {
            list.clear();
            firebaseFirestore.collection("CATEGORIES").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            list.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
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
//                                    homePageModelList.add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(),
//                                            documentSnapshot.get("background").toString()));
                                    } else if ((long) documentSnapshot.get("view_type") == 2) {
                                        List<WishlistModel> viewAllProductList = new ArrayList<>();
                                        List<HorizontalProductScrollModel> productlist = new ArrayList<>();
                                        long no_of_banners = (long) documentSnapshot.get("no_of_products");
                                        for (long x = 1; x < no_of_banners + 1; x++) {
                                            productlist.add(new HorizontalProductScrollModel(
                                                    documentSnapshot.get("product_id_" + x).toString(),
                                                    documentSnapshot.get("product_image_" + x).toString(),
                                                    documentSnapshot.get("product_title_" + x).toString(),
                                                    documentSnapshot.get("product_subtitle_" + x).toString(),
                                                    documentSnapshot.get("product_price_" + x).toString()));
                                            viewAllProductList.add(new WishlistModel(
                                                    documentSnapshot.get("product_id_" + x).toString(),
                                                    documentSnapshot.get("product_image_" + x).toString(),
                                                    documentSnapshot.get("product_full_title_" + x).toString(),
                                                    (long) documentSnapshot.get("free_coupens_" + x),
                                                    documentSnapshot.get("average_rating_" + x).toString(),
                                                    (long) documentSnapshot.get("total_rating_" + x),
                                                    documentSnapshot.get("product_price_" + x).toString(),
                                                    documentSnapshot.get("product_cutted_price_" + x).toString(),
                                                    (boolean) documentSnapshot.get("COD_" + x),
                                                    (boolean) documentSnapshot.get("in_stock_"+x)
                                            ));
                                        }
                                        lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), productlist, viewAllProductList));
                                    } else if ((long) documentSnapshot.get("view_type") == 3) {
                                        List<HorizontalProductScrollModel> productlist = new ArrayList<>();
                                        long no_of_banners = (long) documentSnapshot.get("no_of_products");
                                        for (long x = 1; x < no_of_banners + 1; x++) {
                                            productlist.add(new HorizontalProductScrollModel(
                                                    documentSnapshot.get("product_id_" + x).toString(),
                                                    documentSnapshot.get("product_image_" + x).toString(),
                                                    documentSnapshot.get("product_title_" + x).toString(),
                                                    documentSnapshot.get("product_subtitle_" + x).toString(),
                                                    documentSnapshot.get("product_price_" + x).toString()));
                                        }
                                        lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), productlist));
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
                                                    task.getResult().get("product_image_1").toString(),
                                                    task.getResult().get("product_title").toString(),
                                                    (long) task.getResult().get("free_coupens"),
                                                    task.getResult().get("average").toString(),
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
                        Toast.makeText(context, "Đã xóa thành công !!!", Toast.LENGTH_SHORT).show();
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
                            cartLists.add(String.valueOf(task.getResult().get("product_ID_" + x).toString()));

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
                                                                if (cartLists.size() >= 2) {
                                                                    index = cartLists.size() - 2;
                                                                }

                                                                if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                                                    cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM,
                                                                            productID,
                                                                            documentSnapshot.get("product_image_1").toString(),
                                                                            documentSnapshot.get("product_title").toString(),
                                                                            (long) documentSnapshot.get("free_coupens"),
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
                                                                            documentSnapshot.get("product_image_1").toString(),
                                                                            documentSnapshot.get("product_title").toString(),
                                                                            (long) documentSnapshot.get("free_coupens"),
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

        public static void clearData() {
            list.clear();
            lists.clear();
            loadedCategoriesNames.clear();
            wishList.clear();
            wishlistModelList.clear();
        }

        public static void checkNotifications(boolean remove,@Nullable final TextView notifycount){
            if(remove){
                registration.remove();
            }else {
                registration=firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATIONS")
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                if(documentSnapshot != null && documentSnapshot.exists()) {
                                    notificationModelList.clear();
                                    int unread=0;
                                    for (long x = 0; x < (long) documentSnapshot.get("list_size"); x++) {
                                        notificationModelList.add(0,new NotificationModel(
                                                documentSnapshot.getString("image_"+x)
                                                ,documentSnapshot.getString("body_"+x)
                                                ,documentSnapshot.getBoolean("readed_"+x)
                                        ));
                                        if(!documentSnapshot.getBoolean("readed_"+x)){
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
                            }
                        });
            }
        }
    }
