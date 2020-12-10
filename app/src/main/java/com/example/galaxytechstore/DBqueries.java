package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galaxytechstore.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class DBqueries {

        public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        public static List<CategoryModel> list = new ArrayList<>();
        public static List<List<HomePageModel>> lists = new ArrayList<>();
        public static List<String> loadedCategoriesNames = new ArrayList<>();
        public static List<String> wishList = new ArrayList<>();
        //public static List<WishlistModel> wishlistModelList = new ArrayList<>();
        public static List<String> myRatedIds = new ArrayList<>();
        public static List<Long> myRating = new ArrayList<>();
        public static List<String> cartLists = new ArrayList<>();
        //public static List<CartItemModel> cartItemModelList = new ArrayList<>();

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
                                                    (boolean) documentSnapshot.get("COD_" + x)
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


    }
