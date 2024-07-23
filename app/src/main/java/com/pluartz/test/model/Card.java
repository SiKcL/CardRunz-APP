package com.pluartz.test.model;

public class Card {

    String name,category,color,photo;

    Double card_price;

    public  Card(){}

    public Card(String name, String category, Double card_price, String photo){
        this.name = name;
        this.category = category;
        this.color = color;
        this.card_price = card_price;
        this.photo = photo;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor(){return color;}

    public void setColor(String color) {this.color = color;}

    public Double getCard_price() {
        return card_price;
    }

    public void setVaccine_price(Double card_price) {
        this.card_price = card_price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
