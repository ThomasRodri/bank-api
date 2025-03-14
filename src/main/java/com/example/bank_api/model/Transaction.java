package com.example.bank_api.model;

public class Transaction {
    private Long id;
    private Float amount;

    public Transaction() {}

    public Transaction(Long id, Float amount) {
        this.id = id;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
