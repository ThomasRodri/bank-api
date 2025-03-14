package com.example.bank_api.model;

public class Pix {
    private Long originId;
    private Long destinyId;
    private Float amount;

    public Pix() {
    }

    public Pix(Long originId, Long destinyId, Float amount) {
        this.originId = originId;
        this.destinyId = destinyId;
        this.amount = amount;
    }

    public Long getOriginId() {
        return originId;
    }

    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    public Long getDestinyId() {
        return destinyId;
    }

    public void setDestinyId(Long destinyId) {
        this.destinyId = destinyId;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
