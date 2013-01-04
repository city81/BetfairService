package com.city81.betfair.utilities;

import spock.lang.*

class PriceOperatorTest extends Specification {

	def "decrement prices in line with betfair tick sizes"() {
		
		expect:             
		PriceOperator.decrementPrice(a) == b                           
		
		where:             
		a     | b        
		5.0   | 4.9
		1.99  | 1.98
		8.0   | 7.8
		2.02  | 2.0
		11.0  | 10.5
	}
	
	def "increment prices in line with betfair tick sizes"() {
		
		expect:
		PriceOperator.incrementPrice(a) == b
		
		where:
		a     | b
		5.0   | 5.1
		1.67  | 1.68
		8.4   | 8.6
		2.02  | 2.04
		12.0  | 12.5
	}
	
}

