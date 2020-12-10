package com.example.galaxytechstore.ui.home;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galaxytechstore.CategoryAdapter;
import com.example.galaxytechstore.CategoryModel;
import com.example.galaxytechstore.DBqueries;
import com.example.galaxytechstore.HomePageAdapter;
import com.example.galaxytechstore.HomePageModel;
import com.example.galaxytechstore.HorizontalProductScrollModel;
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
    private HomeViewModel homeViewModel;
    private List<CategoryModel> categoryModelList = new ArrayList<>();
    private List<HomePageModel> homePageModelList = new ArrayList<>();
    private RecyclerView categoryRecyclerView;
    private RecyclerView homepageRecyclerView;
    private CategoryAdapter categoryAdapter;
    private HomePageAdapter homePageAdapter;
    private ImageView noInternet;
    private Button retryBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        noInternet = (ImageView) root.findViewById(R.id.no_internet_connection);
        categoryRecyclerView = (RecyclerView) root.findViewById(R.id.list_cate);
        homepageRecyclerView = (RecyclerView) root.findViewById(R.id.home_page_recyclerview);
        retryBtn = (Button) root.findViewById(R.id.retry_btn);
        noInternet.setVisibility(View.GONE);
        retryBtn.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.md_red_500),getContext().getResources().getColor(R.color.md_green_500));


        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        homepageRecyclerView.setLayoutManager(layoutManager2);

        categoryAdapter = new CategoryAdapter(categoryModelList);
        DBqueries.loadCategories(categoryRecyclerView, getContext());
        categoryRecyclerView.setAdapter(categoryAdapter);

        homePageAdapter = new HomePageAdapter(homePageModelList);
        loadedCategoriesNames.add("HOME");
        DBqueries.lists.add(new ArrayList<HomePageModel>());
        DBqueries.loadFragmentData(homepageRecyclerView, getContext(), 0, "home");
        homePageAdapter.notifyDataSetChanged();

        return root;
    }
}