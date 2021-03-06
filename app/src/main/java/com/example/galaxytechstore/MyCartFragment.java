package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MyCartFragment extends Fragment {

    private RecyclerView cartItemsRecycleView;
    private Button btn_continue;
    private Dialog loaddialog;
    public static CartAdapter cartAdapter;
    private TextView cartTotal;

    private ImageView nocart_image;
    private TextView nocart_info;
    private Button continue_shopping;

    public MyCartFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        // Loading dialog
        loaddialog = new Dialog(view.getContext());
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();
        // Loading dialog

        cartItemsRecycleView = (RecyclerView) view.findViewById(R.id.cart_items_recyclerview);
        btn_continue = (Button) view.findViewById(R.id.cart_continue_btn);
        cartTotal = (TextView) view.findViewById(R.id.tcm);

        nocart_image = (ImageView) view.findViewById(R.id.no_cart_image);
        nocart_info = (TextView) view.findViewById(R.id.no_cart_info);
        continue_shopping = (Button) view.findViewById(R.id.btn_continue_shopping);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecycleView.setLayoutManager(linearLayoutManager);

        cartAdapter = new CartAdapter(DBqueries.cartItemModelList, cartTotal, true);
        cartItemsRecycleView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continue_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;

                for (int x = 0; x < DBqueries.cartItemModelList.size(); x++) {
                    CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()) {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loaddialog.show();
                if (DBqueries.addressesModelList.size() == 0) {
                    DBqueries.loadAddresses(getContext(), loaddialog, true);
                } else {
                    loaddialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        if (DBqueries.rewardModelList.size() == 0) {
            loaddialog.show();
            DBqueries.loadRewards(getContext(), loaddialog, false);
        }
        if (DBqueries.cartItemModelList.size() == 0) {
            DBqueries.cartLists.clear();
            DBqueries.loadCartList(getContext(), loaddialog, true, new TextView(getContext()), cartTotal);
        } else {
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT) {
                LinearLayout parent = (LinearLayout) cartTotal.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loaddialog.dismiss();
            if(DBqueries.cartItemModelList.size() <=0){
                btn_continue.setVisibility(View.GONE);
            }
            else {
                btn_continue.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for(CartItemModel cartItemModel:DBqueries.cartItemModelList){
            if(!TextUtils.isEmpty(cartItemModel.getSelectedCoupanID())){
                for(RewardModel rewardModel: DBqueries.rewardModelList){
                    if(rewardModel.getCoupenId().equals(cartItemModel.getSelectedCoupanID())){
                        rewardModel.setAlreadyUsed(false);
                    }
                }
                cartItemModel.setSelectedCoupanID(null);
                if(MyRewardsFragment.myRewardsAdapter != null){
                    MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}