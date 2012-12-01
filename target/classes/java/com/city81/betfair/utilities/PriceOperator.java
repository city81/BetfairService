package com.city81.betfair.utilities;

import java.text.DecimalFormat;

/**
 * This class decrements or increments a valid Betfair price by one tick 
 * depending on the price supplied.
 * 
 * 
 * @author geraint.jones
 *
 */
public class PriceOperator {
    
    private static DecimalFormat priceFormat = new DecimalFormat("##.##");

 
    /**
     * Decrement a price by one tick
     * 
     * @param price the price to be decremented
     * @return the price minus one tick
     */
    public static double decrementPrice(double price) {
        double newPrice;
        if (price > 100.0) {
            newPrice = price - 10.0;
        } else if (price > 50.0) {
            newPrice = price - 5.0;
        } else if (price > 30.0) {
            newPrice = price - 2.0;
        } else if (price > 20.0) {
            newPrice = price - 1.0;
        } else if (price > 10.0) { 
            newPrice = price - 0.5;
        } else if (price > 6.0) {
            newPrice = price - 0.2;
        } else if (price > 4.0) {
            newPrice = price - 0.1;
        } else if (price > 3.0) {
            newPrice = price - 0.05;
        } else if (price > 2.0) {
            newPrice = price - 0.02;
        } else {
            newPrice = price - 0.01;
        }

        newPrice = Double.parseDouble(priceFormat.format(newPrice));
        return newPrice;
    }

    /**
     * Increment a price by one tick
     * 
     * @param price the price to be incremented
     * @return the price plus one tick
     */
    public static double incrementPrice(double price) {
        double newPrice;
        if (price >= 100.0) {
            newPrice = price + 10.0;
        } else if (price >= 50.0) {
            newPrice = price + 5.0;
        } else if (price >= 30.0) {
            newPrice = price + 2.0;
        } else if (price >= 20.0) {
            newPrice = price + 1.0;
        } else if (price >= 10.0) {
            newPrice = price + 0.5;
        } else if (price >= 6.0) {
            newPrice = price + 0.2;
        } else if (price >= 4.0) {
            newPrice = price + 0.1;
        } else if (price >= 3.0) {
            newPrice = price + 0.05;
        } else if (price >= 2.0) {
            newPrice = price + 0.02;
        } else {
            newPrice = price + 0.01;
        }

        newPrice = Double.parseDouble(priceFormat.format(newPrice));
        return newPrice;
    }

}

