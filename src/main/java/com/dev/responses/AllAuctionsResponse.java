package com.dev.responses;

import com.dev.objects.Auction;

import java.util.List;

public class AllAuctionsResponse extends BasicResponse {
    private List<Auction> auctions;


    public AllAuctionsResponse(List<Auction> auctions) {
        this.auctions = auctions;
    }

    public AllAuctionsResponse(boolean success, Integer errorCode, List<Auction> auctions) {
        super(success, errorCode);
        this.auctions = auctions;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(List<Auction> auctions) {
        this.auctions = auctions;
    }
}
