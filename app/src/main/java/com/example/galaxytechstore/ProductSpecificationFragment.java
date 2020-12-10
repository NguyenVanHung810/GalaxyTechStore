package com.example.galaxytechstore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class ProductSpecificationFragment extends Fragment {
    public ProductSpecificationFragment() {
    }

    private RecyclerView productSpecificationRecyclerView;
    public List<ProductSpecificationModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_specification, container, false);
        productSpecificationRecyclerView = (RecyclerView) view.findViewById(R.id.product_specification_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        ProductSpecificationAdapter adapter = new ProductSpecificationAdapter(list);
        productSpecificationRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }
}