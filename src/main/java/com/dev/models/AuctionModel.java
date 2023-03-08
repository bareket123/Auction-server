package com.dev.models;

import com.dev.objects.Auction;
import com.dev.objects.Product;

import java.text.SimpleDateFormat;

public class AuctionModel {
    private Product product;
    private String creationDate;
    private int amountOfSaleOffers;
    private String publisher ;
    private int initialPrice;

    public AuctionModel(Auction auction) {
        this.product = auction.getProduct();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        this.creationDate = simpleDateFormat.format(auction.getOpenDate());
        this.amountOfSaleOffers = auction.getSaleOffers().size();
        this.publisher = auction.getSubmitUser().getUsername();
        this.initialPrice = auction.getInitialPrice();
    }


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getAmountOfSaleOffers() {
        return amountOfSaleOffers;
    }

    public void setAmountOfSaleOffers(int amountOfSaleOffers) {
        this.amountOfSaleOffers = amountOfSaleOffers;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(int initialPrice) {
        this.initialPrice = initialPrice;
    }
}
