package com.grocery.app.model;

import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private boolean active;
    private List<Cart> products;
    private Customer customer;  // New field

    // Getters and Setters for existing fields
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Cart> getProducts() {
        return products;
    }

    public void setProducts(List<Cart> products) {
        this.products = products;
    }

    // Getter and Setter for Customer
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Method to get the total sum of product values
    public int getTotalPrice() {
        int totalPrice = 0;
        if (products != null) {
            for (Cart cart : products) {
                totalPrice += cart.getProductPrice();
            }
        }
        return totalPrice;
    }

    // No-argument constructor required by Firebase
    public Order() {
    }

    // Constructor with all fields
    public Order(String orderId, String customerId, List<Cart> products, boolean active, Customer customer) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.products = products;
        this.active = active;
        this.customer = customer;
    }
}
