package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pincode;
    private EditText landmark;
    private EditText name;
    private EditText mobileNo;
    private EditText alternativeMobileNo;
    private Spinner state;
    private Button save;
    private boolean updateAddress=false;
    private AddressesModel addressesModel;
    private int position;
    private Dialog loaddialog;
    private String[] stateList;
    private String selectedState;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new a address");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        stateList = getResources().getStringArray(R.array.city_array);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flatnumber);
        pincode = findViewById(R.id.pincode);
        landmark = findViewById(R.id.landmark);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.phonenumber);
        alternativeMobileNo = findViewById(R.id.alternatephonenumber);
        state = findViewById(R.id.state_spinner);
        save = (Button) findViewById(R.id.btn_save);

        // Loading dialog
        loaddialog = new Dialog(this);
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // Loading dialog

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(adapter);

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(getIntent().getStringExtra("INTENT").equals("update_address")){
            updateAddress=true;
            position=getIntent().getIntExtra("Position",-1);
            addressesModel=DBqueries.addressesModelList.get(position);

            flatNo.setText(addressesModel.getFlatNo());
            locality.setText(addressesModel.getLocality());
            landmark.setText(addressesModel.getLandmark());
            city.setText(addressesModel.getCity());
            pincode.setText(addressesModel.getPincode());
            name.setText(addressesModel.getName());
            mobileNo.setText(addressesModel.getMobileNo());
            alternativeMobileNo.setText(addressesModel.getAlternateMobileNo());

            for(int i=0;i<stateList.length;i++){
                if(stateList[i].equals(addressesModel.getState())){
                    state.setSelection(i);
                }
            }
            save.setText("Update");
        }else {
            position=DBqueries.addressesModelList.size();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(city.getText())) {
                    if (!TextUtils.isEmpty(locality.getText())) {
                        if (!TextUtils.isEmpty(flatNo.getText())) {
                            if (!TextUtils.isEmpty(pincode.getText()) && pincode.getText().length() == 6) {
                                if (!TextUtils.isEmpty(name.getText())) {
                                    if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.getText().length() == 10) {

                                        loaddialog.show();
                                        Map<String, Object> addAddress = new HashMap<>();
                                        addAddress.put("mobile_no_" + (position+1), mobileNo.getText().toString());
                                        addAddress.put("alternate_mobile_no_" + (position+1), alternativeMobileNo.getText().toString());
                                        addAddress.put("name_" + (position+1), name.getText().toString());
                                        addAddress.put("pincode_"+(position+1),pincode.getText().toString());
                                        addAddress.put("state_"+(position+1),selectedState);
                                        addAddress.put("city_"+(position+1),city.getText().toString());
                                        addAddress.put("locality_"+(position+1),locality.getText().toString());
                                        addAddress.put("flat_no_"+(position+1),flatNo.getText().toString());
                                        addAddress.put("landmark_"+(position+1),landmark.getText().toString());

                                        if(!updateAddress){
                                            addAddress.put("list_size",(long) DBqueries.addressesModelList.size()+1);
                                            if(getIntent().getStringExtra("INTENT").equals("manage")){
                                                if(DBqueries.addressesModelList.size() == 0){
                                                    addAddress.put("selected_"+(position+1),true);
                                                }else {
                                                    addAddress.put("selected_"+(position+1),false);
                                                }
                                            }else {
                                                addAddress.put("selected_"+(position+1),true);
                                            }

                                            if(DBqueries.addressesModelList.size()>0) {
                                                addAddress.put("selected_" + (DBqueries.selectedAddress + 1), false);
                                            }
                                        }
                                        FirebaseFirestore.getInstance().collection("USERS")
                                                .document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                                .document("MY_ADDRESSES")
                                                .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if(!updateAddress) {
                                                        if (DBqueries.addressesModelList.size() > 0) {
                                                            DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                                        }
                                                        DBqueries.addressesModelList.add(new AddressesModel(
                                                                city.getText().toString(),
                                                                locality.getText().toString(),
                                                                flatNo.getText().toString(),
                                                                pincode.getText().toString(),
                                                                landmark.getText().toString(),
                                                                name.getText().toString()
                                                                , mobileNo.getText().toString()
                                                                , alternativeMobileNo.getText().toString()
                                                                , selectedState
                                                                , true
                                                        ));
                                                        if(getIntent().getStringExtra("INTENT").equals("manage")){
                                                            if(DBqueries.addressesModelList.size() == 0){
                                                                DBqueries.selectedAddress=DBqueries.addressesModelList.size()-1;
                                                            }
                                                        }else {
                                                            DBqueries.selectedAddress=DBqueries.addressesModelList.size()-1;
                                                        }


                                                    }else {
                                                        DBqueries.addressesModelList.set(position,new AddressesModel(
                                                                city.getText().toString(),
                                                                locality.getText().toString(),
                                                                flatNo.getText().toString(),
                                                                pincode.getText().toString(),
                                                                landmark.getText().toString(),
                                                                name.getText().toString()
                                                                , mobileNo.getText().toString()
                                                                , alternativeMobileNo.getText().toString()
                                                                , selectedState
                                                                , true
                                                        ));
                                                    }
                                                    if(getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    }else {
                                                        MyAddressesActivity.refreshItem(DBqueries.selectedAddress,DBqueries.addressesModelList.size()-1);
                                                    }
                                                    finish();
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                                loaddialog.dismiss();
                                            }
                                        });
                                    }
                                    else {
                                        mobileNo.requestFocus();
                                        Toast.makeText(getApplicationContext(), "Vui lòng nhập số điện thoại đầy đủ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    name.requestFocus();
                                }
                            }
                            else {
                                pincode.requestFocus();
                                Toast.makeText(getApplicationContext(), "Nhập mã pin đầy đủ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            flatNo.requestFocus();
                        }
                    }
                    else {
                        locality.requestFocus();
                    }
                }
                else {
                    city.requestFocus();
                }
            }
        });
    }
}