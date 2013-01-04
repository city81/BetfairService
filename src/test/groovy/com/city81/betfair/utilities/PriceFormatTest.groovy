package com.city81.betfair.utilities;

import spock.lang.*

class PriceFormatTest extends Specification {

	def "round down prices to nearest valid betfair price"() {
		
		expect:             
		PriceFormat.round(a, b) == c                           
		
		where:             
		a                          | b       | c        
		PriceFormat.DIRECTION.DOWN | 100.25  | 100.0
		PriceFormat.DIRECTION.DOWN | 5.1231  | 5.1
		PriceFormat.DIRECTION.DOWN | 1.1316  | 1.13
		PriceFormat.DIRECTION.DOWN | 15.821  | 15.5
		PriceFormat.DIRECTION.DOWN | 8.1231  | 8.0
		PriceFormat.DIRECTION.DOWN | 3.71    | 3.7
		PriceFormat.DIRECTION.DOWN | 99.31   | 95.0
		PriceFormat.DIRECTION.DOWN | 24.31   | 24.0
	}
	
	def "round up prices to nearest valid betfair price"() {
		
		expect:
		PriceFormat.round(a, b) == c
		
		where:             
		a                          | b       | c        
		PriceFormat.DIRECTION.UP   | 100.25  | 110.0
		PriceFormat.DIRECTION.UP   | 5.1231  | 5.2
		PriceFormat.DIRECTION.UP   | 1.1316  | 1.14
		PriceFormat.DIRECTION.UP   | 15.821  | 16.0
		PriceFormat.DIRECTION.UP   | 8.1231  | 8.2
		PriceFormat.DIRECTION.UP   | 3.71    | 3.75
		PriceFormat.DIRECTION.UP   | 99.31   | 100.0
		PriceFormat.DIRECTION.UP   | 24.311  | 25.0
	}
	
}

