package com.unicenta.poc.application;

public enum StockMovementReason {
    PURCHASE(1),
    SALE(-1),
    TRANSFER(1000),
    RETURN_SUPPLIER(-2),
    RETURN_CUSTOMER(2),
    BREAKAGE(-3),
    ADJUSTMENT_ADD(4),
    ADJUSTMENT_MINUS(-4),
    SAMPLE_OUT(-5),
    FREE(-6);

    private final int value;

    StockMovementReason(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static StockMovementReason fromValue(int value) {
        for (StockMovementReason reason : StockMovementReason.values()) {
            if (reason.getValue() == value) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Invalid stock movement reason: " + value);
    }
}
