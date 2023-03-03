package com.dev.objects;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "auction")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String openDate;
    @Column
    private boolean isOpen;
    @ManyToOne
    @JoinColumn (name = "username")
    private User submitUser;
    @Column
    private int initialPrice;
    @ManyToMany
    @JoinColumn (name = "sales_offers")
    private List<SaleOffer> saleOffers;
    @ManyToOne
    @JoinColumn (name = "name")
    private Product product;


    public Auction(boolean isOpen, User submitUser, int initialPrice, List<SaleOffer> saleOffers, Product product) {
        MyDate currentDate=new MyDate(LocalDate.now().getDayOfMonth(),LocalDate.now().getMonth().getValue(),LocalDate.now().getYear());
        this.openDate = String.valueOf(currentDate);
        this.isOpen = isOpen;
        this.submitUser = submitUser;
        this.initialPrice = initialPrice;
        this.saleOffers = saleOffers;
        this.product = product;
    }

    public Auction() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public User getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(User submitUser) {
        this.submitUser = submitUser;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }

    public List<SaleOffer> getSaleOffers() {
        return saleOffers;
    }

    public void setSaleOffers(List<SaleOffer> saleOffers) {
        this.saleOffers = saleOffers;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
