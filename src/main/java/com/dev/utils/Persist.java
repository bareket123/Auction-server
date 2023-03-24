
package com.dev.utils;

import com.dev.models.OpenAuctionModel;
import com.dev.objects.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Persist {

    private final SessionFactory sessionFactory;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public User getUserByUsername(String username) {
        User found;
        Session session = sessionFactory.openSession();
        found = (User) session.createQuery("FROM User WHERE username = :username")
                .setParameter("username", username)
                .uniqueResult();
        session.close();
        return found;
    }

    public void saveUser(User user) {
        Session session = sessionFactory.openSession();
        session.save(user);
        session.close();
    }


    public User getUserByUsernameAndToken(String username, String token) {
        User found;
        Session session = sessionFactory.openSession();
        found = (User) session.createQuery("FROM User WHERE username = :username " +
                        "AND token = :token")
                .setParameter("username", username)
                .setParameter("token", token)
                .uniqueResult();
        session.close();
        return found;
    }

    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        List<User> allUsers = session.createQuery("FROM User ").list();
        session.close();
        return allUsers;
    }

    public User getUserByToken(String token) {
        Session session = sessionFactory.openSession();
        User user = (User) session.createQuery("From User WHERE token = :token")
                .setParameter("token", token)
                .uniqueResult();
        session.close();
        return user;
    }


    public List<OpenAuctionModel> getAuctionsByStatus(boolean isOpen) {
        Session session = sessionFactory.openSession();
        List<Auction> auctions = session.createQuery("from Auction where isOpen= :isOpen").setParameter("isOpen", isOpen).list();
        session.close();

        return getOpenAuctionsModel(auctions);
    }
    private List<OpenAuctionModel> getOpenAuctionsModel(List<Auction> auctions){
        List<OpenAuctionModel> openAuctionModels=new ArrayList<>();
        for (Auction auction:auctions) {
            //check this
            OpenAuctionModel newModel= new OpenAuctionModel(auction,0);
            openAuctionModels.add(newModel);
        }
        return openAuctionModels;
    }
    public List<Auction> getAllAuctions() {
        Session session = sessionFactory.openSession();
        List<Auction> auctions = session.createQuery("from Auction ").list();
        session.close();
        return auctions;
    }


    public List<SaleOffer> getAllSaleOffers() {
        Session session = sessionFactory.openSession();
        List<SaleOffer> saleOffers = session.createQuery("from SaleOffer  order by offerPrice  desc ").list();
        session.close();
        return saleOffers;

    }

    public void addNewAuction(Auction auction) {
        Session session = sessionFactory.openSession();
        session.save(auction);
        session.close();
    }

    public Auction getAuctionByID(int id) {
        Session session = sessionFactory.openSession();
        Auction wantedAuction = (Auction) session.createQuery("From Auction WHERE id=:id").setParameter("id", id).uniqueResult();
        session.close();
        return wantedAuction;
    }

    public void addNewOffer(SaleOffer saleOffer) {
        Session session = sessionFactory.openSession();
        session.save(saleOffer);
        session.saveOrUpdate(saleOffer);
        session.close();

    }
    public void addNewOfferToAuctionList(Auction auction,SaleOffer offerToAdd){
        Session session = sessionFactory.openSession();
        Transaction transaction=session.beginTransaction();
        auction.getSaleOffers().add(offerToAdd);
        session.saveOrUpdate(auction);
        transaction.commit();
        session.close();
    }


    public List<Auction> getAuctionsByToken(String token) {
        Session session = sessionFactory.openSession();
        List<Auction> allAuctionsByUsers =  session.createQuery("From Auction WHERE submitUser.token = :token").setParameter("token", token).list();
        session.close();
        return allAuctionsByUsers;
    }


    public List<Auction> getOpenAuctionsByToken(String token) {
        Session session = sessionFactory.openSession();
        List<Auction> allAuctionsByUsers =  session.createQuery("From Auction WHERE submitUser.token = :token and isOpen=:isOpen").setParameter("token", token).setParameter("isOpen",true).list();
        session.close();
        return allAuctionsByUsers;
    }

    public void updateCreditsForUser(User user , double updatedCredits){
        Session session=sessionFactory.openSession();
        Transaction transaction=session.beginTransaction();
        if (user!=null ){
            user.setCredit(updatedCredits);
        }
        session.saveOrUpdate(user);
        transaction.commit();
        session.close();

    }
    public void closeAuction(Auction auctionForClose){
        Session session=sessionFactory.openSession();
        Transaction transaction=session.beginTransaction();
        if (auctionForClose!=null){
            auctionForClose.setOpen(false);
        }
        session.saveOrUpdate(auctionForClose);
        transaction.commit();
        session.close();


    }
    public void updateWinningBid(SaleOffer winningSaleOffer){
        Session session=sessionFactory.openSession();
        Transaction transaction=session.beginTransaction();
        if (winningSaleOffer!=null){
            winningSaleOffer.setWon(true);
        }
        session.saveOrUpdate(winningSaleOffer);
        transaction.commit();
        session.close();


    }
    public List<SaleOffer> getAllWinningOffers(){
        Session session = sessionFactory.openSession();
        List<SaleOffer> winningOffers =  session.createQuery("From SaleOffer WHERE isWon=:isWon").setParameter("isWon",true).list();
        session.close();
        return winningOffers;


    }



}
