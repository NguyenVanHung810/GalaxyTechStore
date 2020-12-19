package com.example.galaxytechstore;

public class MyOrdersModel {
    private String productImage;
    private int rating;
    private String productTitle;
    private String deliveryStatus;

    public MyOrdersModel(String productImage, int rating, String productTitle, String deliveryStatus) {
        this.productImage = productImage;
        this.rating = rating;
        this.productTitle = productTitle;
        this.deliveryStatus = deliveryStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
