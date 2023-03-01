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
    private String date;
    @Column
    private String time;

    @ManyToOne
    @JoinColumn (name = "username")
    private User initiatorProposal;
    @Column
    private int amount;
    @Column
    private boolean isWon;


    public SaleOffer( User initiatorProposal, int amount, boolean isWon) {
        MyDate currentDate=new MyDate(LocalDate.now().getDayOfMonth(),LocalDate.now().getMonth().getValue(),LocalDate.now().getYear());
        this.date= String.valueOf(currentDate);
        MyDate currentTime=new MyDate(LocalTime.now().getHour(),LocalTime.now().getMinute(),LocalTime.now().getSecond(),LocalTime.now().getNano());
        this.time= String.valueOf(currentTime);
        this.initiatorProposal = initiatorProposal;
        this.amount = amount;
        this.isWon = isWon;
    }

    public SaleOffer() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public User getInitiatorProposal() {
        return initiatorProposal;
    }

    public void setInitiatorProposal(User initiatorProposal) {
        this.initiatorProposal = initiatorProposal;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int bidAmount) {
        this.amount = bidAmount;
    }

    public boolean isWon() {
        return isWon;
    }

    public void setWon(boolean won) {
        isWon = won;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
