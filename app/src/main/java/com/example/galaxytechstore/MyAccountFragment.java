package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton settingsBtn;
    public final static int MANAGE_ADDRESS = 1;
    private Button viewAllAddressButton, signOutBtn;
    private CircleImageView profileView, currentOrderImage;
    private TextView name, email, currentOrderstatus, recentOrdersTitle, addressname, address, pincode;
    private LinearLayout layoutContainer, recentOrdersContainer;
    private Dialog loadingDialog;
    private ImageView orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_my_account, container, false);

        // init
        viewAllAddressButton=root.findViewById(R.id.view_all_address_button);
        profileView=root.findViewById(R.id.profile_pic);
        name=root.findViewById(R.id.username);
        email=root.findViewById(R.id.user_email);
        layoutContainer=root.findViewById(R.id.layout_container);
        currentOrderImage=root.findViewById(R.id.current_order_image);
        currentOrderstatus=root.findViewById(R.id.tv_current_order_status);

        orderedIndicator=root.findViewById(R.id.ordered_indicator);
        packedIndicator=root.findViewById(R.id.packed_indicator);
        shippedIndicator=root.findViewById(R.id.shipped_indicator);
        deliveredIndicator=root.findViewById(R.id.delivered_indicator);

        O_P_progress=root.findViewById(R.id.order_packed_progress);
        P_S_progress=root.findViewById(R.id.packed_shipped_progress);
        S_D_progress=root.findViewById(R.id.shipped_delivered_progress);

        recentOrdersTitle=root.findViewById(R.id.your_recent_orders_title);
        recentOrdersContainer=root.findViewById(R.id.recent_orders_container);

        addressname=root.findViewById(R.id.name);
        address=root.findViewById(R.id.address);
        pincode=root.findViewById(R.id.pincode);
        signOutBtn=root.findViewById(R.id.sign_out);
        settingsBtn=root.findViewById(R.id.setting_btn);
        // init

        ////////// Loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(root.getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////loading dialog

        layoutContainer.getChildAt(1).setVisibility(View.GONE);

        viewAllAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                DBqueries.email=null;  //my code
                startActivity(new Intent(getContext(), Login_Register_ResetPasswordActivity.class));
                getActivity().finish();
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name",name.getText());
                updateUserInfo.putExtra("Email",email.getText());
                updateUserInfo.putExtra("Photo",DBqueries.profile);
                startActivity(updateUserInfo);
            }
        });


        return root;
    }
}