package com.example.galaxytechstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> list;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastposition = -1;

    public HomePageAdapter(List<HomePageModel> list) {
        this.list = list;
        this.recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager bannerSliderViewPaper;
        private int currentPage;
        private Timer timer;
        final private int delay = 3000;
        final private int period = 3000;
        private List<SliderModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewPaper = itemView.findViewById(R.id.banner_slider_view_pager);
        }

        // banner slider
        private void setBannerSliderViewPaper(List<SliderModel> sliderList) {
            currentPage = 2;
            if (timer != null) {
                timer.cancel();
            }

            arrangedList = new ArrayList<>();
            for (int x = 0; x < sliderList.size(); x++) {
                arrangedList.add(x, sliderList.get(x));
            }
            arrangedList.add(0, sliderList.get(sliderList.size()-2));
            arrangedList.add(1, sliderList.get(sliderList.size()-1));
            arrangedList.add(sliderList.get(0));
            arrangedList.add(sliderList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);

            bannerSliderViewPaper.setAdapter(sliderAdapter);
            bannerSliderViewPaper.setClipToPadding(false);
            bannerSliderViewPaper.setPageMargin(20);
            bannerSliderViewPaper.setCurrentItem(currentPage);
            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }
                }
            };
            bannerSliderViewPaper.addOnPageChangeListener(onPageChangeListener);
            startbannerSlidershow(sliderList);
            bannerSliderViewPaper.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stopbannerSlidershow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startbannerSlidershow(arrangedList);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<SliderModel> sliderList) {
            if (currentPage == sliderList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPaper.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = sliderList.size() - 3;
                bannerSliderViewPaper.setCurrentItem(currentPage, false);
            }
        }

        private void startbannerSlidershow(List<SliderModel> sliderList) {
            Handler handler = new Handler();
            Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderList.size()) {
                        currentPage = 0;
                        bannerSliderViewPaper.setCurrentItem(currentPage, true);
                    } else {
                        bannerSliderViewPaper.setCurrentItem(currentPage++, true);
                    }
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, delay, period);
        }

        private void stopbannerSlidershow() {
            timer.cancel();
        }

        // banner slider
    }

    public class stripAdBannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView stripImage;
        private ConstraintLayout stripadcontainer;

        public stripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            stripImage = (ImageView) itemView.findViewById(R.id.strip_ad_image);
            stripadcontainer = (ConstraintLayout) itemView.findViewById(R.id.strip_ad_container);


        }

        private void setStripAd(String rs, String color) {
            Glide.with(itemView.getContext()).load(rs).apply(new RequestOptions().placeholder(R.drawable.banner_placeholder)).into(stripImage);
            stripadcontainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class horizontalProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container;
        private TextView layoutTitle;
        private Button viewAll;
        private RecyclerView horizontalRecyclerView;

        public horizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container = (ConstraintLayout) itemView.findViewById(R.id.container);
            layoutTitle = (TextView) itemView.findViewById(R.id.horizontal_scroll_layout_title);
            viewAll = (Button) itemView.findViewById(R.id.horizontal_scroll_layout_viewall);
            horizontalRecyclerView = (RecyclerView) itemView.findViewById(R.id.horizontal_scroll_layout_recycler);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String color, List<WishlistModel> viewAllList) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            layoutTitle.setText(title);
            if (horizontalProductScrollModelList.size() > 0) {
                viewAll.setVisibility(View.VISIBLE);
                viewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.viewallModelList = viewAllList;
                        Intent viewAllActivity = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllActivity.putExtra("layout_code", 0);
                        viewAllActivity.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllActivity);
                    }
                });
            } else {
                viewAll.setVisibility(View.INVISIBLE);
            }
            HorizontalProductScrollAdapter adapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(itemView.getContext());
            layoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerView.setLayoutManager(layoutManager1);
            horizontalRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public class gridProductViewHolder extends RecyclerView.ViewHolder {

        private TextView Title;
        private Button viewAll;
        private GridLayout gridProductLayout;
        private ConstraintLayout container;


        public gridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = (TextView) itemView.findViewById(R.id.grid_product_layout_title);
            viewAll = (Button) itemView.findViewById(R.id.grid_product_layout_button);
            gridProductLayout = (GridLayout) itemView.findViewById(R.id.gridLayout);
            container = (ConstraintLayout) itemView.findViewById(R.id.container);
        }

        @SuppressLint("NewApi")
        private void setGridProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String tt, String color) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            Title.setText(tt);
            for (int x = 0; x < 4; x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_img);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_tv);
                TextView productDescription = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_desc);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_item_price);

                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions()).placeholder(R.drawable.placeholder).into(productImage);
                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                productDescription.setText(horizontalProductScrollModelList.get(x).getProductDesc());
                productPrice.setText(horizontalProductScrollModelList.get(x).getProductPrice() + "đ");

                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));

                if(!tt.equals("")){
                    int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            productDetailsIntent.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(finalX).getProductID());
                            itemView.getContext().startActivity(productDetailsIntent);
                        }
                    });
                }
            }
            if(!tt.equals("")){
                viewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                        Intent viewAllActivity = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllActivity.putExtra("layout_code", 1);
                        viewAllActivity.putExtra("title", tt);
                        itemView.getContext().startActivity(viewAllActivity);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (list.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.STRIP_AD_BANNER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_banner, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomePageModel.STRIP_AD_BANNER:
                View stripAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new stripAdBannerViewHolder(stripAdView);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new horizontalProductViewHolder(horizontalProductView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new gridProductViewHolder(gridProductView);
            default:
                return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (list.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = list.get(position).getList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPaper(sliderModelList);
                break;
            case HomePageModel.STRIP_AD_BANNER:
                String resource = list.get(position).getResource();
                String cl = list.get(position).getBackgroundcolor();
                ((stripAdBannerViewHolder) holder).setStripAd(resource, cl);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String color = list.get(position).getBackgroundcolor();
                List<WishlistModel> wishlistModelList = list.get(position).getWishlistModelList();
                List<HorizontalProductScrollModel> horizontalProductScrollModels = list.get(position).getHorizontalProductScrollModelList();
                ((horizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModels, list.get(position).getTitle(),color, wishlistModelList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                List<HorizontalProductScrollModel> gridProductScrollModels = list.get(position).getHorizontalProductScrollModelList();
                ((gridProductViewHolder) holder).setGridProductLayout(gridProductScrollModels, list.get(position).getTitle(), list.get(position).getBackgroundcolor());
                break;
            default:
                return;
        }
        if(lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}

