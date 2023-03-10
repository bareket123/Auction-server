package com.dev.responses;

import com.dev.models.MyOffersModel;
import com.dev.models.MyProductModel;

import java.util.List;

public class MyOfferResponse extends BasicResponse{
     private List<MyOffersModel> myOffersModels;

    public MyOfferResponse(List<MyOffersModel> myOffersModels) {
        this.myOffersModels = myOffersModels;
    }

    public MyOfferResponse(boolean success, Integer errorCode, List<MyOffersModel> myOffersModels) {
        super(success, errorCode);
        this.myOffersModels = myOffersModels;
    }

    public List<MyOffersModel> getMyOffersModels() {
        return myOffersModels;
    }

    public void setMyOffersModels(List<MyOffersModel> myOffersModels) {
        this.myOffersModels = myOffersModels;
    }
}
