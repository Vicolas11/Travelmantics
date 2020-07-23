package com.jwhh.travelmantics;

public class TravelDetail {
    private String deal;
    private String price;
    private String description;
    private String imageUrl;

    public TravelDetail() {}

    public TravelDetail(String deal, String price, String description, String imageUrl) {
        this.setDeal(deal);
        this.setPrice(price);
        this.setDescription(description);
        this.setImageUrl(imageUrl);
    }

    public String getDeal() {
        return deal;
    }

    public void setDeal(String deal) {
        this.deal = deal;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
