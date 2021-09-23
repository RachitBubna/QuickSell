package com.example.quicksell;

public class Product {
    private String mProductName;
    private String mProductPrice;
    private String mProductDesc;
    private String mImageUrl;

    public Product() {
    }

    public Product(String productName, String productDesc, String productPrice, String imageUrl) {

        if (productName == null) {
            mProductName = "-";
        } else {
            mProductName = productName;
        }
        if (productDesc == null) {
            mProductName = "-";
        } else {
            mProductDesc = productDesc;
        }
        if (productPrice == null) {
            mProductName = "-";
        } else {
            mProductPrice = "₹ " + productPrice;
        }
        if (imageUrl == null) {
            mImageUrl = "-";
        } else {
            mImageUrl = imageUrl;
        }

    }

    public String getmProductName() {
        return mProductName;
    }

    public String getmProductPrice() {
        return mProductPrice;
    }

    public String getmProductDesc() {
        return mProductDesc;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}


