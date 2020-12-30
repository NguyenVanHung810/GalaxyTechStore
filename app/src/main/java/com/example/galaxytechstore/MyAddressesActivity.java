package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MyAddressesActivity extends AppCompatActivity {

    private int previousAddress, mode;
    private LinearLayout addNewAddressBtn;
    private RecyclerView addressesRecyclerView;
    private Button deliverherebtn;
    private TextView adddressSaved;
    public static AddressesAdapter addressesAdapter;
    private Dialog loaddialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        //Loading dialog
        loaddialog = new Dialog(this);
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                adddressSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" địa chỉ đã được lưu.");
            }
        });
        //Loading dialog

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Địa chỉ của tôi");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        previousAddress = DBqueries.selectedAddress;
        addressesRecyclerView = (RecyclerView) findViewById(R.id.addresses_recyclerview);
        deliverherebtn = (Button) findViewById(R.id.deliver_here_btn);
        addNewAddressBtn = (LinearLayout) findViewById(R.id.add_new_address_btn);
        adddressSaved = (TextView) findViewById(R.id.addresses_saved);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addressesRecyclerView.setLayoutManager(linearLayoutManager);

        mode = getIntent().getIntExtra("MODE",-1);
        if(mode == DeliveryActivity.SELECT_ADDRESS){
            deliverherebtn.setVisibility(View.VISIBLE);
        }
        else {
            deliverherebtn.setVisibility(View.GONE);
        }
        deliverherebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBqueries.selectedAddress != previousAddress){

                    final int previousAddressIndex = previousAddress;
                    loaddialog.show();
                    Map<String, Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1), false);
                    updateSelection.put("selected_"+String.valueOf(DBqueries.selectedAddress+1), true);

                    previousAddress= DBqueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                finish();
                            }
                            else {
                                previousAddress=previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                            }
                            loaddialog.dismiss();
                        }
                    });
                }
                else {
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(DBqueries.addressesModelList, mode, loaddialog);
        addressesAdapter.notifyDataSetChanged();
        addressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)addressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode != DeliveryActivity.SELECT_ADDRESS){
                    startActivity(new Intent(MyAddressesActivity.this,AddAddressActivity.class).putExtra("INTENT","manage"));
                }else {
                    startActivity(new Intent(MyAddressesActivity.this,AddAddressActivity.class).putExtra("INTENT","null"));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adddressSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+ " địa chỉ đã được lưu.");
    }

    public static void refreshItem(int deselect, int select){
        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(mode == DeliveryActivity.SELECT_ADDRESS) {
                if (DBqueries.selectedAddress != previousAddress) {
                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                    DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                    DBqueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (mode == DeliveryActivity.SELECT_ADDRESS){
            if (DBqueries.selectedAddress != previousAddress) {
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}