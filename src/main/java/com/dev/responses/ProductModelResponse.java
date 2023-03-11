package com.dev.responses;

import com.dev.models.ProductModel;

import java.util.List;

public class ProductModelResponse extends BasicResponse {
    private ProductModel productModels;
    private boolean isPublisher;

    public ProductModelResponse(ProductModel productModels, boolean isPublisher) {
        this.productModels = productModels;
        this.isPublisher = isPublisher;
    }

    public ProductModelResponse(boolean success, Integer errorCode, ProductModel productModels, boolean isPublisher) {
        super(success, errorCode);
        this.productModels = productModels;
        this.isPublisher = isPublisher;
    }

    public ProductModel getProductModels() {
        return productModels;
    }

    public void setProductModels(ProductModel productModels) {
        this.productModels = productModels;
    }

    public boolean isPublisher() {
        return isPublisher;
    }

    public void setPublisher(boolean publisher) {
        isPublisher = publisher;
    }
}
