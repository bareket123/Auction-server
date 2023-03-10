package com.dev.models;

import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;

public class SaleOfferModel {

    private double offerPrice;
    private boolean isWon;
    private String submitterUserName;

    public SaleOfferModel(SaleOffer saleOffer) {
        this.offerPrice = saleOffer.getOfferPrice();
        this.isWon = saleOffer.isWon();
        this.submitterUserName=saleOffer.getSubmitsOffer().getUsername();
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }

    public boolean isWon() {
        return isWon;
    }

    public void setWon(boolean won) {
        isWon = won;
    }

    public String getSubmitterUserName() {
        return submitterUserName;
    }

    public void setSubmitterUserName(String submitterUserName) {
        this.submitterUserName = submitterUserName;
    }
}
