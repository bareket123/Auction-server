package com.dev.controllers;



import com.dev.objects.Auction;
import com.dev.objects.SaleOffer;
import com.dev.objects.User;
import com.dev.utils.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dev.utils.Constants.*;

@Controller
public class LiveUpdatesController {


//    @ModelAttribute
//    public void init (HttpServletRequest request) {
//        System.out.println(request.getRequestURI());
//    }

    @Autowired
    private Persist persist;

    private Map<String,SseEmitter> emitterMap = new HashMap<>();

    @RequestMapping (value = "/sse-handler", method = RequestMethod.GET)

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
        List<String> offersTokens = new ArrayList<>();
        for (SaleOffer saleOffer : saleOffers) {
            String currentSubmitOfferToken = saleOffer.getSubmitsOffer().getToken();
            if (!offersTokens.contains(currentSubmitOfferToken)) {
                offersTokens.add(currentSubmitOfferToken);
            }
        }

        List<SseEmitter> emitterList = new ArrayList<>();
        for (String offer : offersTokens) {
            SseEmitter emitter = this.emitterMap.get(offer);
            if (emitter != null) {
                emitterList.add(emitter);
            }
        }

        for (SseEmitter sseEmitter:emitterList) {
            if (sseEmitter!=null){
                try {
                    sseEmitter.send(EVENT_CLOSED_AUCTION);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }




}
