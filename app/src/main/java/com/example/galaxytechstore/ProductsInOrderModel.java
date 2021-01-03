package com.example.galaxytechstore;

public class ProductsInOrderModel {
    private String productImage;
    private String productTitle;
    private String productPrice;
    private Long productQuantity;
    private String cuttedPrice;

    public ProductsInOrderModel(String productImage, String productTitle, String productPrice, Long productQuantity, String cuttedPrice) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.cuttedPrice = cuttedPrice;
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

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }
}

