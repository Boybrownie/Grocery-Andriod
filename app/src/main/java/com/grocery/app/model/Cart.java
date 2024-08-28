package com.grocery.app.model;

public class Cart {
    private String productId;
    private String productTitle;
    private Integer productPrice;
    private String productImageName;
    private String customerId;
    private String id;

    public String getId() {
        return id;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public String getProductImageName() {
        return productImageName;
    }

    public String getProductId() {
        return productId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Cart() {
    }

    public int getMembershipPrice() {
        return productPrice - (productPrice / 10);
    }

    public Cart(String id, String productId, String productTitle, Integer productPrice,
                String productImageName, String customerId) {
        this.id = id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productImageName = productImageName;
        this.customerId = customerId;
    }
}
