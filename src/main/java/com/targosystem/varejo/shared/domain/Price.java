package com.targosystem.varejo.shared.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Price {
    private final BigDecimal value;

    public Price(BigDecimal value) {
        Objects.requireNonNull(value, "Price value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Price cannot be negative");
        }
        this.value = value.setScale(2, BigDecimal.ROUND_HALF_UP); // Duas casas decimais
    }

    public static Price of(String value) {
        return new Price(new BigDecimal(value));
    }

    public static Price of(BigDecimal value) {
        return new Price(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    public Price multiply(Quantity quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null");
        return new Price(this.value.multiply(new BigDecimal(quantity.getValue())));
    }

    public Price add(Price other) {
        Objects.requireNonNull(other, "Other price cannot be null");
        return new Price(this.value.add(other.value));
    }

    public Price subtract(Price other) {
        Objects.requireNonNull(other, "Other price cannot be null");
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Resulting price cannot be negative");
        }
        return new Price(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return value.equals(price.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "R$" + value.toString();
    }
}