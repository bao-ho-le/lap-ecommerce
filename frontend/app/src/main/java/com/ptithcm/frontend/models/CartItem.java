package com.ptithcm.frontend.models;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {

    private final long id;
    private final Product product;
    private int quantity;

    public CartItem(long id, Product product, int quantity) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}