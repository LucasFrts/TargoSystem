package com.targosystem.varejo.shared.domain;

import java.util.Objects;

public class Quantity {
    private final int value;

    public Quantity(int value) {
        if (value < 0) {
            throw new DomainException("Quantity cannot be negative");
        }
        this.value = value;
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public int getValue() {
        return value;
    }

    public Quantity add(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return new Quantity(this.value + other.value);
    }

    public Quantity subtract(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        int result = this.value - other.value;
        if (result < 0) {
            throw new DomainException("Resulting quantity cannot be negative");
        }
        return new Quantity(result);
    }

    public boolean isGreaterThan(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return this.value > other.value;
    }

    public boolean isLessThan(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return this.value < other.value;
    }

    public boolean isEqualTo(Quantity other) {
        Objects.requireNonNull(other, "Other quantity cannot be null");
        return this.value == other.value;
    }

    public boolean isZero() {
        return this.value == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}