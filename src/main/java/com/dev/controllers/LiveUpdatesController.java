package com.dev.controllers;



import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;
import com.dev.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dev.utils.Constants.*;

@Controller
public class LiveUpdatesController {

    @Autowired
    private Persist persist;

    private List<SseEmitter> emitterList = new ArrayList<>();
    private Map<String,SseEmitter> emitterMap = new HashMap<>();


//    @RequestMapping (value = "/sse-handler", method = RequestMethod.GET)
//                               //מגיש הצעה+ מכרז עבורו מוגשת הצעה
//    public SseEmitter handle (String submitUserToken,int auctionId) throws IOException {
//        SseEmitter sseEmitter = null;
//        Auction auction=persist.getAuctionByID(auctionId);
//        User user=persist.getUserByToken(submitUserToken);
//        if (user!=null){
//            if (auction!=null){
//                sseEmitter = new SseEmitter(10L * MINUTE);
//               String key = createKey(user.getId(), auction.getSubmitUser().getId());
//                this.emitterMap.put(key,sseEmitter);
//            }
//
//        }
//
//        return sseEmitter;
//    }
    @RequestMapping (value = "/sse-handler", method = RequestMethod.GET)
    //מגיש הצעה+ מכרז עבורו מוגשת הצעה
    public SseEmitter handle (String submitUserToken){
        SseEmitter sseEmitter = null;
        User user=persist.getUserByToken(submitUserToken);
        if (user!=null){
                sseEmitter=this.emitterMap.get(submitUserToken);
            if (sseEmitter==null){
                sseEmitter=new SseEmitter(10L * MINUTE);
                this.emitterMap.put(submitUserToken,sseEmitter);
            }

        }

        return sseEmitter;
    }


    private String createKey (int senderId, int recipientId) {
        return String.format("%d_%d", senderId, recipientId);
    }

    public void addedNewOffer (String token) {
        SseEmitter messageEmitter = this.emitterMap.get(token);
        if (messageEmitter != null) {
            try {
                messageEmitter.send(EVENT_ADDED_NEW_OFFER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void submittedAuctionWasClosed (List<SaleOffer> saleOffers) {
       List<String> offers=saleOffers.stream()
               .map(saleOffer -> saleOffer.getSubmitsOffer().getToken()).distinct().collect(Collectors.toList());
       List<SseEmitter> emitterList=offers.stream().map(this.emitterMap::get).collect(Collectors.toList());
       emitterList.forEach(sseEmitter -> {
           if (sseEmitter!=null){
               try {
                   sseEmitter.send(EVENT_CLOSED_AUCTION);
               }catch (Exception e){
                   e.printStackTrace();
               }
           }else {
               System.out.println("sse is null");
           }
       });
    }

//    public void sendConversationMessage (int senderId, int recipientId, String content) {
//        String key = createKey(recipientId, senderId);
//        SseEmitter conversationEmitter = this.emitterMap.get(key);
//        if (conversationEmitter != null) {
//            MessageModel messageModel = new MessageModel();
//            messageModel.setContent(content);
//            messageModel.setSendDate(new Date().toString());
//            try {
//                conversationEmitter.send(messageModel);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
