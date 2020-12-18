package com.example.galaxytechstore;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MyCartFragment extends Fragment {

    private RecyclerView cartItemsRecycleView;
    private Button btn_continue;
    private Dialog loaddialog;
    public static CartAdapter cartAdapter;
    private TextView cartTotal;

    public MyCartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);
        return view;
    }
}