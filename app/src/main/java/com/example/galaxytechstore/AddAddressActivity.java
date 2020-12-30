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
import android.view.MenuItem;
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

import es.dmoral.toasty.Toasty;

public class AddAddressActivity extends AppCompatActivity {

    private EditText name, phonenumber, city, district, ward, address;
    private Button save;

    private boolean updateAddress = false;
    private AddressesModel addressesModel;
    private int position;
    private Dialog loaddialog;

    private String selectedState;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm địa chỉ mới");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.user_name);
        phonenumber = findViewById(R.id.phone_number);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        ward = findViewById(R.id.ward);
        address = findViewById(R.id.address);

        save = (Button) findViewById(R.id.btn_save);

        // Loading dialog
        loaddialog = new Dialog(this);
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // Loading dialog

        if (getIntent().getStringExtra("INTENT").equals("update_address")) {
            updateAddress = true;
            position = getIntent().getIntExtra("Position", -1);
            addressesModel = DBqueries.addressesModelList.get(position);

            name.setText(addressesModel.getName());
            phonenumber.setText(addressesModel.getPhone());
            city.setText(addressesModel.getCity());
            district.setText(addressesModel.getDistrict());
            ward.setText(addressesModel.getWard());
            address.setText(addressesModel.getAddress());

            save.setText("Cập nhật");
        } else {
            position = DBqueries.addressesModelList.size();
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name.getText())) {
                    if (!TextUtils.isEmpty(phonenumber.getText()) && phonenumber.getText().length() == 10) {
                        if (!TextUtils.isEmpty(city.getText())) {
                            if (!TextUtils.isEmpty(district.getText())) {
                                if (!TextUtils.isEmpty(ward.getText())) {
                                    if (!TextUtils.isEmpty(address.getText())) {

                                        loaddialog.show();
                                        Map<String, Object> addAddress = new HashMap<>();
                                        addAddress.put("name_" + (position + 1), name.getText().toString());
                                        addAddress.put("phone_number_" + (position + 1), phonenumber.getText().toString());
                                        addAddress.put("city_" + (position + 1), city.getText().toString());
                                        addAddress.put("district_" + (position + 1), district.getText().toString());
                                        addAddress.put("ward_" + (position + 1), ward.getText().toString());
                                        addAddress.put("address_" + (position + 1), address.getText().toString());

                                        if (!updateAddress) {
                                            addAddress.put("list_size", (long) DBqueries.addressesModelList.size() + 1);
                                            if (getIntent().getStringExtra("INTENT").equals("manage")) {
                                                if (DBqueries.addressesModelList.size() == 0) {
                                                    addAddress.put("selected_" + (position + 1), true);
                                                } else {
                                                    addAddress.put("selected_" + (position + 1), false);
                                                }
                                            } else {
                                                addAddress.put("selected_" + (position + 1), true);
                                            }

                                            if (DBqueries.addressesModelList.size() > 0) {
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
                                                    if (!updateAddress) {
                                                        if (DBqueries.addressesModelList.size() > 0) {
                                                            DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                                        }
                                                        DBqueries.addressesModelList.add(new AddressesModel(
                                                                name.getText().toString(),
                                                                phonenumber.getText().toString(),
                                                                city.getText().toString(),
                                                                district.getText().toString(),
                                                                ward.getText().toString(),
                                                                address.getText().toString(),
                                                                true
                                                        ));
                                                        if (getIntent().getStringExtra("INTENT").equals("manage")) {
                                                            if (DBqueries.addressesModelList.size() == 0) {
                                                                DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                            }
                                                        } else {
                                                            DBqueries.selectedAddress = DBqueries.addressesModelList.size() - 1;
                                                        }


                                                    } else {
                                                        DBqueries.addressesModelList.set(position, new AddressesModel(
                                                                name.getText().toString(),
                                                                phonenumber.getText().toString(),
                                                                city.getText().toString(),
                                                                district.getText().toString(),
                                                                ward.getText().toString(),
                                                                address.getText().toString(),
                                                                true
                                                        ));
                                                    }
                                                    if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    } else {
                                                        MyAddressesActivity.refreshItem(DBqueries.selectedAddress, DBqueries.addressesModelList.size() - 1);
                                                    }
                                                    finish();
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                                loaddialog.dismiss();
                                            }
                                        });
                                    } else {
                                        address.requestFocus();
                                        Toasty.error(getApplicationContext(), "Vui lòng nhập địa chỉ đầy đủ !!!", Toasty.LENGTH_SHORT).show();
                                    }
                                } else {
                                    ward.requestFocus();
                                    Toasty.error(getApplicationContext(), "Vui lòng nhập Phường/Xã đầy đủ !!!", Toasty.LENGTH_SHORT).show();
                                }
                            } else {
                                district.requestFocus();
                                Toasty.error(getApplicationContext(), "Vui lòng nhập Quận/Huyện đầy đủ !!!", Toasty.LENGTH_SHORT).show();
                            }
                        } else {
                            city.requestFocus();
                            Toasty.error(getApplicationContext(), "Vui lòng nhập Tỉnh/Thành đầy đủ !!!", Toasty.LENGTH_SHORT).show();
                        }
                    } else {
                        phonenumber.requestFocus();
                        Toasty.error(getApplicationContext(), "Vui lòng nhập Số điện thoại đầy đủ !!!", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    name.requestFocus();
                    Toasty.error(getApplicationContext(), "Vui lòng nhập Họ tên đầy đủ !!!", Toasty.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}