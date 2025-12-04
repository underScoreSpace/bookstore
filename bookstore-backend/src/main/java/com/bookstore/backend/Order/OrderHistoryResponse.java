package com.bookstore.backend.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryResponse {
    private int id;
    private String orderNumber;
    private BigDecimal total;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> items;

    public OrderHistoryResponse(int id, String orderNumber, BigDecimal total,
                                LocalDateTime placedAt, List<OrderItemResponse> items) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.total = total;
        this.placedAt = placedAt;
        this.items = items;
    }

    public int getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getPlacedAt() { return placedAt; }
    public List<OrderItemResponse> getItems() { return items; }
}
