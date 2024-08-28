package com.grocery.app.model;

public class Customer {
    private String name;
    private String email;
    private String password;
    private String id;
    private Long membershipExpiry;
    private String streetAddress;
    private String city;
    private String zipCode;
    private String country;
    private String avatarUrl; // New field for avatar URL

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMembershipExpiry() {
        return membershipExpiry;
    }

    public void setMembershipExpiry(Long membershipExpiry) {
        this.membershipExpiry = membershipExpiry;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatarUrl() {
        return avatarUrl; // Getter for avatar URL
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl; // Setter for avatar URL
    }

    public boolean isMember() {
        return membershipExpiry >= System.currentTimeMillis();
    }

    // Constructors
    public Customer() {
    }

    public Customer(String name, String email, String password, String id, Long membershipExpiry, String streetAddress, String city, String zipCode, String country, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
        this.membershipExpiry = membershipExpiry;
        this.streetAddress = streetAddress;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.avatarUrl = avatarUrl; // Initialize avatar URL
    }
}
