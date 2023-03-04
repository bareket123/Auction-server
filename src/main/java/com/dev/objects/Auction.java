package com.dev.objects;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auction")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column
    private LocalDate openDate;
    @Column
    private boolean isOpen;
    @ManyToOne
    @JoinColumn (name = "username")
    private User submitUser;
    @Column
    private int initialPrice;
    @ManyToMany
    @JoinColumn (name = "sales_offers_id")
    private List<SaleOffer> saleOffers;
    @ManyToOne
    @JoinColumn (name= "product_id")
    private Product product;


    public Auction(boolean isOpen, User submitUser, int initialPrice, List<SaleOffer> saleOffers, Product product) {
        this.openDate = LocalDate.now();
        this.isOpen = isOpen;
        this.submitUser = submitUser;
        this.initialPrice = initialPrice;
        this.saleOffers = saleOffers;
        this.product = product;
    }

    public Auction( User submitUser, int initialPrice, Product product) {
        this.openDate = LocalDate.now();
        this.isOpen = true;
        this.submitUser = submitUser;
        this.initialPrice = initialPrice;
        this.saleOffers = new ArrayList<>();
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

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
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
