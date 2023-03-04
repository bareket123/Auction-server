package com.dev.controllers;

import com.dev.objects.Auction;
import com.dev.objects.Product;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;
import com.dev.responses.BasicResponse;
import com.dev.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.dev.utils.Constants.*;
import static com.dev.utils.Errors.*;

@RestController
public class DashboardController {

    @Autowired
    private Persist persist;
    private static int TOTAL_RESULT_OF_PAYMENTS=0;



    @RequestMapping(value = "/get-open-auctions",method = RequestMethod.GET)
    public List<Auction> getOpenAuction(){
        return persist.getAuctionsByStatus(true);

    }

    @RequestMapping(value = "/create-new-auction" , method = RequestMethod.POST)
    public BasicResponse createNewAuction (String submitUser, int initialPrice, Product product){
        BasicResponse basicResponse =null;
        User user = persist.getUserByToken(submitUser) ;
        if( user != null ) {
            if (product != null) {
                persist.saveProduct(product);
                Auction newAuction = new Auction(user, initialPrice, product);
                basicResponse = new BasicResponse(true, null);
                persist.addNewAuction(newAuction);
                TOTAL_RESULT_OF_PAYMENTS += TENDER_OPENING_COAST;
                user.setCredit(user.getCredit() - TENDER_OPENING_COAST);
            }else {
                basicResponse = new BasicResponse(false ,ERROR_PRODUCT_NOT_SEND);
            }

        }else{
            basicResponse = new BasicResponse(false , ERROR_NO_SUCH_TOKEN);
        }
        System.out.println(basicResponse.getErrorCode());
        return basicResponse;
    }
    @RequestMapping(value = "/close-exist-auction" , method = RequestMethod.POST)
    public BasicResponse closeExistAuction (int auctionId , int offerId)
    { BasicResponse basicResponse = null ;
        Auction auctionForClose = persist.getAuctionByID(auctionId) ;
        SaleOffer maxOffer = persist.getOffersByID(offerId);
        if(auctionForClose != null)
            if(auctionForClose.getSaleOffers().size()>=3) {
                maxOffer.setWon(true);
                auctionForClose.setOpen(false);
                returnMoneyForLosers(auctionForClose.getSaleOffers()) ;
                TOTAL_RESULT_OF_PAYMENTS +=  (WINNING_BID_COAST*maxOffer.getOfferPrice());
                auctionForClose.getSubmitUser().setCredit(maxOffer.getOfferPrice()*WINNING_BID_CREADIT);
                basicResponse = new BasicResponse(true , null);
            }
            else
                basicResponse = new BasicResponse(false , ERROR_NOT_ENOUGH_OFFERS ) ;

        else
            basicResponse = new BasicResponse(false , ERROR_NO_SUCH_AUCTION);

        return  basicResponse;
    }
    private void returnMoneyForLosers (List <SaleOffer> saleOffers ) {
        for (SaleOffer saleoffer : saleOffers )
            if (!saleoffer.isWon())
                saleoffer.getSubmitsOffer().setCredit(saleoffer.getSubmitsOffer().getCredit()+saleoffer.getOfferPrice());

    }

    @RequestMapping(value = "/create-sale-offer" , method = RequestMethod.POST)
    public BasicResponse createSaleOffer (String token , double offerPrice,int productId) {
        BasicResponse basicResponse;
        User user = persist.getUserByToken(token);
        if (user != null) {
            if (user.getCredit()>=offerPrice){
                SaleOffer saleOffer = new SaleOffer(user,offerPrice);
                TOTAL_RESULT_OF_PAYMENTS += OFFERS_SUBMIT_COAST ;
                updateCreditByPreviousOffer(user,productId);
                persist.addNewOffer(saleOffer) ;
                basicResponse=new BasicResponse(true,null) ;
            }else {
                basicResponse=new BasicResponse(false,ERROR_NOT_ENOUGH_MONEY);
            }

        }else {
            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
        }

        return basicResponse;
    }
    private BasicResponse updateCreditByPreviousOffer (User user, int productId) {
        BasicResponse basicResponse;
       SaleOffer latestSaleOffer,previousSaleOffer;
        Auction auction = persist.getAuctionByProductID(productId) ;
        List<SaleOffer> allSalesOfferByAuction = auction.getSaleOffers();
        List<SaleOffer> saleOffersByCurrentUser = new ArrayList<>();

        for (SaleOffer saleOffer : allSalesOfferByAuction) {
            if (saleOffer.getSubmitsOffer().equals(user)){
                saleOffersByCurrentUser.add(saleOffer);
            }

        }

        int maxId=0;
        for (SaleOffer currentSaleOffer:saleOffersByCurrentUser) {
          if ( currentSaleOffer.getId()>maxId){
              maxId=currentSaleOffer.getId();
          }

        }
       latestSaleOffer=persist.getOffersByID(maxId);
       previousSaleOffer =persist.getOffersByID(maxId-1);
       if (latestSaleOffer.getOfferPrice()>=previousSaleOffer.getOfferPrice()){
           user.setCredit(user.getCredit()+previousSaleOffer.getOfferPrice());
           user.setCredit(user.getCredit()-latestSaleOffer.getOfferPrice());
           basicResponse=new BasicResponse(true,null);
       }else {
           basicResponse=new BasicResponse(false, ERROR_TOO_LOWER_OFFER_PRICE);
       }

       return basicResponse;
    }
    @RequestMapping(value = "get-all-auctions-by-token",method = {RequestMethod.GET})
    public List<Auction> getAllAuctionByToken(String token){
        return persist.getAuctionsByToken(token);

    }


}
