package com.dev.controllers;

import com.dev.models.OpenAuctionModel;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;
import com.dev.responses.AllAuctionsResponse;
import com.dev.responses.AllUsersResponse;
import com.dev.responses.BasicResponse;
import com.dev.utils.Constants;
import com.dev.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import static com.dev.utils.Errors.*;
import static java.lang.Integer.parseInt;


@RestController
public class ManagerController {
    @Autowired
    private Persist persist;


    @RequestMapping (value = "/update-credits",method = {RequestMethod.POST})
    public BasicResponse updateCredits(String token,int updatedCredits){
        BasicResponse basicResponse;
        User user= persist.getUserByToken(token);
        if (user!=null){
            persist.updateCreditsForUser(user,updatedCredits);
            basicResponse=new BasicResponse(true,null);
            System.out.println(user.getCredit());

        }else {
            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
        }

        return basicResponse;

    }
    @RequestMapping(value = "/get-open-auction-by-token", method = RequestMethod.GET)
    public AllAuctionsResponse getOpenAuctionByToken (String token) {
        User user = persist.getUserByToken(token);
        List<OpenAuctionModel> openAuction = persist.getAuctionsByStatus(true);
        List<OpenAuctionModel> openAuctionByUser = new ArrayList<>() ;
        for (OpenAuctionModel auction : openAuction ) {
            String submitUserToken=persist.getAuctionByID(auction.getAuctionId()).getSubmitUser().getToken();
            if (submitUserToken.equals(user.getToken())) {
                openAuctionByUser.add(auction) ;

        }

        }
        return new AllAuctionsResponse(true,null,openAuctionByUser);
    }
    @RequestMapping(value = "get-all-users" , method = RequestMethod.GET)
    public AllUsersResponse getAllUsers(){
        return new AllUsersResponse(true,null,persist.getAllUsers());
    }




    @RequestMapping(value = "get-total-system-payments" , method = RequestMethod.GET)
    public double totalSystemPayments(){
        double totalResultOfPayment=0;
        System.out.println("****************************************");

        List<SaleOffer > winningOffers= persist.getAllWinningOffers();
        for (SaleOffer offer:winningOffers) {
            totalResultOfPayment+=offer.getOfferPrice()* Constants.WINNING_BID_COAST;
            System.out.println("****************************************");
            System.out.println("offer:"+ offer.getOfferPrice());
            System.out.println("****************************************");

        }
        totalResultOfPayment+=persist.getAllAuctions().size()*Constants.AUCTION_OPENING_COAST;
        totalResultOfPayment+=persist.getAllSaleOffers().size()*Constants.OFFERS_SUBMIT_COAST;
        System.out.println("*****************"+totalResultOfPayment+"*************");
        return totalResultOfPayment;
    }

}


