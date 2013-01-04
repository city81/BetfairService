package com.city81.betfair.service;

import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService
import com.betfair.publicapi.types.exchange.v5.APIRequestHeader
import com.betfair.publicapi.types.global.v3.APIRequestHeader
import static org.mockito.Mockito.*
import spock.lang.*

class AbstractServiceTest extends Specification {

	def mockExchangeService = org.mockito.Mockito.mock(BFExchangeService)
	def mockExchangeHeader = org.mockito.Mockito.mock(com.betfair.publicapi.types.exchange.v5.APIRequestHeader)
	
	def mockGlobalService = org.mockito.Mockito.mock(BFGlobalService)
	def mockGlobalHeader = org.mockito.Mockito.mock(com.betfair.publicapi.types.global.v3.APIRequestHeader)
	
}

