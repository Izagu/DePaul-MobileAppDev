package com.example.stonks;
import java.io.Serializable;
import java.util.Objects;

public class stocks implements Serializable, Comparable<stocks> {
    // Serializable needed to add as extra to intent

    private String name;
    private String symbol;
   // private String capital;
    private String value;
    //private String region;
    //private String subRegion;
    private String change;
    private String percent;
    //private String citizen;
    //private String callingCodes;
    //private String borders;

    public stocks(String name, String symbol, String value,
                   String change, String percent) {
        this.name = name;
        this.symbol = symbol;
        this.change = change;
        this.value = value;
        this.percent = percent;

    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getChange() {
        return change;
    }
    public String getValue() {
        return value;
    }
    public String getPercent() {
        return percent;
    }

    /*public String getRegion() {
        return region;
    }

    public String getSubRegion() {
        return subRegion;
    }



    public String getCitizen() {
        return citizen;
    }

    public String getCallingCodes() {
        return callingCodes;
    }

    public String getBorders() {
        return borders;
    }
    public String getCapital() {
        return capital;
    }
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        stocks stok = (stocks) o;
        return name.equals(stok.name) &&
                symbol.equals(stok.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol);
    }

    @Override
    public int compareTo(stocks stok) {
        return name.compareTo(stok.getName());
    }
}
