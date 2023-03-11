package com.dev.models;

import com.dev.objects.Auction;

public class AuctionIdModel {

    private int auctionId;


    public AuctionIdModel(Auction auction) {
        this.auctionId = auction.getId();
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }
}
