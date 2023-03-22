package com.dev.models;

import com.dev.objects.Auction;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OpenAuctionModel {
    private int auctionId;
    private String productName;
    private String productPhoto;
    private String creationDate;
    private int amountOfSaleOffers;
    private int amountOfMySaleOffers;



    public OpenAuctionModel(Auction auction,int myOffers) {
        this.auctionId=auction.getId();
        this.productName = auction.getProductName();
        this.productPhoto=auction.getProductPhoto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.creationDate = auction.getOpenDate().format(formatter);
        this.amountOfSaleOffers = auction.getSaleOffers().size();
        this.amountOfMySaleOffers=myOffers;

    }



    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getAmountOfSaleOffers() {
        return amountOfSaleOffers;
    }

    public void setAmountOfSaleOffers(int amountOfSaleOffers) {
        this.amountOfSaleOffers = amountOfSaleOffers;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public int getAmountOfMySaleOffers() {
        return amountOfMySaleOffers;
    }

    public void setAmountOfMySaleOffers(int amountOfMySaleOffers) {
        this.amountOfMySaleOffers = amountOfMySaleOffers;
    }

}
