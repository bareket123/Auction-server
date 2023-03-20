package com.dev.objects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table
public class SaleOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column
    private LocalDate date;
    @Column
    private LocalTime time;

    @ManyToOne
    @JoinColumn (name = "username")
    private User submitsOffer;
    @Column
    private double offerPrice;
    @Column
    private boolean isWon;



    public SaleOffer(User submitsOffer, double offerPrice) {
        this.date= LocalDate.now();
        this.time= LocalTime.now();
        this.submitsOffer = submitsOffer;
        this.offerPrice = offerPrice;
        this.isWon = false;
    }

    public SaleOffer() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public User getSubmitsOffer() {
        return submitsOffer;
    }

    public void setSubmitsOffer(User initiatorProposal) {
        this.submitsOffer = initiatorProposal;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(double bidAmount) {
        this.offerPrice = bidAmount;
    }

    public boolean isWon() {
        return isWon;
    }

    public void setWon(boolean won) {
        isWon = won;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
