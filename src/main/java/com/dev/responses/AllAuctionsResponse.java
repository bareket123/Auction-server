package com.dev.responses;

import com.dev.models.OpenAuctionModel;
import com.dev.objects.Auction;

import java.util.List;

public class AllAuctionsResponse extends BasicResponse {
    private List<OpenAuctionModel> auctions;


    public AllAuctionsResponse(List<OpenAuctionModel> auctions) {
        this.auctions = auctions;
    }

    public AllAuctionsResponse(boolean success, Integer errorCode, List<OpenAuctionModel> auctions) {
        super(success, errorCode);
        this.auctions = auctions;
    }

    public List<OpenAuctionModel> getAuctions() {
        return auctions;
    }

    public void setAuctions(List<OpenAuctionModel> auctions) {
        this.auctions = auctions;
    }
}
