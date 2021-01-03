package com.example.galaxytechstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class ProductsInOrderAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ProductsInOrderModel> products;

    public ProductsInOrderAdapter(Context context, int layout, List<ProductsInOrderModel> products) {
        this.context = context;
        this.layout = layout;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder{
        ImageView image;
        TextView title;
        TextView price;
        TextView quantity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView==null){
            LayoutInflater inflater =  (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);

            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.textView);
            holder.price = (TextView) convertView.findViewById(R.id.textView2);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            holder.quantity = (TextView) convertView.findViewById(R.id.textView3);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductsInOrderModel productsInOrderModel = products.get(position);
        holder.title.setText(productsInOrderModel.getProductTitle());
        holder.price.setText(vnMoney(Long.parseLong(productsInOrderModel.getProductPrice())));
        holder.quantity.setText("Số lượng: x"+productsInOrderModel.getProductQuantity());
        Glide.with(context).load(productsInOrderModel.getProductImage()).into(holder.image);
        return convertView;
    }

    private String vnMoney(Long s) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(s) + " đ";
    }
}
