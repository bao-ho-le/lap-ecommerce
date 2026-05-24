package com.ptithcm.frontend.models;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderSummary implements Serializable {

    private final long id;
    private final String code;
    private final String status;
    private final String orderDate;
    private final BigDecimal totalAmount;
    private final int itemCount;

    public OrderSummary(long id, String code, String status, String orderDate, BigDecimal totalAmount, int itemCount) {
        this.id = id;
        this.code = code;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public int getItemCount() {
        return itemCount;
    }
}