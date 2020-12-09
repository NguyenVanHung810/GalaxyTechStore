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
        categoryRecyclerView = (RecyclerView) root.findViewById(R.id.list_cate);
        homepageRecyclerView = (RecyclerView) root.findViewById(R.id.home_page_recyclerview);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager1);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        homepageRecyclerView.setLayoutManager(layoutManager2);

        categoryModelList.add(new CategoryModel("https://www.phucanh.vn/media/news/3012_csm_4zu3_Asus_Zenbook_UX3430UQ_Teaser_7c44e97fc6Top10Laptopttnhtchochnhsanhvbintpvideonm2017.jpg", "Home"));
        categoryModelList.add(new CategoryModel("https://www.phucanh.vn/media/news/3012_csm_4zu3_Asus_Zenbook_UX3430UQ_Teaser_7c44e97fc6Top10Laptopttnhtchochnhsanhvbintpvideonm2017.jpg", "Laptop"));
        categoryModelList.add(new CategoryModel("https://www.phucanh.vn/media/news/3012_csm_4zu3_Asus_Zenbook_UX3430UQ_Teaser_7c44e97fc6Top10Laptopttnhtchochnhsanhvbintpvideonm2017.jpg", "Smartphone"));
        categoryModelList.add(new CategoryModel("https://www.phucanh.vn/media/news/3012_csm_4zu3_Asus_Zenbook_UX3430UQ_Teaser_7c44e97fc6Top10Laptopttnhtchochnhsanhvbintpvideonm2017.jpg", "Tablet"));
        categoryModelList.add(new CategoryModel("https://www.phucanh.vn/media/news/3012_csm_4zu3_Asus_Zenbook_UX3430UQ_Teaser_7c44e97fc6Top10Laptopttnhtchochnhsanhvbintpvideonm2017.jpg", "Tai nghe"));

        List<SliderModel> sliderModelList = new ArrayList<>();
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));
        sliderModelList.add(new SliderModel("null", "#000000"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","ádasd","đâs","20.000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","ádasd","đâs","20.000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","ádasd","đâs","20.000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","ádasd","đâs","20.000"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel("","","ádasd","đâs","20.000"));


        homePageModelList.add(new HomePageModel(0, sliderModelList));
        homePageModelList.add(new HomePageModel(2,"trending","#dfdfdf", horizontalProductScrollModelList, new ArrayList<WishlistModel>()));
        homePageModelList.add(new HomePageModel(3,"trending","#dfdfdf", horizontalProductScrollModelList, new ArrayList<WishlistModel>()));

        categoryAdapter = new CategoryAdapter(categoryModelList);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setAdapter(categoryAdapter);

        homePageAdapter = new HomePageAdapter(homePageModelList);
        homepageRecyclerView.setAdapter(homePageAdapter);
        homePageAdapter.notifyDataSetChanged();






        return root;
    }
}