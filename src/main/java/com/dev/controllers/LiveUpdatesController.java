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

import java.util.*;

import static com.dev.utils.Constants.EVENT_TYPING;
import static com.dev.utils.Constants.MINUTE;

@Controller
public class LiveUpdatesController {

    @Autowired
    private Persist persist;
    @Autowired
    private DashboardController dashboardController;

    private List<SseEmitter> emitterList = new ArrayList<>();
    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    /*
    -עוד בעמוד הראשי:
    יש להציג נוטיפיקציות המגיעות מהשרת באמצעות SSE
    במקרה שמישהו הגיש הצעה למוצר שאותו העליתי למכירה, או שמכרז שבו הצעתי הצעה נסגר.

     */
//    @PostConstruct
//    public void init () {
//        new Thread(() -> {
//            while (true) {
//                for (SseEmitter sseEmitter : emitterList) {
//                    try {
//                        sseEmitter.send(new Date().toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                try {
//                    Thread.sleep(SECOND);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//
//    }

    @RequestMapping (value = "/sse-handler", method = RequestMethod.GET)
    public SseEmitter handle (String submitterOfferToken, int auctionId) {
        User submitterOfferUser = persist.getUserByToken(submitterOfferToken);
        Auction auctionThatChanged=persist.getAuctionByID(auctionId);
        SseEmitter sseEmitter = null;
        if (submitterOfferUser != null) {
            if (auctionThatChanged!=null){
                sseEmitter = new SseEmitter(10L * MINUTE);
                String key = createKey(auctionThatChanged.getSubmitUser().getId(), submitterOfferUser.getId());
                this.emitterMap.put(key, sseEmitter);
            }else {
                System.out.println("auction is null*********************");
            }

        }else {
            System.out.println("offer is null*********************");
        }
        return sseEmitter;
    }
//    private SaleOffer getNewOffer(String token,List<SaleOffer> saleOffers){
//       SaleOffer newSaleOffer=null;
//        List<SaleOffer> allOfferByUser=new ArrayList<>();
//        for (SaleOffer currentOffer:saleOffers) {
//            if (currentOffer.getSubmitsOffer().getToken().equals(token)){
//                allOfferByUser.add(currentOffer);
//            }
//        }
//        if (allOfferByUser.size()>1){
//            //check time and date
//        }else if (allOfferByUser.size()==1){
//
//        }
//
//    }

    private String createKey (int senderId, int recipientId) {
        return String.format("%d_%d", senderId, recipientId);
    }

    public void sendStartTypingEvent (int senderId, int recipientId) {
        String key = createKey(recipientId, senderId);
        SseEmitter conversationEmitter = this.emitterMap.get(key);
        if (conversationEmitter != null) {
            try {
                conversationEmitter.send(EVENT_TYPING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
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
