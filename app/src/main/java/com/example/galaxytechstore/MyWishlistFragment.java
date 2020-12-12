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


public class MyWishlistFragment extends Fragment {

    private RecyclerView myWishlistFragment;
    private Dialog loaddialog;
    public static WishListAdapter wishListAdapter;

    public MyWishlistFragment() {
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);

        // -- Loading dialog
        loaddialog = new Dialog(view.getContext());
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();
        // -- Loading dialog

        myWishlistFragment = (RecyclerView) view.findViewById(R.id.my_wishlist_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myWishlistFragment.setLayoutManager(layoutManager);

        if (DBqueries.wishlistModelList.size() == 0) {
            DBqueries.wishList.clear();
            DBqueries.loadWishList(getContext(), loaddialog, true);
        } else {
            loaddialog.dismiss();
        }

        wishListAdapter = new WishListAdapter(DBqueries.wishlistModelList, true);
        myWishlistFragment.setAdapter(wishListAdapter);
        wishListAdapter.notifyDataSetChanged();

        return view;
    }
}
