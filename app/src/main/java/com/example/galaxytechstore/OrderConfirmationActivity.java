package com.example.galaxytechstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class OrderConfirmationActivity extends AppCompatActivity {


    private Button verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        verifyBtn=findViewById(R.id.verify_btn);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updateStatus=new HashMap<>();
                updateStatus.put("Order_Status","Ordered");
                final String order_id=getIntent().getStringExtra("order_id");
                FirebaseFirestore.getInstance().collection("ORDERS").document(order_id)
                        .update(updateStatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Map<String, Object> userOrder = new HashMap<>();
                                    userOrder.put("order_id",order_id);
                                    userOrder.put("time", FieldValue.serverTimestamp());
                                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id)
                                            .set(userOrder)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        DeliveryActivity.ordered=true;
                                                        DeliveryActivity.codOrderConfirmed=true;
                                                        finish();
                                                    }else {
                                                        Toasty.warning(OrderConfirmationActivity.this, "Failed to update user orders list !", Toast.LENGTH_LONG,true).show();
                                                    }
                                                }
                                            });
                                }else {
                                    Toasty.error(OrderConfirmationActivity.this, "Order Cancelled !", Toast.LENGTH_LONG,true).show();
                                }
                            }
                        });
            }
        });

    }
}