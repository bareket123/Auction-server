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
    @Column
    private String productName;

    @Column
    private String productPhoto;

    @Column
    private String productDescription;

    public Auction( boolean isOpen, User submitUser, int initialPrice, List<SaleOffer> saleOffers, String productName, String productPhoto, String productDescription) {
        this.openDate =LocalDate.now();
        this.isOpen = isOpen;
        this.submitUser = submitUser;
        this.initialPrice = initialPrice;
        this.saleOffers = saleOffers;
        this.productName = productName;
        this.productPhoto = productPhoto;
        this.productDescription = productDescription;
    }

    public Auction(User submitUser, int initialPrice, String productName, String productPhoto, String productDescription) {
        this.openDate = LocalDate.now();
        this.isOpen = true;
        this.submitUser = submitUser;
        this.initialPrice = initialPrice;
        this.saleOffers = new ArrayList<>();
        this.productName = productName;
        this.productPhoto = productPhoto;
        this.productDescription = productDescription;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
