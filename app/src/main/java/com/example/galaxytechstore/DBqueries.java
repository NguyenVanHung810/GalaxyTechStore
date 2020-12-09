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

    }
