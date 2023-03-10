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

import java.util.*;
import java.util.stream.Collectors;

import static com.dev.utils.Constants.*;
import static com.dev.utils.Errors.*;

@RestController
public class DashboardController {

    @Autowired
    private Persist persist;
    @Autowired
    private static int TOTAL_RESULT_OF_PAYMENTS = 0;
    @Autowired
    private LiveUpdatesController liveUpdatesController;


    @RequestMapping(value = "/get-open-auctions", method = RequestMethod.GET)
    public AllAuctionsResponse getOpenAuction() {
//          persist.sortedOpenAuctionByHigherOffer();
        return new AllAuctionsResponse(true, null, persist.getAuctionsByStatus(true));

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
                TOTAL_RESULT_OF_PAYMENTS += AUCTION_OPENING_COAST;
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
                    // persist.updateCreditsForUser(winningOffer.getSubmitsOffer(),winningOffer.getSubmitsOffer().getCredit()-winningOffer.getOfferPrice());
                    List<SaleOffer> losingSalesOffers = auctionForClose.getSaleOffers();
                    losingSalesOffers.remove(winningOffer);
                    returnMoneyForLosers(losingSalesOffers, auctionForClose, winningOffer.getSubmitsOffer());
                    TOTAL_RESULT_OF_PAYMENTS += (WINNING_BID_COAST * winningOffer.getOfferPrice());
                    persist.updateCreditsForUser(auctionForClose.getSubmitUser(), auctionForClose.getSubmitUser().getCredit() + winningOffer.getOfferPrice() * WINNING_BID_CREDIT);
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
                    winningSaleOffer = checkByTime(currentSaleOffer, previousSaleOffer);
                    maxSaleOffer = winningSaleOffer.getOfferPrice();
                } else {
                    winningSaleOffer = currentSaleOffer;
                    maxSaleOffer = currentSaleOffer.getOfferPrice();
                }

            }
        }
        return winningSaleOffer;
    }

    private SaleOffer checkByTime(SaleOffer current, SaleOffer previous) {
        int compareTime = current.getTime().compareTo(previous.getTime());
        SaleOffer winningSaleOffer = null;
        if (compareTime <= TIME_OR_DATE_DIFFERENCE) {
            winningSaleOffer = current;
        } else {
            winningSaleOffer = previous;
        }

//   switch (compareTime){
//        case EQUAL_TIMES:
//            //chose random
//            winningSaleOffer=current;
//            break;
//        case FIRST_AFTER_SECOND:
//            winningSaleOffer=previous;
//            break;
//        case FIRST_BEFORE_SECOND:
//           winningSaleOffer=current;
//            break;
//
//    }
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
                    if (user.getCredit() >= offerPrice && auction.getInitialPrice() <= offerPrice&&offerPrice != NOT_VALID_OFFER ) {
                            SaleOffer highestOfferByUser = checkHigherBid(offersByUser);
                            if (highestOfferByUser!=null){
                                if (offerPrice<highestOfferByUser.getOfferPrice()) {
                                    return new BasicResponse(false,ERROR_TOO_LOWER_OFFER_PRICE);
                                }
                            }

                            SaleOffer newSaleOffer = new SaleOffer(user, offerPrice);
                            persist.addNewOffer(newSaleOffer);
                            persist.addNewOfferToAuctionList(auction, newSaleOffer);
                            TOTAL_RESULT_OF_PAYMENTS += OFFERS_SUBMIT_COAST;
                            persist.updateCreditsForUser(user, user.getCredit() - OFFERS_SUBMIT_COAST);
                            // updateCreditByPreviousOffer(user,productId);
                            updateCreditByHigherOffer(user, auction, newSaleOffer);
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
//    public List<SaleOffer> findTwoMaxOffers(List<Integer> numbers, int n) {
//        List<Integer> maxValues = new ArrayList<>();
//
//        if (n > numbers.size()) {
//            throw new IllegalArgumentException("n cannot be greater than list size");
//        }
//
//        // Sort the list in descending order
//        Collections.sort(numbers, Collections.reverseOrder());
//
//        // Add the n largest values to the maxValues list
//        for (int i = 0; i < n; i++) {
//            maxValues.add(numbers.get(i));
//        }
//
//        return maxValues;
//    }



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

    @RequestMapping(value = "/test2", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse  getMyOffer(String token, int auctionId) {
        User user = persist.getUserByToken(token);
        BasicResponse basicResponse;
        Auction auction = persist.getAuctionByID(auctionId);
        if (user != null) {
            if (auction != null) {
                List<SaleOffer> mySaleOfferByAuction = getSalesOfferByUser(user, auction);
                if (mySaleOfferByAuction != null) {
                    basicResponse = new SaleOffersResponse(true, null, mySaleOfferByAuction);

                } else
                    basicResponse = new BasicResponse(false, ERROR_NO_SUCH_AUCTION);

            } else
                basicResponse = new BasicResponse(false, ERROR_NO_OFFERS);


        } else
            basicResponse = new BasicResponse(false, ERROR_NO_SUCH_TOKEN);


        return basicResponse;
    }


    @RequestMapping(value = "/test" , method = {RequestMethod.GET,RequestMethod.POST})

    public  HashMap<Auction,List<SaleOffer>> getMapOfMyAuction(String token){
        HashMap<Auction,List<SaleOffer>> myAuctions = new HashMap<>();
        List <Auction> allAuctionsISubmitted=new ArrayList<>();
        BasicResponse basicResponse=null;
        User user=persist.getUserByToken(token);
        List <Auction> allAuctions= persist.getAllAuctions();
        if (user!=null){
            if (allAuctions!=null){
//                List <Auction> allAuctionsISubmitted=new ArrayList<>();
                for (Auction  auction: allAuctions) {
                    if (auction.getSaleOffers().stream().filter((item)->{
                        return item.getSubmitsOffer().getToken().equals(token);
                    }).collect(Collectors.toList()).size()>0)
                        allAuctionsISubmitted.add(auction);
                }

                for (Auction myAuction:allAuctionsISubmitted) {
                    myAuctions.put(myAuction,getSalesOfferByUser(user,myAuction));
                }


            //    basicResponse=new AllAuctionsResponse(true,null,allAuctionsISubmitted);

            }else{
                basicResponse=new BasicResponse(false,ERROR_NO_OFFERS);
            }

        }else {
            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);

        }


        return myAuctions;


    }
    @RequestMapping(value = "get-all-auctions" , method = RequestMethod.GET)
    public List<AuctionIdModel> getAllAuctionsSize(){
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
       List<SaleOffer> saleOffers=getSalesOfferByUser(user,auction);
       if (user!=null){
           if (auction!=null){
               ProductModel productModel=new ProductModel(auction,saleOffers);
              if (auction.getSubmitUser().getToken().equals(token)){
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



//    @RequestMapping (value = "/get-my-auctions",method = {RequestMethod.GET})
//    public BasicResponse getMyAuctions(String token){
//        BasicResponse basicResponse;
//        User user=persist.getUserByToken(token);
//       List <Auction> allAuctions= persist.getAllAuctions();
//        if (user!=null){
//            if (allAuctions!=null){
//                List <OpenAuctionModel> allAuctionsISubmitted=new ArrayList<>();
//                for (OpenAuctionModel  auction: allAuctions) {
//                    if (auction.getSaleOffers().stream().filter((item)->{
//                        return item.getSubmitsOffer().getToken().equals(token);
//                    }).collect(Collectors.toList()).size()>0)
//                    allAuctionsISubmitted.add(auction);
//                }
//
//                basicResponse=new AllAuctionsResponse(true,null,allAuctionsISubmitted);
//
//            }else{
//                basicResponse=new BasicResponse(false,ERROR_NO_OFFERS);
//            }
//
//        }else {
//            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
//
//        }
//
//
//        return basicResponse;
//    }

/*
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
 List<SaleOOfer> allSalesOfferByCurrentAuction
 */
    @RequestMapping(value = "get-all-auctions-by-token",method = {RequestMethod.GET})
    public List<Auction> getAllAuctionByToken(String token){
        return persist.getAuctionsByToken(token);

    }

    @RequestMapping(value = "get-username-by-token",method = {RequestMethod.GET})
    public String getUsernameByToken(String token){
        return persist.getUserByToken(token).getUsername();

    }
    @RequestMapping(value = "get-Model-all-auctions-by-token",method = {RequestMethod.GET})
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
           userCredit=user.getCredit();
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
//        List<SaleOffer> myOffers=new ArrayList<>();
        BasicResponse basicResponse;
        if (user!=null){
            for (Auction auction:persist.getAllAuctions()) {
                List<SaleOffer> currentOffersInAuction=getSalesOfferByUser(user,auction);
                for (SaleOffer currentOffer:currentOffersInAuction) {
                 MyOffersModel newOffer=new MyOffersModel(currentOffer,auction);
                    myOffersModels.add(newOffer);
                }
//                if (currentOffersInAuction.size()>0){
//                    myOffers.addAll(currentOffersInAuction);
////
//                }
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
//    @RequestMapping(value = "get-sorted-sale-offers-by-auction",method = {RequestMethod.GET})
//    public BasicResponse getSortedSaleOffersByAuction(int auctionId){
//        BasicResponse basicResponse;
//        Auction auction=persist.getAuctionByID(auctionId);
//        if (auction!=null){
//          List<SaleOffer> saleOffersByAuction=persist.getSortedSaleOffersListByAuction(auction);
//          if (saleOffersByAuction!=null){
//
//              basicResponse=new SaleOffersResponse(true,null,saleOffersByAuction);
//          }else {
//              basicResponse=new BasicResponse(false,ERROR_NO_OFFERS);
//          }
//
//
//        }else {
//            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_AUCTION);
//
//        }
//       return basicResponse;
//    }

        public int getTotalResultOfPayments (){
        return TOTAL_RESULT_OF_PAYMENTS;
        }
    @RequestMapping (value = "/added-new-offer", method = RequestMethod.POST)
    public BasicResponse notificationOfNewOffer(String token, int auctionId) {
        BasicResponse basicResponse = null;
        User user = persist.getUserByToken(token);
        Auction auction=persist.getAuctionByID(auctionId);
        if (user != null) {
            if (auction!=null){
                liveUpdatesController.addedNewOffer(user.getId(), auction.getSubmitUser().getId());
                basicResponse=new BasicResponse(true,null);
            }else {
                basicResponse=new BasicResponse(false,ERROR_NO_SUCH_AUCTION);
            }

        } else {
            basicResponse = new BasicResponse(false, ERROR_NO_SUCH_TOKEN);
        }

        return basicResponse;
    }



}
