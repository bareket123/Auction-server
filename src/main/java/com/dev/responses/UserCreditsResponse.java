package com.dev.responses;

public class UserCreditsResponse extends BasicResponse {
    private double credit;

    public UserCreditsResponse(double credit) {
        this.credit = credit;
    }

    public UserCreditsResponse(boolean success, Integer errorCode, double credit) {
        super(success, errorCode);
        this.credit = credit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
}
