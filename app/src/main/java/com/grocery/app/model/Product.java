package com.grocery.app.model;

public class Product {
    private String id;
    private String title;
    private Integer price;
    private String imageName;
    private String categoryId; // New field added

    public String getTitle() {
        return title;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageName() {
        return imageName;
    }

    public String getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public int getMembershipPrice() {
        return price - (price / 10);
    }

    public Product() {
    }

    public Product(String id, String title, Integer price, String imageName, String categoryId) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imageName = imageName;
        this.categoryId = categoryId; // Initialize categoryId
    }
}
