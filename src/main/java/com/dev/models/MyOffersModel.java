package com.dev.models;

import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;


public class MyOffersModel {

   private int auctionId;
   private String productName;
    private boolean auctionStatus;
    private SaleOfferModel saleOfferModel;


    public MyOffersModel(SaleOffer saleOffer, Auction auction){
        productName=auction.getProductName();
        this.auctionStatus=auction.isOpen();
        this.saleOfferModel=convertOfferToModel(saleOffer);
        this.auctionId=auction.getId();

    }



    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }



    public boolean isAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(boolean auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public SaleOfferModel getSaleOfferModel() {
        return saleOfferModel;
    }

    public void setSaleOfferModel(SaleOfferModel saleOfferModel) {
        this.saleOfferModel = saleOfferModel;
    }

    private SaleOfferModel convertOfferToModel (SaleOffer saleOffer){
        return new SaleOfferModel(saleOffer);
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }
}
