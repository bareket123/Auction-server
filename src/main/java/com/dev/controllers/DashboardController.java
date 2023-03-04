package com.dev.controllers;

import com.dev.objects.Auction;
import com.dev.objects.Product;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;
import com.dev.responses.AllAuctionsResponse;
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
    private static int TOTAL_RESULT_OF_PAYMENTS = 0;


    @RequestMapping(value = "/get-open-auctions", method = RequestMethod.GET)
    public AllAuctionsResponse getOpenAuction() {
        return new AllAuctionsResponse(true, null, persist.getAuctionsByStatus(true));

    }

    @RequestMapping(value = "/create-new-auction", method = RequestMethod.POST)
    public BasicResponse createNewAuction(String submitUser, int initialPrice, Product product) {
        BasicResponse basicResponse = null;
        User user = persist.getUserByToken(submitUser);
        if (user != null) {
            if (product != null) {
                persist.saveProduct(product);
                Auction newAuction = new Auction(user, initialPrice, product);
                basicResponse = new BasicResponse(true, null);
                persist.addNewAuction(newAuction);
                TOTAL_RESULT_OF_PAYMENTS += TENDER_OPENING_COAST;
                user.setCredit(user.getCredit() - TENDER_OPENING_COAST);
            } else {
                basicResponse = new BasicResponse(false, ERROR_PRODUCT_NOT_SEND);
            }

        } else {
            basicResponse = new BasicResponse(false, ERROR_NO_SUCH_TOKEN);
        }
        System.out.println(basicResponse.getErrorCode());
        return basicResponse;
    }

    @RequestMapping(value = "/close-exist-auction", method ={ RequestMethod.GET,RequestMethod.POST})
    public BasicResponse closeExistAuction(int auctionId) {
        BasicResponse basicResponse;
        Auction auctionForClose = persist.getAuctionByID(auctionId);
        //SaleOffer maxOffer = persist.getOffersByID(offerId);
        if (auctionForClose != null) {
            if (auctionForClose.getSaleOffers().size() >=3) {
                persist.closeAuction(auctionForClose);
                SaleOffer winningOffer=checkHigherBid(auctionForClose.getSaleOffers());
                persist.updateWinningBid(winningOffer);
                persist.updateCreditsForUser(winningOffer.getSubmitsOffer(),winningOffer.getSubmitsOffer().getCredit()-winningOffer.getOfferPrice());
                returnMoneyForLosers(auctionForClose.getSaleOffers());
                 TOTAL_RESULT_OF_PAYMENTS +=  (WINNING_BID_COAST*winningOffer.getOfferPrice());
                persist.updateCreditsForUser(auctionForClose.getSubmitUser(),auctionForClose.getSubmitUser().getCredit()+winningOffer.getOfferPrice()* WINNING_BID_CREDIT);
                basicResponse = new BasicResponse(true, null);
            } else
            basicResponse = new BasicResponse(false, ERROR_NOT_ENOUGH_OFFERS);

    } else{
            basicResponse =new BasicResponse(false,ERROR_NO_SUCH_AUCTION);
        }


        return basicResponse;
}
private SaleOffer checkHigherBid(List<SaleOffer> saleOffers){
    double maxSaleOffer=0;
    SaleOffer winningSaleOffer=null;
        for (int i=0;i<saleOffers.size();i++) {
            SaleOffer currentSaleOffer=saleOffers.get(i);
            if (currentSaleOffer.getOfferPrice()>maxSaleOffer){
                maxSaleOffer=currentSaleOffer.getOfferPrice();
                winningSaleOffer=currentSaleOffer;
            }else if (currentSaleOffer.getOfferPrice()==maxSaleOffer ){
                  SaleOffer previousSaleOffer=saleOffers.get(i-1);
                 int compareDate = currentSaleOffer.getDate().compareTo(previousSaleOffer.getDate());
                if (compareDate>0){
                    winningSaleOffer=previousSaleOffer;
                    maxSaleOffer=previousSaleOffer.getOfferPrice();
                }else if (compareDate==0){
                    winningSaleOffer=checkByTime(currentSaleOffer,previousSaleOffer);
                    maxSaleOffer=winningSaleOffer.getOfferPrice();
                }else {
                    winningSaleOffer=currentSaleOffer;
                       maxSaleOffer=currentSaleOffer.getOfferPrice();
                }

            }
       }
        return winningSaleOffer;
}
private SaleOffer checkByTime(SaleOffer current,SaleOffer previous){
    int compareTime = current.getTime().compareTo(previous.getTime());
   SaleOffer winningSaleOffer = null;
   switch (compareTime){
        case EQUAL_TIMES:
            //chose random
            winningSaleOffer=current;
            break;
        case FIRST_AFTER_SECOND:
            winningSaleOffer=previous;
            break;
        case FIRST_BEFORE_SECOND:
           winningSaleOffer=current;
            break;

    }
    return winningSaleOffer;
}

    private void returnMoneyForLosers (List <SaleOffer> saleOffers ) {
        for (SaleOffer saleoffer : saleOffers )
            if (!saleoffer.isWon()){
                persist.updateCreditsForUser(saleoffer.getSubmitsOffer(),saleoffer.getSubmitsOffer().getCredit()+saleoffer.getOfferPrice());
            }


    }

    @RequestMapping(value = "/create-sale-offer" , method = RequestMethod.POST)
    public BasicResponse createSaleOffer (String token , double offerPrice,int productId) {
        BasicResponse basicResponse;
        User user = persist.getUserByToken(token);
        Auction auction=persist.getAuctionByProductID(productId);
        assert auction!=null;
        if (user != null && !(user.getToken().equals(auction.getSubmitUser().getToken())) ) {
            if (user.getCredit()>=offerPrice){
                if (offerPrice!=NOT_VALID_OFFER){
                    SaleOffer saleOffer = new SaleOffer(user,offerPrice);
                    TOTAL_RESULT_OF_PAYMENTS += OFFERS_SUBMIT_COAST ;
                    persist.updateCreditsForUser(user,user.getCredit()-OFFERS_SUBMIT_COAST);
                    updateCreditByPreviousOffer(user,productId);
                    persist.addNewOffer(saleOffer) ;
                    basicResponse=new BasicResponse(true,null) ;
                }else {
                    basicResponse=new BasicResponse(false,ERROR_NOT_VALID_SALE_OFFER);
                }

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
