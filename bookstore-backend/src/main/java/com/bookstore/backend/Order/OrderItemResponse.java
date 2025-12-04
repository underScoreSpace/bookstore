package com.bookstore.backend.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderItemResponse {
    private int id;
    private String title;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal grandTotal;

    public OrderItemResponse(int id, String title, int quantity,
                             BigDecimal unitPrice, BigDecimal grandTotal) {
        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.grandTotal = grandTotal;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getGrandTotal() { return grandTotal; }
}
