package com.city81.betfair.utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class rounds a price either <code>UP</code> or <code>DOWN</code> to the 
 * nearest valid Betfair price.
 * 
 * @author geraint.jones
 *
 */
public class PriceFormat {

    public enum DIRECTION {
    	DOWN,
    	UP
    }
    
    /**
     * For a given <code>DIRECTION</code>, round the supplied price to the nearest valid Betfair price.
     * 
     * @param direction direction of rounding
     * @param price the price to be rounded
     * @return the rounded price
     */
    public static double round(DIRECTION direction, double price) {
    	
    	BigDecimal newPrice = BigDecimal.valueOf(price);
    	
        if (price > 100.0) {
            newPrice = newPrice.setScale(1, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(10)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.1));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.1));
                }
            }
        } else if (price > 50.0) {
            newPrice = newPrice.setScale(1, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(5)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.1));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.1));
                }
            }
        } else if (price > 30.0) {
            newPrice = newPrice.setScale(1, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(2)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.1));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.1));
                }
            }
        } else if (price > 20.0) {
            newPrice = newPrice.setScale(1, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(1)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.1));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.1));
                }
            }
        } else if (price > 10.0) {
            newPrice = newPrice.setScale(2, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(0.5)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.01));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.01));
                }
            }
        } else if (price > 6.0) {
            newPrice = newPrice.setScale(2, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(0.2)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.01));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.01));
                }
            }
        } else if (price > 4.0) {
            newPrice = newPrice.setScale(2, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(0.1)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.01));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.01));
                }
            }
        } else if (price > 3.0) {
            newPrice = newPrice.setScale(3, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(0.05)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.001));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.001));
                }
            }
        } else if (price > 2.0) {
            newPrice = newPrice.setScale(3, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(0.02)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.001));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.001));
                }
            }
        } else {
            newPrice = newPrice.setScale(3, RoundingMode.HALF_EVEN);
            while (newPrice.remainder(BigDecimal.valueOf(0.01)).doubleValue() > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = newPrice.add(BigDecimal.valueOf(0.001));
                } else {
                    newPrice = newPrice.subtract(BigDecimal.valueOf(0.001));
                }
            }
        }
        return newPrice.doubleValue();
    } 
    
}

