package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.example.galaxytechstore.DBqueries.lists;
import static com.example.galaxytechstore.DBqueries.loadFragmentData;
import static com.example.galaxytechstore.DBqueries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private HomePageAdapter homePageAdapter;
    private List<HomePageModel> homePageAdapterList = new ArrayList<HomePageModel>();

    String title;
    String id;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        id = getIntent().getStringExtra("Cate_Id");
        title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));

        homePageAdapterList.add(new HomePageModel(2, "", "#ffffff", horizontalProductScrollModelFakeList, new ArrayList<WishlistModel>()));
        homePageAdapterList.add(new HomePageModel(2, "", "#ffffff", horizontalProductScrollModelFakeList, new ArrayList<WishlistModel>()));
        homePageAdapterList.add(new HomePageModel(2, "", "#ffffff", horizontalProductScrollModelFakeList, new ArrayList<WishlistModel>()));

        categoryRecyclerView = findViewById(R.id.category_recycler_view);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);

        homePageAdapter = new HomePageAdapter(homePageAdapterList);


        int listPosition = 0;
        for (int x = 0; x < loadedCategoriesNames.size(); x++) {
            if (loadedCategoriesNames.get(x).equals(title.toUpperCase())) {
                listPosition = x;
            }
        }

        if (listPosition == 0) {
            loadedCategoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            DBqueries.loadCategoryActivityData(categoryRecyclerView, this, loadedCategoriesNames.size() - 1, id);
        } else {
            homePageAdapter = new HomePageAdapter(lists.get(listPosition));
        }

        categoryRecyclerView.setAdapter(homePageAdapter);
        homePageAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_search_ic) {
            startActivity(new Intent(CategoryActivity.this, SearchActivity.class));
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}