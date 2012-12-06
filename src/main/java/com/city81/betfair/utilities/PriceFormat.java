package com.city81.betfair.utilities;

import java.text.DecimalFormat;

/**
 * This class rounds a price either <code>UP</code> or <code>DOWN</code> to the 
 * nearest valid Betfair price.
 * 
 * @author geraint.jones
 *
 */
public class PriceFormat {


    private static DecimalFormat priceFormatOneDec = new DecimalFormat("###.#");
    private static DecimalFormat priceFormatTwoDec = new DecimalFormat("##.##");
    private static DecimalFormat priceFormatThreeDec = new DecimalFormat("#.###");

    public enum DIRECTION {
    	DOWN,
    	UP
    }
  
	protected static String formatPriceFormatOneDec(Double d) {
	    synchronized(priceFormatOneDec) {
	        return priceFormatOneDec.format(d);
	    }
	}    
    
	protected static String formatPriceFormatTwoDec(Double d) {
	    synchronized(priceFormatTwoDec) {
	        return priceFormatTwoDec.format(d);
	    }
	}    	
	
	protected static String formatPriceFormatThreeDec(Double d) {
	    synchronized(priceFormatThreeDec) {
	        return priceFormatThreeDec.format(d);
	    }
	} 	
    
    /**
     * For a given <code>DIRECTION</code>, round the supplied price to the nearest valid Betfair price.
     * 
     * @param direction direction of rounding
     * @param price the price to be rounded
     * @return the rounded price
     */
    public static double round(DIRECTION direction, double price) {
        double newPrice;
        if (price > 100.0) {
            newPrice = (Double.parseDouble(formatPriceFormatOneDec(price)));
            while (((newPrice * 10) % 100) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice + 0.1)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice - 0.1)));
                }
            }
        } else if (price > 50.0) {
            newPrice = (Double.parseDouble(formatPriceFormatOneDec(price)));
            while (((newPrice * 10) % 50) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice + 0.1)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice - 0.1)));
                }
            }
        } else if (price > 30.0) {
            newPrice = (Double.parseDouble(formatPriceFormatOneDec(price)));
            while (((newPrice * 10) % 20) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice + 0.1)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice - 0.1)));
                }
            }
        } else if (price > 20.0) {
            newPrice = (Double.parseDouble(formatPriceFormatOneDec(price)));
            while (((newPrice * 10) % 10) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice + 0.1)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatOneDec(newPrice - 0.1)));
                }
            }
        } else if (price > 10.0) {
            newPrice = (Double.parseDouble(formatPriceFormatTwoDec(price)));
            while (((newPrice * 10) % 5) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatTwoDec(newPrice + 0.01)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatTwoDec(newPrice - 0.01)));
                }
            }
        } else if (price > 6.0) {
            newPrice = (Double.parseDouble(formatPriceFormatTwoDec(price)));
            while (((newPrice * 10) % 2) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatTwoDec(newPrice + 0.01)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatTwoDec(newPrice - 0.01)));
                }
            }
        } else if (price > 4.0) {
            newPrice = (Double.parseDouble(formatPriceFormatTwoDec(price)));
            while (((newPrice * 10) % 1) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatTwoDec(newPrice + 0.01)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatTwoDec(newPrice - 0.01)));
                }
            }
        } else if (price > 3.0) {
            newPrice = (Double.parseDouble(formatPriceFormatThreeDec(price)));
            while (((newPrice * 100) % 5) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatThreeDec(newPrice + 0.001)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatThreeDec(newPrice - 0.001)));
                }
            }
        } else if (price > 2.0) {
            newPrice = (Double.parseDouble(formatPriceFormatThreeDec(price)));
            while (((newPrice * 100) % 2) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatThreeDec(newPrice + 0.001)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatThreeDec(newPrice - 0.001)));
                }
            }
        } else {
            newPrice = (Double.parseDouble(formatPriceFormatThreeDec(price)));
            while (((newPrice * 100) % 1) > 0) {
                if (direction.equals(DIRECTION.UP)) {
                    newPrice = (Double.parseDouble(formatPriceFormatThreeDec(newPrice + 0.001)));
                } else {
                    newPrice = (Double.parseDouble(formatPriceFormatThreeDec(newPrice - 0.001)));
                }
            }
        }
        return newPrice;
    }
    
}

