package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
        loadingDialog.show();
        //////////loading dialog

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                for(MyOrderItemModel orderItemModel:DBqueries.myOrderItemModelList){
                    if(!orderItemModel.isCancellationrequested()){
                        if(!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Cancelled")){
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(currentOrderImage);
                            currentOrderstatus.setText(orderItemModel.getOrderStatus());

                            switch (orderItemModel.getOrderStatus()){
                                case "Ordered":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    break;
                                case "Packed":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    O_P_progress.setProgress(100);
                                    break;
                                case "Shipped":
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    O_P_progress.setProgress(100);
                                    P_S_progress.setProgress(100);
                                    break;
                                case "out for Delivery":
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                                    O_P_progress.setProgress(100);
                                    P_S_progress.setProgress(100);
                                    S_D_progress.setProgress(100);
                                    break;
                            }
                        }
                    }
                }
                int i=0;
                for(MyOrderItemModel myOrderItemModel:DBqueries.myOrderItemModelList){
                    if(i<4) {
                        if (myOrderItemModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                            i++;
                        }
                    }else {
                        break;
                    }
                }
                if(i==0){
                    recentOrdersTitle.setText("No recent Orders");
                }
                if(i<3){
                    for (int x=i ; x<4;x++){
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        loadingDialog.setOnDismissListener(null);
                        if(DBqueries.addressesModelList.size() == 0){
                            addressname.setText("No Address");
                            address.setText("-");
                            pincode.setText("-");
                        }else {
                            setAddress();
                        }
                    }
                });
                DBqueries.loadAddresses(getContext(),loadingDialog,false);

            }
        });

        DBqueries.loadOrders(getContext(),null,loadingDialog);

        viewAllAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressActivity = new Intent(getContext(), MyAddressesActivity.class);
                myAddressActivity.putExtra("MODE", DeliveryActivity.SELECT_ADDRESS);
                startActivity(myAddressActivity);
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

    @Override
    public void onStart() {
        super.onStart();

        name.setText(DBqueries.fullname);
        email.setText(DBqueries.email);
        if(!DBqueries.profile.equals("")){
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.user)).into(profileView);
        }else {
            profileView.setImageResource(R.drawable.user);
        }

        if(!loadingDialog.isShowing()){
            if(DBqueries.addressesModelList.size() == 0){
                addressname.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            }else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String nametext,mobileNo;
        nametext = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")){
            addressname.setText(nametext + " - " + mobileNo);
        }else {
            addressname.setText(nametext + " - " + mobileNo+" or "+DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }

        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if(landmark.equals("")){
            address.setText(flatNo+","+locality+","+city+","+state);
        }else {
            address.setText(flatNo+","+locality+","+landmark+","+city+","+state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());
    }
}