package com.bookstore.backend.Order;
import java.math.BigDecimal;

public class CheckoutResponse {
    private int orderId;
    private String orderNumber;
    private BigDecimal total;
    private String message;

    public CheckoutResponse(int orderId, String orderNumber, BigDecimal total, String message) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.total = total;
        this.message = message;
    }

    public int getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public BigDecimal getTotal() { return total; }
    public String getMessage() { return message; }
}
