package com.dev.objects;

public class MyDate {

    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;
    private int second;
    private int milliSecond;

    public MyDate(int day, int month, int year, int hour,int minute,int second,int milliSecond) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour=hour;
        this.minute=minute;
        this.second=second;
        this.milliSecond=milliSecond;
    }
    public MyDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;

    }
    public MyDate(int hour,int minute,int second,int milliSecond) {
        this.hour=hour;
        this.minute=minute;
        this.second=second;
        this.milliSecond=milliSecond;
    }



    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public void setMilliSecond(int milliSecond) {
        this.milliSecond = milliSecond;
    }
    public String toString(){

        return day +"/" + month + "/" +year;


    }
}

