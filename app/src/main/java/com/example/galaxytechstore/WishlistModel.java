package com.example.galaxytechstore;

import java.util.ArrayList;

public class WishlistModel {
    private String productID;
    private String prodcutImage;
    private String productTitle;
    private long freecoupen;
    private String rating;
    private long total_Ratings;
    private String productPrice;
    private String cuttedPrice;
    private Boolean COD;
    private Boolean inStock;
    private ArrayList<String> tags;


    public WishlistModel(String productID, String prodcutImage, String productTitle, String rating, long total_Ratings, String productPrice, String cuttedPrice, Boolean COD, boolean inStock ) {
        this.productID = productID;
        this.prodcutImage = prodcutImage;
        this.productTitle = productTitle;
        this.rating = rating;
        this.total_Ratings = total_Ratings;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.COD = COD;
        this.inStock = inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProdcutImage() {
        return prodcutImage;
    }

    public void setProdcutImage(String prodcutImage) {
        this.prodcutImage = prodcutImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public long getFreecoupen() {
        return freecoupen;
    }

    public void setFreecoupen(long freecoupen) {
        this.freecoupen = freecoupen;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public long getTotal_Ratings() {
        return total_Ratings;
    }

    public void setTotal_Ratings(long total_Ratings) {
        this.total_Ratings = total_Ratings;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Boolean getCOD() {
        return COD;
    }

    public void setCOD(Boolean COD) {
        this.COD = COD;
    }
}
