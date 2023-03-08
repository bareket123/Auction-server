package com.dev.models;

import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;

public class SaleOfferModel {

    private double offerPrice;
    private boolean isWon;

    public SaleOfferModel(SaleOffer saleOffer) {
        this.offerPrice = saleOffer.getOfferPrice();
        this.isWon = saleOffer.isWon();
    }









}
