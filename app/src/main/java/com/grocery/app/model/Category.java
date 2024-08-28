package com.grocery.app.model;

public class Category {
    private String id;
    private String title;


    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return title; // Return the category name to be displayed in the Spinner
    }

    public Category(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
