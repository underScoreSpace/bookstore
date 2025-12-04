package com.bookstore.backend.Order;

public class CheckoutRequest {
    private int userId;

    private String shipName;
    private String shipAddress1;
    private String shipAddress2;
    private String shipCity;
    private String shipRegion;
    private String shipPostal;
    private String shipCountry;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }

    public String getShipAddress1() { return shipAddress1; }
    public void setShipAddress1(String shipAddress1) { this.shipAddress1 = shipAddress1; }

    public String getShipAddress2() { return shipAddress2; }
    public void setShipAddress2(String shipAddress2) { this.shipAddress2 = shipAddress2; }

    public String getShipCity() { return shipCity; }
    public void setShipCity(String shipCity) { this.shipCity = shipCity; }

    public String getShipRegion() { return shipRegion; }
    public void setShipRegion(String shipRegion) { this.shipRegion = shipRegion; }

    public String getShipPostal() { return shipPostal; }
    public void setShipPostal(String shipPostal) { this.shipPostal = shipPostal; }

    public String getShipCountry() { return shipCountry; }
    public void setShipCountry(String shipCountry) { this.shipCountry = shipCountry; }
}
