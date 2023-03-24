package com.dev.controllers;

import com.dev.models.*;
import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;
import com.dev.responses.*;
import com.dev.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.text.DecimalFormat;
import java.util.*;
import static com.dev.utils.Constants.*;
import static com.dev.utils.Errors.*;

@RestController
public class DashboardController {

    @Autowired
    private Persist persist;
    @Autowired
    private LiveUpdatesController liveUpdatesController;


    @RequestMapping(value = "/get-open-auctions", method = RequestMethod.GET)
    public BasicResponse getOpenAuction(String token) {
        BasicResponse basicResponse;
        List<OpenAuctionModel> openAuctionModels=new ArrayList<>();
        User user=persist.getUserByToken(token);
        if (user!=null || token.equals("Admin")){
            for (OpenAuctionModel openAuction:persist.getAuctionsByStatus(true)) {
                Auction currentAuction=persist.getAuctionByID(openAuction.getAuctionId());
                if(currentAuction!=null){
                    OpenAuctionModel openAuctionModel=new OpenAuctionModel(currentAuction,getSalesOfferByUser(user,currentAuction).size());
                    openAuctionModels.add(openAuctionModel);
                }
            }
          basicResponse=new OpenAuctionsResponse(true,null,openAuctionModels);
        }else {
            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
        }
       return basicResponse;
    }


    @RequestMapping(value = "/create-new-auction", method = {RequestMethod.POST})
    public BasicResponse createNewAuction(String submitUser, int initialPrice, String productName, String productPhoto, String productDescription) {
        BasicResponse basicResponse;
        User user = persist.getUserByToken(submitUser);
        if (user != null) {
            if (productName != null && productPhoto != null && productDescription != null) {
                Auction newAuction = new Auction(user, initialPrice, productName, productPhoto, productDescription);
                basicResponse = new BasicResponse(true, null);
                persist.addNewAuction(newAuction);
                persist.updateCreditsForUser(user,user.getCredit() - AUCTION_OPENING_COAST);
            } else {
                basicResponse = new BasicResponse(false, ERROR_PRODUCT_DETAILS_NOT_SEND);
            }
        } else {
            basicResponse = new BasicResponse(false, ERROR_NO_SUCH_TOKEN);
        }
        System.out.println(basicResponse.getErrorCode());
        return basicResponse;
    }

    @RequestMapping(value = "/close-exist-auction", method = {RequestMethod.POST, RequestMethod.GET})
    public BasicResponse closeExistAuction(int auctionId) {
        BasicResponse basicResponse;
        Auction auctionForClose = persist.getAuctionByID(auctionId);
        if (auctionForClose != null) {
            if (auctionForClose.isOpen()) {
                if (auctionForClose.getSaleOffers().size() >= 3) {
                    persist.closeAuction(auctionForClose);
                    SaleOffer winningOffer = checkHigherBid(auctionForClose.getSaleOffers());
                    persist.updateWinningBid(winningOffer);
                    List<SaleOffer> losingSalesOffers = auctionForClose.getSaleOffers();
                    losingSalesOffers.remove(winningOffer);
                    returnMoneyForLosers(losingSalesOffers, auctionForClose, winningOffer.getSubmitsOffer());
                    persist.updateCreditsForUser(auctionForClose.getSubmitUser(), auctionForClose.getSubmitUser().getCredit() + winningOffer.getOfferPrice() * WINNING_BID_CREDIT);
                    liveUpdatesController.submittedAuctionWasClosed(auctionForClose.getSaleOffers());
                    basicResponse = new BasicResponse(true, null);
                } else
                    basicResponse = new BasicResponse(false, ERROR_NOT_ENOUGH_OFFERS);
            }else {
                basicResponse = new BasicResponse(false, ERROR_AUCTION_IS_CLOSED);
            }
        } else {
            basicResponse = new BasicResponse(false, ERROR_NO_SUCH_AUCTION);
        }
        return basicResponse;
    }

    private SaleOffer checkHigherBid(List<SaleOffer> saleOffers) {
        double maxSaleOffer = 0;
        SaleOffer winningSaleOffer = null;
        for (int i = 0; i < saleOffers.size(); i++) {
            SaleOffer currentSaleOffer = saleOffers.get(i);
            if (currentSaleOffer.getOfferPrice() > maxSaleOffer) {
                maxSaleOffer = currentSaleOffer.getOfferPrice();
                winningSaleOffer = currentSaleOffer;
            } else if (currentSaleOffer.getOfferPrice() == maxSaleOffer) {
                SaleOffer previousSaleOffer = saleOffers.get(i - 1);
                int compareDate = currentSaleOffer.getDate().compareTo(previousSaleOffer.getDate());
                if (compareDate > TIME_OR_DATE_DIFFERENCE) {
                    winningSaleOffer = previousSaleOffer;
                    maxSaleOffer = previousSaleOffer.getOfferPrice();
                } else if (compareDate == TIME_OR_DATE_DIFFERENCE) {
                    winningSaleOffer = checkWinnerByTime(currentSaleOffer, previousSaleOffer);
                    maxSaleOffer = winningSaleOffer.getOfferPrice();
                } else {
                    winningSaleOffer = currentSaleOffer;
                    maxSaleOffer = currentSaleOffer.getOfferPrice();
                }
            }
        }
        return winningSaleOffer;
    }

    private SaleOffer checkWinnerByTime(SaleOffer current, SaleOffer previous) {
        int compareTime = current.getTime().compareTo(previous.getTime());
        SaleOffer winningSaleOffer;
        if (compareTime <= TIME_OR_DATE_DIFFERENCE) {
            winningSaleOffer = current;
        } else {
            winningSaleOffer = previous;
        }
        return winningSaleOffer;
    }

    private void returnMoneyForLosers(List<SaleOffer> losingOffers, Auction auction, User winner) {
        Set<User> losingUsers = new HashSet<>();
        for (SaleOffer offer : losingOffers) {
            losingUsers.add(offer.getSubmitsOffer());
        }
        for (User currentUser : losingUsers) {
            if (!winner.getToken().equals(currentUser.getToken())) {
                List<SaleOffer> saleOffersByUser = getSalesOfferByUser(currentUser, auction);
                if (saleOffersByUser.size() > 0) {
                    SaleOffer highestOfferOfUser = checkHigherBid(saleOffersByUser);
                    persist.updateCreditsForUser(currentUser, currentUser.getCredit() + highestOfferOfUser.getOfferPrice());
                }
            }
        }
    }

    @RequestMapping(value = "/create-sale-offer", method = {RequestMethod.POST})
    public BasicResponse createSaleOffer(String token, double offerPrice, int auctionId) {
        BasicResponse basicResponse;
        User user = persist.getUserByToken(token);
        Auction auction = persist.getAuctionByID(auctionId);
        if (user != null) {
            if (auction != null) {
                List<SaleOffer> offersByUser=getSalesOfferByUser(user,auction);
                if (!(user.getToken().equals(auction.getSubmitUser().getToken()))) {
                    if (user.getCredit() >= (offerPrice+OFFERS_SUBMIT_COAST) && auction.getInitialPrice() <= offerPrice&&offerPrice != NOT_VALID_OFFER ) {
                            SaleOffer highestOfferByUser = checkHigherBid(offersByUser);
                            if (highestOfferByUser!=null){
                                if (offerPrice<highestOfferByUser.getOfferPrice()) {
                                    return new BasicResponse(false,ERROR_TOO_LOWER_OFFER_PRICE);
                                }
                                if (offerPrice==highestOfferByUser.getOfferPrice()) {
                                    return new BasicResponse(false,ERROR_TOO_LOWER_OFFER_PRICE);
                                }
                            }

                            SaleOffer newSaleOffer = new SaleOffer(user, offerPrice);
                            persist.addNewOffer(newSaleOffer);
                            persist.addNewOfferToAuctionList(auction, newSaleOffer);
                            persist.updateCreditsForUser(user, user.getCredit() - OFFERS_SUBMIT_COAST);
                            updateCreditByHigherOffer(user, auction, newSaleOffer);
                            liveUpdatesController.addedNewOffer(auction.getSubmitUser().getToken());
                            basicResponse = new BasicResponse(true, null);


                    } else {
                        basicResponse = new BasicResponse(false, ERROR_NOT_ENOUGH_MONEY);
                    }
                } else {
                    basicResponse = new BasicResponse(false, ERROR_NOT_VALID_OFFER_USER);
                }
            } else {
                basicResponse = new BasicResponse(false, ERROR_NO_SUCH_AUCTION);
            }
        } else {
            basicResponse = new BasicResponse(false, ERROR_NO_SUCH_TOKEN);
        }

        return basicResponse;
    }



    private void updateCreditByHigherOffer(User user, Auction auction, SaleOffer newSaleOffer) {
        List<SaleOffer> saleOffersByUser = getSalesOfferByUser(user, auction);
        if (saleOffersByUser.size() > 1) {
            saleOffersByUser.remove(newSaleOffer);
            SaleOffer highestWithoutTheNew = checkHigherBid(saleOffersByUser);
            System.out.println("current highest without: " + highestWithoutTheNew.getOfferPrice());
            System.out.println("current new: " + newSaleOffer.getOfferPrice());
            if (newSaleOffer.getOfferPrice() > highestWithoutTheNew.getOfferPrice()) {
                persist.updateCreditsForUser(user, user.getCredit() - newSaleOffer.getOfferPrice());
                persist.updateCreditsForUser(user, user.getCredit() + highestWithoutTheNew.getOfferPrice());
            } else if (newSaleOffer.getOfferPrice() == highestWithoutTheNew.getOfferPrice()) {
                persist.updateCreditsForUser(user, user.getCredit() - newSaleOffer.getOfferPrice());

            }
        } else {
            persist.updateCreditsForUser(user, user.getCredit() - newSaleOffer.getOfferPrice());
        }
    }

    private List<SaleOffer> getSalesOfferByUser(User user, Auction auction) {
        List<SaleOffer> allSalesOfferByAuction = auction.getSaleOffers();
        List<SaleOffer> saleOffersByUser = new ArrayList<>();
        for (SaleOffer saleOffer : allSalesOfferByAuction) {
            if(user!=null){
                if (saleOffer.getSubmitsOffer().getToken().equals(user.getToken())) {
                    saleOffersByUser.add(saleOffer);
                }
            }else {
                System.out.println("user is null");
            }


        }
        return saleOffersByUser;
    }

    @RequestMapping(value = "get-all-auctions" , method = RequestMethod.GET)
    public List<AuctionIdModel> getAllModelsAuctions(){
        List<AuctionIdModel> auctionIdModels=new ArrayList<>();
        for (Auction auction:persist.getAllAuctions()) {
            AuctionIdModel newAuctionIdModel=new AuctionIdModel(auction);
            auctionIdModels.add(newAuctionIdModel);
        }
        return auctionIdModels;
    }

    @RequestMapping(value = "get-product-by-id" , method = RequestMethod.GET)
    public BasicResponse getProductById(int auctionId,String token){
        BasicResponse basicResponse;
       Auction auction=persist.getAuctionByID(auctionId);
       User user=persist.getUserByToken(token);
       if (user!=null || token.equals("Admin")){
           if (auction!=null){
               List<SaleOffer> saleOffers=getSalesOfferByUser(user,auction);
               ProductModel productModel=new ProductModel(auction,saleOffers);

              if (auction.getSubmitUser().getToken().equals(token) && !token.equals("Admin") ){
                basicResponse=new ProductModelResponse(true,null,productModel,true);
              }else {
                  basicResponse=new ProductModelResponse(true,null,productModel,false);
              }
           }else {
               basicResponse=new BasicResponse(false,ERROR_NO_SUCH_AUCTION);
           }
       }else {
          basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
       }
       return basicResponse ;

    }
    @RequestMapping(value = "/get-open-auction-size-by-token" ,method = {RequestMethod.GET})
    public int getOpenAuctionByToken(String token){
    return persist.getOpenAuctionsByToken(token).size();

    }

    @RequestMapping(value = "get-username-by-token",method = {RequestMethod.GET})
    public String getUsernameByToken(String token){
        return persist.getUserByToken(token).getUsername();

    }
    @RequestMapping(value = "get-model-all-auctions-by-token",method = {RequestMethod.GET})
    public List<MyProductModel> getAllProductModelByToken(String token){
        List<MyProductModel> myProductModels=new ArrayList<>();
        MyProductModel newProductModel;
        SaleOffer highestOffer;
        List<Auction> myAuctions=persist.getAuctionsByToken(token);
        for (Auction auction:myAuctions) {
            highestOffer=checkHigherBid(auction.getSaleOffers());
            if (highestOffer!=null){
                 newProductModel=new MyProductModel(auction,new SaleOfferModel(highestOffer));
            }else {
                 newProductModel=new MyProductModel(auction);
            }
            myProductModels.add(newProductModel);
        }
        return myProductModels;
    }

    @RequestMapping(value = "get-user-credits",method = {RequestMethod.GET})
    public BasicResponse getUserCredits(String userToken){
       User user=persist.getUserByToken(userToken);
       BasicResponse basicResponse;
       double userCredit;
       if (user!=null){
           DecimalFormat decimalFormat = new DecimalFormat("#.###");
           userCredit = Double.parseDouble(decimalFormat.format(user.getCredit()));
           basicResponse=new UserCreditsResponse(true,null,userCredit);
       }else {
          basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
       }
       return basicResponse;
    }

    @RequestMapping (value = "/get-all-sales-offers",method = {RequestMethod.GET})
    public List<SaleOffer> getAllSaleOffers(){
        return persist.getAllSaleOffers();
    }

    @RequestMapping (value = "/get-my-offers-model",method = {RequestMethod.GET})
    public BasicResponse getAllMyOffersModel(String token){
        User user=persist.getUserByToken(token);
        List<MyOffersModel> myOffersModels=new ArrayList<>();
        BasicResponse basicResponse;
        if (user!=null){
            for (Auction auction:persist.getAllAuctions()) {
                List<SaleOffer> currentOffersInAuction=getSalesOfferByUser(user,auction);
                for (SaleOffer currentOffer:currentOffersInAuction) {
                 MyOffersModel newOffer=new MyOffersModel(currentOffer,auction);
                    myOffersModels.add(newOffer);
                }
            }
            if (myOffersModels.size()>0){
                basicResponse=new MyOfferResponse(true,null,myOffersModels);
            }else {
                basicResponse=new BasicResponse(false,ERROR_NO_OFFERS);
            }
        }else {
            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
        }
        return basicResponse;
    }
}