package com.example.galaxytechstore;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyOrdersFragment extends Fragment {


    private Dialog loadingDialog;
    public static MyOrderAdapter myOrderAdapter;
    private RecyclerView MyOrdersRecyclerView;


    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        MyOrdersRecyclerView = (RecyclerView) view.findViewById(R.id.MyOrdersRecyclerView);

        //////////loading dialog

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        //////////loading dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        MyOrdersRecyclerView.setLayoutManager(layoutManager);


        myOrderAdapter = new MyOrderAdapter(DBqueries.myOrderItemModelList, loadingDialog);
        MyOrdersRecyclerView.setAdapter(myOrderAdapter);
        myOrderAdapter.notifyDataSetChanged();

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        myOrderAdapter.notifyDataSetChanged();
        loadingDialog.show();
        DBqueries.orderItemsModelList.clear();
        if(DBqueries.orderItemsModelList.size() ==0){
            DBqueries.loadOrders(getContext(), myOrderAdapter, loadingDialog);
        }
        else {
            loadingDialog.dismiss();
        }
    }
}