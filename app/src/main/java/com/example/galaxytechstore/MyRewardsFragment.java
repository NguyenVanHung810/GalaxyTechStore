package com.example.galaxytechstore;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyRewardsFragment extends Fragment {

    private RecyclerView RewardsRecyclerView;
    private Dialog loaddialog;
    public static  MyRewardsAdapter myRewardsAdapter;

    public MyRewardsFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);
        RewardsRecyclerView = (RecyclerView) view.findViewById(R.id.my_rewards_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RewardsRecyclerView.setLayoutManager(layoutManager);

        // Loading dialog
        loaddialog = new Dialog(view.getContext());
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(view.getContext().getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();
        // Loading dialog

        myRewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList, false);
        RewardsRecyclerView.setAdapter(myRewardsAdapter);

        if(DBqueries.rewardModelList.size() == 0){
            DBqueries.loadRewards(getContext(), loaddialog, true);
        }
        else {
            loaddialog.dismiss();
        }

        myRewardsAdapter.notifyDataSetChanged();
        return view;
    }
}