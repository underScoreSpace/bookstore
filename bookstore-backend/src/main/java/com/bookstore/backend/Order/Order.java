package com.bookstore.backend.Order;

import com.bookstore.backend.User.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_number", length = 30, nullable = false)
    private String orderNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal tax;

    @Column(name = "shipping_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingFee;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(length = 20, nullable = false)
    private String status = "PENDING";

    @Column(name = "payment_ref", length = 100)
    private String paymentRef;

    @Column(name = "placed_at")
    private LocalDateTime placedAt = LocalDateTime.now();

    @Column(name = "ship_name", length = 200, nullable = false)
    private String shipName;

    @Column(name = "ship_address1", length = 200, nullable = false)
    private String shipAddress1;

    @Column(name = "ship_address2", length = 200)
    private String shipAddress2;

    @Column(name = "ship_city", length = 100, nullable = false)
    private String shipCity;

    @Column(name = "ship_region", length = 100, nullable = false)
    private String shipRegion;

    @Column(name = "ship_postal", length = 30, nullable = false)
    private String shipPostal;

    @Column(name = "ship_country", length = 2, nullable = false)
    private String shipCountry = "US";

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();



    public int getId() { return id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentRef() { return paymentRef; }
    public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }

    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }

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

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
