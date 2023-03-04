package com.dev.controllers;

import com.dev.objects.Auction;
import com.dev.objects.User;
import com.dev.responses.AllUsersResponse;
import com.dev.responses.BasicResponse;
import com.dev.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import static com.dev.utils.Errors.*;


@RestController
public class ManagerController {
    @Autowired
    private Persist persist;




// טבלה לא מתעדכנת אבל פונקציה עובדת- לבדוק
    @RequestMapping (value = "/update-credits",method = RequestMethod.POST)
    public BasicResponse updateCredits(String token,int updatedCredits){
        BasicResponse basicResponse;
        User user= persist.getUserByToken(token);
        if (user!=null){
            user.setCredit(updatedCredits);
            basicResponse=new BasicResponse(true,null);
        }else {
            basicResponse=new BasicResponse(false,ERROR_NO_SUCH_TOKEN);
        }

        return basicResponse;

    }
    @RequestMapping(value = "/get-open-auction-by-token", method = RequestMethod.GET)
    public List<Auction> getOpenAuctionByToken (String token) {
        User user = persist.getUserByToken(token);
        List<Auction> openAuction = persist.getAuctionsByStatus(true) ;
        List<Auction> openAuctionByUser = new ArrayList<>() ;
        for (Auction auction : openAuction )
        {  if (auction.getSubmitUser().equals(user)) {
            openAuctionByUser.add(auction) ;
        }

        }
        return openAuctionByUser;
    }
    @RequestMapping(value = "get-all-users" , method = RequestMethod.GET)
    public AllUsersResponse getAllUsers(){
        return new AllUsersResponse(true,null,persist.getAllUsers());
    }
    @RequestMapping(value = "get-all-auctions-by-token",method = {RequestMethod.GET})
    public List<Auction> getAllAuctionByUser(String token){
        return persist.getAuctionByUser(token);

    }

}
/*
במחלקה - manageController
שינינו את סוג הערך המוחזר מLIST לAllUsersResponse
@RequestMapping(value = "get-all-users" , method = RequestMethod.GET)
public AllUsersResponse getAllUsers(){
   return new AllUsersResponse(true,null,persist.getAllUsers());
}

בפונקציה updateCredit :
אין שינוי בטבלת sql לאחר שינוי קרדיט.
אבל הפונקציה כן עובדת רק הטבלה לא מתעדכנת .


—איך אתן מקשרות מכרז למוצר?

–להוסיף ל- product description   שדה של תיאור מוצר .

–השדה בטבלה של auction  מכיל מוצר ולא שם מוצר.

 */
