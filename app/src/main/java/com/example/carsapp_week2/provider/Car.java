package com.example.carsapp_week2.provider;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/* Represents a table in the database. */
@Entity(tableName = "cars")
public class Car {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "carId")
    private int id;

    @ColumnInfo(name = "carMaker")
    private String maker;

    @ColumnInfo(name = "carModel")
    private String model;

    @ColumnInfo(name = "carYear")
    private int year;

    @ColumnInfo(name = "carColor")
    private String color;

    @ColumnInfo(name = "carSeats")
    private int seats;

    @ColumnInfo(name = "carPrice")
    private int price;

    @ColumnInfo(name = "carAddress")
    private String address;

    public Car(String maker, String model,int year, String color,int seats, int price, String address){
        this.maker = maker;
        this.model = model;
        this.year = year;
        this.color = color;
        this.seats = seats;
        this.price = price;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
