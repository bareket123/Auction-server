package com.dev.responses;

import com.dev.objects.SaleOffer;

import java.util.List;

public class SaleOffersResponse extends BasicResponse{

    private List<SaleOffer> saleOfferList;

    public SaleOffersResponse(List<SaleOffer> saleOfferList) {
        this.saleOfferList = saleOfferList;
    }

    public SaleOffersResponse(boolean success, Integer errorCode, List<SaleOffer> saleOfferList) {
        super(success, errorCode);
        this.saleOfferList = saleOfferList;
    }


    public List<SaleOffer> getSaleOfferList() {
        return saleOfferList;
    }

    public void setSaleOfferList(List<SaleOffer> saleOfferList) {
        this.saleOfferList = saleOfferList;
    }
}
