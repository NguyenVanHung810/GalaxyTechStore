package com.example.galaxytechstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ProductInOrderActivity extends AppCompatActivity {

    private ListView listView;
    private ProductsInOrderAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sản phẩm đặt mua");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listView = findViewById(R.id.pio_lv);

        adapter = new ProductsInOrderAdapter(getApplicationContext(), R.layout.products_in_order_layout, OrderDetailsActivity.products);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}