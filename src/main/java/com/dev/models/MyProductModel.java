package com.dev.models;

import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;

public class MyProductModel {
    int auctionId;
    String productName;
    SaleOfferModel highestOffer;
    boolean isAuctionOpen;

    public MyProductModel(Auction auction, SaleOfferModel highestOffer) {
        this.auctionId=auction.getId();
        this.productName = auction.getProductName();
        this.highestOffer = highestOffer;
        this.isAuctionOpen = auction.isOpen();
    }
    public MyProductModel(Auction auction) {
        this.auctionId=auction.getId();
        this.productName = auction.getProductName();
        this.isAuctionOpen = auction.isOpen();
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }



    public boolean isAuctionOpen() {
        return isAuctionOpen;
    }

    public void setAuctionOpen(boolean auctionOpen) {
        isAuctionOpen = auctionOpen;
    }

    public SaleOfferModel getHighestOffer() {
        return highestOffer;
    }

    public void setHighestOffer(SaleOfferModel highestOffer) {
        this.highestOffer = highestOffer;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }
}
