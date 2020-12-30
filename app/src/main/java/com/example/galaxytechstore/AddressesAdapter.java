package com.example.galaxytechstore;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.galaxytechstore.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.galaxytechstore.MyAddressesActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.Viewholder> {
    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh=false;
    private Dialog loadingDialog;


    public AddressesAdapter(List<AddressesModel> addressesModelList, int MODE, Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        this.preSelectedPosition = DBqueries.selectedAddress;
        this.loadingDialog=loadingDialog;
    }

    @NonNull
    @Override
    public AddressesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.Viewholder holder, int position) {
        String name=addressesModelList.get(position).getName();
        String phone=addressesModelList.get(position).getPhone();
        String city=addressesModelList.get(position).getCity();
        String distict=addressesModelList.get(position).getDistrict();
        String ward=addressesModelList.get(position).getWard();
        String address=addressesModelList.get(position).getAddress();
        boolean selected=addressesModelList.get(position).getSelected();

        holder.setdata(name, phone, city, distict, ward, address, selected, position);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView fullname;
        private TextView fulladdress;
        private TextView phone_number;
        private ImageView icon;
        private LinearLayout optioncontainer;
        private TextView tv_edit, tv_delete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            fullname = (TextView) itemView.findViewById(R.id.name_item);
            fulladdress = (TextView) itemView.findViewById(R.id.address_item);
            phone_number = (TextView) itemView.findViewById(R.id.phonenumber_item);
            icon = (ImageView) itemView.findViewById(R.id.ic_item);
            optioncontainer = (LinearLayout) itemView.findViewById(R.id.option_container);
            tv_edit = (TextView) itemView.findViewById(R.id.edit);
            tv_delete = (TextView) itemView.findViewById(R.id.delete);
        }

        private void setdata(String name, String phone, String city, String district, String ward, String address, boolean selected, final int position) {

            fullname.setText(name);
            fulladdress.setText(address + "," + ward + "," + district + "," + city);
            phone_number.setText(phone);

            if (MODE == DeliveryActivity.SELECT_ADDRESS) {
                icon.setImageResource(R.drawable.check);
                if (selected) {
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    icon.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress = position;
                        }
                    }
                });
            } else if (MODE == MANAGE_ADDRESS) {
                optioncontainer.setVisibility(View.GONE);
                tv_delete.setVisibility(View.GONE);
                tv_edit.setVisibility(View.GONE);
                optioncontainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {  ////edit address
                        itemView.getContext().startActivity(new Intent(itemView.getContext(), AddAddressActivity.class).putExtra("INTENT", "update_address").putExtra("Position", position));
                        refresh = false;
                    }
                });
                optioncontainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {  /// remove address
                        loadingDialog.show();
                        Map<String, Object> addresses = new HashMap<>();
                        int x = 0, selected = -1;
                        for (int i = 0; i < addressesModelList.size(); i++) {
                            if (i != position) {
                                x++;
                                addresses.put("name_" + x, addressesModelList.get(i).getName());
                                addresses.put("phone_number_" + x, addressesModelList.get(i).getPhone());
                                addresses.put("city_" + x, addressesModelList.get(i).getCity());
                                addresses.put("district_" + x, addressesModelList.get(i).getDistrict());
                                addresses.put("ward_" + x, addressesModelList.get(i).getWard());
                                addresses.put("address_" + x, addressesModelList.get(i).getAddress());

                                if (addressesModelList.get(position).getSelected()) {
                                    if (position - 1 >= 0) {
                                        if (x == position) {
                                            addresses.put("selected_" + x, true);
                                            selected = x;
                                        } else {
                                            addresses.put("selected_" + x, addressesModelList.get(i).getSelected());
                                        }
                                    } else {
                                        if (x == 1) {
                                            addresses.put("selected_" + x, true);
                                            selected = x;
                                        } else {
                                            addresses.put("selected_" + x, addressesModelList.get(i).getSelected());
                                        }
                                    }
                                } else {
                                    addresses.put("selected_" + x, addressesModelList.get(i).getSelected());
                                    if (addressesModelList.get(i).getSelected()) {
                                        selected = x;
                                    }
                                }
                            }
                        }
                        addresses.put("list_size", x);
                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DBqueries.addressesModelList.remove(position);
                                    if (finalSelected != -1) {
                                        DBqueries.selectedAddress = finalSelected - 1;
                                        DBqueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                    } else if (DBqueries.addressesModelList.size() == 0) {
                                        DBqueries.selectedAddress = -1;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
                        refresh = false;
                    }
                });
                icon.setImageResource(R.drawable.vertical_menu);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optioncontainer.setVisibility(View.VISIBLE);
                        tv_edit.setVisibility(View.VISIBLE);
                        tv_delete.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(preSelectedPosition, preSelectedPosition);
                        } else {
                            refresh = true;
                        }
                        preSelectedPosition = position;

                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshItem(preSelectedPosition, preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }
}
