package com.example.galaxytechstore;

public class CategoryModel {
    private String categoryId;
    private String categoryImage;
    private String categoryName;

    public CategoryModel(String categoryId, String categoryImage, String categoryName) {
        this.categoryId = categoryId;
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
