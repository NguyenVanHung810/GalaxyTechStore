package com.example.galaxytechstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.galaxytechstore.CategoryAdapter;
import com.example.galaxytechstore.CategoryModel;
import com.example.galaxytechstore.DBqueries;
import com.example.galaxytechstore.HomePageAdapter;
import com.example.galaxytechstore.HomePageModel;
import com.example.galaxytechstore.HorizontalProductScrollModel;
import com.example.galaxytechstore.MainActivity;
import com.example.galaxytechstore.R;
import com.example.galaxytechstore.SliderModel;
import com.example.galaxytechstore.WishlistModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.galaxytechstore.DBqueries.loadedCategoriesNames;

public class HomeFragment extends Fragment {

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private List<HomePageModel> homePageModelList = new ArrayList<>();
    private RecyclerView categoryRecyclerView;
    private RecyclerView homepageRecyclerView;
    private CategoryAdapter categoryAdapter;
    private HomePageAdapter homePageAdapter;
    private ImageView noInternet;
    private Button retryBtn;

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        noInternet = (ImageView) root.findViewById(R.id.no_internet_connection);
        categoryRecyclerView = (RecyclerView) root.findViewById(R.id.list_cate);
        homepageRecyclerView = (RecyclerView) root.findViewById(R.id.home_page_recyclerview);
        retryBtn = (Button) root.findViewById(R.id.retry_btn);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.md_red_500),getContext().getResources().getColor(R.color.md_green_500));

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager1.onSaveInstanceState();
        categoryRecyclerView.setLayoutManager(layoutManager1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.onSaveInstanceState();
        homepageRecyclerView.setLayoutManager(layoutManager2);

        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));
        categoryModelList.add(new CategoryModel("", "", ""));

        List<SliderModel> sliderModelList = new ArrayList<>();
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","","",""));


        homePageModelList.add(new HomePageModel(0, sliderModelList));
        homePageModelList.add(new HomePageModel(2,"trending","#dfdfdf", horizontalProductScrollModelList, new ArrayList<WishlistModel>()));

        categoryAdapter = new CategoryAdapter(categoryModelList);
        homePageAdapter = new HomePageAdapter(homePageModelList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternet.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homepageRecyclerView.setVisibility(View.VISIBLE);

            if(DBqueries.list.size() == 0){
                DBqueries.loadCategories(categoryRecyclerView, getContext());
            }
            else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);
            if(DBqueries.lists.size() == 0){
                loadedCategoriesNames.add("HOME");
                DBqueries.lists.add(new ArrayList<HomePageModel>());
                DBqueries.loadFragmentData(homepageRecyclerView, getContext(), 0,"home");
            }
            else {
                homePageAdapter = new HomePageAdapter(DBqueries.lists.get(0));
                homePageAdapter.notifyDataSetChanged();
            }
            homepageRecyclerView.setAdapter(homePageAdapter);
        }
        else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            noInternet.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            categoryRecyclerView.setVisibility(View.GONE);
            homepageRecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.no_image).into(noInternet);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });


        return root;
    }

    private void reloadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();
        DBqueries.clearData();
        if(networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternet.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homepageRecyclerView.setVisibility(View.VISIBLE);
            categoryAdapter = new CategoryAdapter(categoryModelList);
            homePageAdapter = new HomePageAdapter(homePageModelList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homepageRecyclerView.setAdapter(homePageAdapter);

            DBqueries.loadCategories(categoryRecyclerView, getContext());
            loadedCategoriesNames.add("HOME");
            DBqueries.lists.add(new ArrayList<HomePageModel>());
            DBqueries.loadFragmentData(homepageRecyclerView, getContext(), 0,"Home");
        }
        else {
            MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "No internet Connection!", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homepageRecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.no_image).into(noInternet);
            noInternet.setVisibility(View.VISIBLE);
            retryBtn.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}