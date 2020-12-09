package com.example.galaxytechstore;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private List<SliderModel> sliderModels;

    public SliderAdapter(List<SliderModel> sliderModels) {
        this.sliderModels = sliderModels;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.slider_layout, container,false);
        ImageView banner = view.findViewById(R.id.banner_slider);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .cornerRadiusDp(3)
                .oval(false)
                .build();
        Picasso.get().load(sliderModels.get(position).getBanner()).fit().transform(transformation).placeholder(R.drawable.banner_placeholder).into(banner);
        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return sliderModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
