package com.dev.models;

import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    private String productName;
    private String productPhoto;
    private String productDescription;
    private String creationDate;
    private int initialPrice;
    private int numberOffers;
    private String publisher;
    private List<SaleOfferModel> saleOffersByUser;


    public ProductModel(Auction auction, List<SaleOffer> saleOffersByUser) {
        this.productName = auction.getProductName();
        this.productPhoto = auction.getProductPhoto();
        this.productDescription = auction.getProductDescription();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.creationDate = auction.getOpenDate().format(formatter);
        this.initialPrice = auction.getInitialPrice();
        this.numberOffers = auction.getSaleOffers().size();
        this.publisher = auction.getSubmitUser().getUsername();
        this.saleOffersByUser = convertOffersListToOffersModelList(saleOffersByUser);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }

    public int getNumberOffers() {
        return numberOffers;
    }

    public void setNumberOffers(int numberOffers) {
        this.numberOffers = numberOffers;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<SaleOfferModel> getSaleOffersByUser() {
        return saleOffersByUser;
    }

    public void setSaleOffersByUser(List<SaleOfferModel> saleOffersByUser) {
        this.saleOffersByUser = saleOffersByUser;
    }
    private List<SaleOfferModel> convertOffersListToOffersModelList(List<SaleOffer> saleOffers){
        List<SaleOfferModel> saleOfferModels =new ArrayList<>();
        for (SaleOffer offer:saleOffers) {
            SaleOfferModel newOfferModel=new SaleOfferModel(offer);
            saleOfferModels.add(newOfferModel);
        }
        return saleOfferModels;

    }

}
