package com.city81.betfair.service;

import spock.lang.*
import com.city81.betfair.utilities.PriceFormat
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsReq;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsResp;
import groovy.mock.interceptor.*
import static org.mockito.Mockito.*

class AccountsServiceTest extends AbstractServiceTest {

	def "get account balance"() {
		
		def getAccountFundsResp = new GetAccountFundsResp();
		getAccountFundsResp.setAvailBalance(100.0)
		getAccountFundsResp.setBalance(200.0)
		
		when(mockExchangeService.getAccountFunds(((GetAccountFundsReq)anyObject()))).thenReturn(getAccountFundsResp);
				
		given:
		def accountsService = new AccountsService(mockExchangeService, mockExchangeHeader)
		
		when:             
		def accountsResp = accountsService.getAccountBalances()                    
		
		then:             
		assert accountsResp.getAvailBalance() == 100.0
		assert accountsResp.getBalance() == 200.0
		
	}
	
}

