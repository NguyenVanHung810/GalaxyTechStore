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
    }

    private FloatingActionButton settingsBtn;
    public final static int MANAGE_ADDRESS = 1;
    private Button viewAllAddressButton, signOutBtn;
    private CircleImageView profileView;
    private TextView name, email, fullname, fulladdress, phonenumber;
    private Dialog loadingDialog;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_my_account, container, false);
        viewAllAddressButton=root.findViewById(R.id.view_all_address_button);
        profileView=root.findViewById(R.id.profile_pic);
        name=root.findViewById(R.id.username);
        email=root.findViewById(R.id.user_email);

        fullname=root.findViewById(R.id.name);
        fulladdress=root.findViewById(R.id.address);
        phonenumber=root.findViewById(R.id.phonenumber);
        signOutBtn=root.findViewById(R.id.sign_out);
        settingsBtn=root.findViewById(R.id.setting_btn);

        ////////// Loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(root.getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////loading dialog


        loadingDialog.show();
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                loadingDialog.setOnDismissListener(null);
                if(DBqueries.addressesModelList.size() == 0){
                    fullname.setText("Không có địa chỉ");
                    fulladdress.setText("-");
                    phonenumber.setText("-");
                }else {
                    setAddress();
                }
            }
        });
        DBqueries.loadAddresses(getContext(),loadingDialog,false);

        viewAllAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressActivity = new Intent(getContext(), MyAddressesActivity.class);
                myAddressActivity.putExtra("MODE", MANAGE_ADDRESS);
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
                updateUserInfo.putExtra("Phone", DBqueries.phone);
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
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.user_1)).into(profileView);
        }else {
            profileView.setImageResource(R.drawable.user_1);
        }

        if(!loadingDialog.isShowing()){
            if(DBqueries.addressesModelList.size() == 0){
                fullname.setText("Không có địa chỉ");
                fulladdress.setText("-");
                phonenumber.setText("-");
            }else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String nametext, mobileNo;
        nametext = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPhone();
        fullname.setText(nametext);
        phonenumber.setText(mobileNo);

        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String district = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getDistrict();
        String ward = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getWard();
        String address = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAddress();

        fulladdress.setText(address+", "+ward+", "+district+", "+city+".");
    }
}