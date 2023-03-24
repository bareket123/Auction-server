package com.dev.responses;

import com.dev.models.OpenAuctionModel;

import java.util.List;

public class OpenAuctionsResponse extends BasicResponse {
    private List<OpenAuctionModel> auctions;


    public OpenAuctionsResponse(List<OpenAuctionModel> auctions) {
        this.auctions = auctions;
    }

    public OpenAuctionsResponse(boolean success, Integer errorCode, List<OpenAuctionModel> auctions) {
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
