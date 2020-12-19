package com.example.galaxytechstore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;
    public static Activity productDetailsActivity;
    private Long productPriceValue;


    private ViewPager productImagesViewPaper;
    private TextView productTitleCart;
    private TextView averageRatingMiniView;
    private TextView ProducttotalRatings;
    private TextView productPrice;
    private TextView cuttedPrice;
    private ImageView codeIndicator;
    private TextView tvcodeIndicator;
    private TabLayout viewPaperIndicator;
    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton addWishList;
    private ViewPager productDetailsViewPaper;
    private TabLayout productDetailsTabLayout;


    private TextView rewardTitle;
    private TextView rewardbody;

    private TextView productDescriptionBody;

    private FirebaseUser currentUser;

    public static MenuItem cartItem;

    //private ConstraintLayout productDetailsOnly;
    private ConstraintLayout productDetailsTabsContainer;
    private ConstraintLayout productDetailsOnlyContainer;
    private ViewPager productDetailsViewpaper;
    private TabLayout productDetailsTablayout;
    private String productDescription;
    private String productOtherDetails;
    private List<ProductSpecificationModel> list = new ArrayList<>();

    private LinearLayout coupenlayout;

    private TextView totalRating;
    private LinearLayout ratingNumberContainer;
    private LinearLayout ratingsProgessBarContainer;
    private TextView averageRatings;
    // rating layout
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private Button buynow_btn;
    private Dialog signInDialog;
    private Dialog loaddialog;
    private LinearLayout addtocart;
    private String productOriginPrice;
    private boolean inStock = false;

    private Button coupanRedeemButton;

    private DocumentSnapshot documentSnapshot;

    private FirebaseFirestore firebaseFirestore;
    public static String productID;

    private TextView badge_count;

    public static boolean fromSearch = false;

    /////coupan dialog
    private TextView coupanTitle,discountedPrice,originalPrice;
    private TextView coupanExpiryDate;
    private TextView coupanBody;
    private RecyclerView coupanRecyclerview;
    private LinearLayout selectedCoupan;
    private LinearLayout coupanRedemLayout;
    /////coupan dialog


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            coupenlayout.setVisibility(View.GONE);
        } else {
            coupenlayout.setVisibility(View.VISIBLE);
        }

        if (currentUser != null) {
            if (DBqueries.myRating.size() == 0) {
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductDetailsActivity.this, loaddialog, false);
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            } else {
                loaddialog.dismiss();
            }
        } else {
            loaddialog.dismiss();
        }

        if (DBqueries.myRatedIds.contains(productID)) {
            int index = DBqueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        if (DBqueries.myRatedIds.contains(productID)) {
            int index = DBqueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        if (DBqueries.cartLists.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (DBqueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addWishList.setImageResource(R.drawable.like);

        } else {
            addWishList.setImageResource(R.drawable.heart);
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPaper = (ViewPager) findViewById(R.id.product_images_viewpaper);
        viewPaperIndicator = (TabLayout) findViewById(R.id.viewpaper_indicator);
        addWishList = (FloatingActionButton) findViewById(R.id.add_to_wishlist_btn);
        productDetailsViewPaper = (ViewPager) findViewById(R.id.product_details_viewpaper);
        productDetailsTabLayout = (TabLayout) findViewById(R.id.product_details_tablayout);
        buynow_btn = (Button) findViewById(R.id.buy_now_btn);
        coupanRedeemButton = findViewById(R.id.coupan_redeem_btn);
        productTitleCart = (TextView) findViewById(R.id.product_title_cart);
        averageRatingMiniView = (TextView) findViewById(R.id.rating_wishlist_item);
        ProducttotalRatings = (TextView) findViewById(R.id.total_rating_miniview);
        productPrice = (TextView) findViewById(R.id.product_price);
        cuttedPrice = (TextView) findViewById(R.id.cutted_price_details);
        tvcodeIndicator = (TextView) findViewById(R.id.tv_cod_indicator);
        codeIndicator = (ImageView) findViewById(R.id.code_indicator);
        rewardTitle = (TextView) findViewById(R.id.reward_title);
        rewardbody = (TextView) findViewById(R.id.reward_body);
        productDetailsTabsContainer = (ConstraintLayout) findViewById(R.id.product_details_tabs_container);
        productDetailsOnlyContainer = (ConstraintLayout) findViewById(R.id.product_details_container);
        productDescriptionBody = (TextView) findViewById(R.id.product_details_body);
        totalRating = (TextView) findViewById(R.id.total_rating);
        ratingNumberContainer = (LinearLayout) findViewById(R.id.ratings_number_container);
        ratingsProgessBarContainer = (LinearLayout) findViewById(R.id.ratings_progressbar_container);
        averageRatings = (TextView) findViewById(R.id.average_rating);
        addtocart = (LinearLayout) findViewById(R.id.add_to_cart_btn);
        coupenlayout = (LinearLayout) findViewById(R.id.coupen_redemption_layout);

        firebaseFirestore = FirebaseFirestore.getInstance();
        initialRating = -1;

        //Loading dialog
        loaddialog = new Dialog(ProductDetailsActivity.this);
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();
        //

        ////// coupan redemption dialog

        final Dialog checkCoupanPricedialog = new Dialog(ProductDetailsActivity.this);
        checkCoupanPricedialog.setCancelable(true);
        checkCoupanPricedialog.setContentView(R.layout.coupan_redeem_dialog);
        checkCoupanPricedialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toogleRecyclerView = checkCoupanPricedialog.findViewById(R.id.toggle_recyclerview);
        coupanRecyclerview = checkCoupanPricedialog.findViewById(R.id.coupans_recyclerView);
        selectedCoupan = checkCoupanPricedialog.findViewById(R.id.selected_coupan);
        originalPrice = checkCoupanPricedialog.findViewById(R.id.original_price);
        discountedPrice = checkCoupanPricedialog.findViewById(R.id.discounted_price);
        coupanTitle = checkCoupanPricedialog.findViewById(R.id.coupan_title);
        coupanExpiryDate = checkCoupanPricedialog.findViewById(R.id.coupan_validity);
        coupanBody = checkCoupanPricedialog.findViewById(R.id.coupan_body);

        originalPrice.setText(productPrice.getText());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        coupanRecyclerview.setLayoutManager(linearLayoutManager);

        MyRewardsAdapter rewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList, true,coupanRecyclerview,selectedCoupan,productPriceValue,coupanTitle,coupanExpiryDate,coupanBody,discountedPrice);
        rewardsAdapter.notifyDataSetChanged();
        coupanRecyclerview.setAdapter(rewardsAdapter);

        toogleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDialogRecyclerView();
            }
        });

        coupanRedeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCoupanPricedialog.show();
            }
        });

        ////// coupan redemption dialog

        List<String> productImages = new ArrayList<>();
        productID = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();
                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        for (long i = 1; i < (long) documentSnapshot.get("no_of_images") + 1; i++) {
                                            productImages.add(documentSnapshot.get("product_image_" + i).toString());
                                        }
                                        ProductImagesAdapter adapter = new ProductImagesAdapter(productImages);
                                        productImagesViewPaper.setAdapter(adapter);
                                        productTitleCart.setText(documentSnapshot.get("product_title").toString());
                                        averageRatingMiniView.setText(documentSnapshot.get("average").toString());
                                        ProducttotalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");
                                        productPrice.setText(documentSnapshot.get("product_price").toString());
                                        productOriginPrice= documentSnapshot.get("product_price").toString();
                                        cuttedPrice.setText(documentSnapshot.get("cutted_price").toString());
                                        if ((boolean) documentSnapshot.get("COD")) {
                                            tvcodeIndicator.setVisibility(View.VISIBLE);
                                            codeIndicator.setVisibility(View.VISIBLE);
                                        } else {
                                            tvcodeIndicator.setVisibility(View.INVISIBLE);
                                            tvcodeIndicator.setVisibility(View.INVISIBLE);
                                        }
                                        rewardTitle.setText((long) documentSnapshot.get("free_coupens") + " " + documentSnapshot.get("free_coupen_title").toString());
                                        rewardbody.setText(documentSnapshot.get("free_coupen_body").toString());
                                        if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                            productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                            productDetailsOnlyContainer.setVisibility(View.GONE);
                                            productDescription = documentSnapshot.get("product_description").toString();
                                            productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                            list = new ArrayList<>();

                                            for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                                list.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                                for (long i = 1; i < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; i++) {
                                                    list.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_fields_" + i + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_fields_" + i + "_value").toString()));

                                                }
                                            }
                                        } else {
                                            productDetailsTabsContainer.setVisibility(View.GONE);
                                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                            productDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                        }
                                        totalRating.setText((long) documentSnapshot.get("total_ratings") + "");
                                        for (int x = 0; x < 5; x++) {
                                            TextView rating = (TextView) ratingNumberContainer.getChildAt(x);
                                            rating.setText((long) documentSnapshot.get(String.valueOf(5 - x) + "_star") + "");
                                            ProgressBar progressBar = (ProgressBar) ratingsProgessBarContainer.getChildAt(x);
                                            int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                            progressBar.setMax(maxProgress);
                                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                        }
                                        averageRatingMiniView.setText(documentSnapshot.get("average").toString());
                                        averageRatings.setText(documentSnapshot.get("average").toString());
                                        productDetailsViewPaper.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, list));


                                        if (currentUser != null) {
                                            if (DBqueries.myRating.size() == 0) {
                                            }
                                            if (DBqueries.cartLists.size() == 0) {
                                                DBqueries.loadCartList(ProductDetailsActivity.this, loaddialog, false, badge_count, new TextView(ProductDetailsActivity.this));
                                            }
                                            if (DBqueries.wishList.size() == 0) {
                                                DBqueries.loadWishList(ProductDetailsActivity.this, loaddialog, false);
                                            }
                                            if(DBqueries.rewardModelList.size() == 0){
                                                DBqueries.loadRewards(ProductDetailsActivity.this, loaddialog, false);
                                            }
                                            if (DBqueries.cartLists.size() != 0 && DBqueries.wishList.size() != 0  &&  DBqueries.rewardModelList.size() != 0){
                                                loaddialog.dismiss();
                                            }

                                        } else {
                                            loaddialog.dismiss();
                                        }

                                        if (DBqueries.myRatedIds.contains(productID)) {
                                            int index = DBqueries.myRatedIds.indexOf(productID);
                                            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
                                            setRating(initialRating);
                                        }

                                        if (DBqueries.cartLists.contains(productID)) {
                                            ALREADY_ADDED_TO_CART = true;
                                        } else {
                                            ALREADY_ADDED_TO_CART = false;
                                        }

                                        if (DBqueries.wishList.contains(productID)) {
                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            addWishList.setImageResource(R.drawable.like);

                                        } else {
                                            addWishList.setImageResource(R.drawable.heart);
                                            ALREADY_ADDED_TO_WISHLIST = false;
                                        }


                                        if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                            inStock = true;
                                            buynow_btn.setVisibility(View.VISIBLE);
                                            addtocart.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (currentUser == null) {
                                                        signInDialog.show();
                                                    } else {
                                                        if (!running_cart_query) {
                                                            running_cart_query = true;
                                                            if (ALREADY_ADDED_TO_CART) {
                                                                running_cart_query = false;
                                                                Toast.makeText(getApplicationContext(), "Đã thêm vào giỏ hàng !!!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Map<String, Object> addProduct = new HashMap<>();
                                                                addProduct.put("product_ID_" + String.valueOf(DBqueries.cartLists.size()), productID);
                                                                addProduct.put("list_size", (long) (DBqueries.cartLists.size() + 1));
                                                                firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            if (DBqueries.cartItemModelList.size() != 0) {
                                                                                DBqueries.cartItemModelList.add(0, new CartItemModel(CartItemModel.CART_ITEM,
                                                                                        productID,
                                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                                        documentSnapshot.get("product_title").toString(),
                                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                                        documentSnapshot.get("product_price").toString(),
                                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                                        (long) 1,
                                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                                        (long) 0,
                                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                                        (long) documentSnapshot.get("stock_quantity"),
                                                                                        inStock,
                                                                                        (boolean) documentSnapshot.get("COD"))
                                                                                );
                                                                            }

                                                                            ALREADY_ADDED_TO_CART = true;
                                                                            DBqueries.cartLists.add(productID);
                                                                            Toast.makeText(getApplicationContext(), "Thêm vào giỏ hàng thành công !", Toast.LENGTH_SHORT).show();
                                                                            invalidateOptionsMenu();
                                                                            running_cart_query = false;
                                                                        } else {
                                                                            running_cart_query = false;
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            inStock = false;
                                            buynow_btn.setVisibility(View.GONE);
                                            TextView outofstock = (TextView) addtocart.getChildAt(0);
                                            outofstock.setText("HẾT HÀNG");
                                            outofstock.setTextColor(getResources().getColor(R.color.md_red_500));
                                            outofstock.setCompoundDrawables(null, null, null, null);
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    loaddialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPaperIndicator.setupWithViewPager(productImagesViewPaper, true);
        addWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBqueries.wishList.indexOf(productID);
                            DBqueries.removeFromWishList(index, ProductDetailsActivity.this);
                            ALREADY_ADDED_TO_WISHLIST = false;
                            addWishList.setImageResource(R.drawable.heart);
                        } else {
                            addWishList.setImageResource(R.drawable.like);
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.wishList.size()), productID);
                            addProduct.put("list_size", (long) (DBqueries.wishList.size() + 1));

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (DBqueries.wishlistModelList.size() != 0) {
                                            DBqueries.wishlistModelList.add(new WishlistModel(productID,
                                                    documentSnapshot.get("product_image_1").toString(),
                                                    documentSnapshot.get("product_title").toString(),
                                                    (long) documentSnapshot.get("free_coupens"),
                                                    documentSnapshot.get("average_rating").toString(),
                                                    (long) documentSnapshot.get("total_rating"),
                                                    documentSnapshot.get("product_price").toString(),
                                                    documentSnapshot.get("product_cutted_price").toString(),
                                                    (boolean) documentSnapshot.get("COD"),
                                                    (boolean) documentSnapshot.get("in_stock")
                                            ));
                                       }
                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        addWishList.setImageResource(R.drawable.like);
                                        DBqueries.wishList.add(productID);
                                        Toast.makeText(getApplicationContext(), "Thêm sản phẩm yêu thích thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addWishList.setImageResource(R.drawable.heart);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;
                                }
                            });
                        }
                    }
                }
            }
        });
        productDetailsViewPaper.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPaper.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initialRating = 0;
        rateNowContainer = (LinearLayout) findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starposition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starposition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starposition);
                                Map<String, Object> updateRating = new HashMap<>();

                                if (DBqueries.myRatedIds.contains(productID)) {
                                    TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starposition - 1);

                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starposition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average", calculateAverageRating((long) starposition - initialRating, false));

                                } else {
                                    updateRating.put((starposition + 1) + "_star", (long) documentSnapshot.get((starposition + 1) + "_star") + 1);
                                    updateRating.put("average", calculateAverageRating((long) starposition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }
                                firebaseFirestore.collection("PRODUCTS").document(productID).update(updateRating)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> myRating = new HashMap<>();
                                                    if (DBqueries.myRatedIds.contains(productID)) {
                                                        myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starposition + 1);
                                                    } else {
                                                        myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                                        myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                                        myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starposition + 1);
                                                    }

                                                    firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                            .document("MY_RATINGS").update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                if (DBqueries.myRatedIds.contains(productID)) {

                                                                    DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID), (long) starposition + 1);


                                                                    TextView oldRating = (TextView) ratingNumberContainer.getChildAt(5 - initialRating - 1);
                                                                    TextView finalRating = (TextView) ratingNumberContainer.getChildAt(5 - starposition - 1);
                                                                    oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                                    finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));


                                                                } else {
                                                                    DBqueries.myRatedIds.add(productID);
                                                                    DBqueries.myRating.add((long) starposition + 1);

                                                                    TextView rating = (TextView) ratingNumberContainer.getChildAt(5 - starposition - 1);
                                                                    rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                                    totalRating.setText(((long) documentSnapshot.get("total_ratings") + 1) + "");
                                                                    ProducttotalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + "ratings");

                                                                    Toast.makeText(getApplicationContext(), "Thank you for rating !!!", Toast.LENGTH_SHORT).show();
                                                                }
                                                                for (int x = 0; x < 5; x++) {
                                                                    TextView ratingfigure = (TextView) ratingNumberContainer.getChildAt(x);
                                                                    ProgressBar progressBar = (ProgressBar) ratingsProgessBarContainer.getChildAt(x);
                                                                    int maxProgress = Integer.parseInt(totalRating.getText().toString());
                                                                    progressBar.setMax(maxProgress);
                                                                    progressBar.setProgress(Integer.parseInt(ratingfigure.getText().toString()));
                                                                }
                                                                initialRating = starposition;
                                                                averageRatings.setText(calculateAverageRating(0, true));
                                                                averageRatingMiniView.setText(calculateAverageRating(0, true));

                                                                if (DBqueries.wishList.contains(productID) && DBqueries.wishlistModelList.size() != 0) {
                                                                    int index = DBqueries.wishList.indexOf(productID);
                                                                    DBqueries.wishlistModelList.get(index).setRating(averageRatings.getText().toString());
                                                                    DBqueries.wishlistModelList.get(index).setTotal_Ratings(Long.parseLong(totalRating.getText().toString()));
                                                                }
                                                            } else {
                                                                setRating(initialRating);
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            running_rating_query = false;
                                                        }
                                                    });
                                                } else {
                                                    running_rating_query = false;
                                                    setRating(initialRating);
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        }

        buynow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loaddialog.show();
                    productDetailsActivity=ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM,
                            productID,
                            documentSnapshot.get("product_image_1").toString(),
                            documentSnapshot.get("product_title").toString(),
                            (long) documentSnapshot.get("free_coupens"),
                            documentSnapshot.get("product_price").toString(),
                            documentSnapshot.get("cutted_price").toString(),
                            (long) 1,
                            (long) documentSnapshot.get("offers_applied"),
                            (long) 0,
                            (long)documentSnapshot.get("max_quantity"),
                            (long)documentSnapshot.get("stock_quantity"),
                            inStock,
                            (boolean)documentSnapshot.get("COD"))
                    );
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));


                    if (DBqueries.addressesModelList.size() == 0) {
                        DBqueries.loadAddresses(getApplicationContext(), loaddialog, true);
                    } else {
                        loaddialog.dismiss();
                        Intent deliveryIntent = new Intent(getApplicationContext(), DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button sign_in_dialog = signInDialog.findViewById(R.id.sign_in_btn);
        Button sign_up_dialog = signInDialog.findViewById(R.id.sign_up_btn);
        Intent registerIntent = new Intent(ProductDetailsActivity.this, Login_Register_ResetPasswordActivity.class);


        sign_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.diableCloseBtn = true;
                SignUpFragment.diableCloseBtn = true;
                signInDialog.dismiss();
                Login_Register_ResetPasswordActivity.setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        sign_up_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.diableCloseBtn = true;
                SignUpFragment.diableCloseBtn = true;
                signInDialog.dismiss();
                Login_Register_ResetPasswordActivity.setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

    }

    @SuppressLint("NewApi")
    public static void setRating(int starPosition) {
        if (starPosition > -1) {
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (x <= starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
                }
            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingno = (TextView) ratingNumberContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingno.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRating.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRating.getText().toString()) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.main_cart_ic);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badge_icon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badge_icon.setImageResource(R.drawable.cart_2);
        badge_count = cartItem.getActionView().findViewById(R.id.badge_count);

        if(currentUser != null){
            if (DBqueries.cartLists.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this, new Dialog(ProductDetailsActivity.this), false, badge_count, new TextView(ProductDetailsActivity.this));
            }
            else {
                badge_count.setVisibility(View.VISIBLE);
                if (DBqueries.cartLists.size() < 10){
                    badge_count.setText(String.valueOf(DBqueries.cartLists.size()));
                }
                else {
                    badge_count.setText("10");
                }
            }
        }

        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    MainActivity.showcart = true;
                    startActivity(cartIntent);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_search_ic) {
            if(fromSearch) {
                finish();
            }
            else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.main_cart_ic) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                MainActivity.showcart = true;
                startActivity(cartIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }
}