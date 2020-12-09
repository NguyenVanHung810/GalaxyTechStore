package com.example.galaxytechstore;

import java.util.List;

public class HomePageModel {
    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT_VIEW = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    /// banner
    private int type;
    private String backgroundcolor;

    private List<SliderModel> list;

    public HomePageModel(int type, List<SliderModel> list) {
        this.type = type;
        this.list = list;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getList() {
        return list;
    }

    public void setList(List<SliderModel> list) {
        this.list = list;
    }
    /// banner


    /// strip ad
    private String resource;

    public HomePageModel(int type, String resource, String backgroundcolor) {
        this.type = type;
        this.resource = resource;
        this.backgroundcolor = backgroundcolor;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBackgroundcolor() {
        return backgroundcolor;
    }

    public void setBackgroundcolor(String backgroundcolor) {
        this.backgroundcolor = backgroundcolor;
    }
    /// strip ad

    /// horizontal product layout
    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private List<WishlistModel> wishlistModelList;

    public HomePageModel(int type, String title,String backgroundcolor, List<HorizontalProductScrollModel> horizontalProductScrollModelList, List<WishlistModel> wishlistModelList) {
        this.type = type;
        this.title = title;
        this.backgroundcolor = backgroundcolor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.wishlistModelList = wishlistModelList;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    public HomePageModel(int type, String title, String backgroundcolor, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroundcolor = backgroundcolor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    /// horizontal product layout

    /// grid product layout
}
