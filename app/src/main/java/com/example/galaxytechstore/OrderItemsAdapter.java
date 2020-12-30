package com.example.galaxytechstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderItemsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<OrderItemsModel> orderItemsModelList;

    public OrderItemsAdapter(Context context, int layout, List<OrderItemsModel> orderItemsModelList) {
        this.context = context;
        this.layout = layout;
        this.orderItemsModelList = orderItemsModelList;
    }

    @Override
    public int getCount() {
        return orderItemsModelList.size();
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
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }



        OrderItemsModel orderItemsModel = orderItemsModelList.get(position);
        holder.title.setText(orderItemsModel.getProductTitle());
        holder.price.setText(orderItemsModel.getProductPrice());
        Picasso.get().load(orderItemsModel.getProductImage()).into(holder.image);


        return convertView;
    }
}
