package com.city81.betfair.service;

import spock.lang.*
import com.city81.betfair.utilities.PriceFormat
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.betfair.publicapi.types.exchange.v5.ArrayOfRunnerPrices;
import com.betfair.publicapi.types.exchange.v5.GetMarketErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesResp;
import com.betfair.publicapi.types.exchange.v5.Market;
import com.betfair.publicapi.types.exchange.v5.MarketPrices;
import com.betfair.publicapi.types.exchange.v5.ArrayOfPrice;
import com.betfair.publicapi.types.exchange.v5.ArrayOfRunner;
import com.betfair.publicapi.types.exchange.v5.Price;
import com.betfair.publicapi.types.exchange.v5.Runner;
import com.betfair.publicapi.types.exchange.v5.RunnerPrices;

import groovy.mock.interceptor.*
import static org.mockito.Mockito.*

class MarketsServiceTest extends AbstractServiceTest {

	@Shared def marketPrices = new MarketPrices()
	@Shared def market = new Market()
	
	def setupSpec() {
		
		//
		// setup market prices data
		//
		def runnerPrice1Price = new Price()
		runnerPrice1Price.setPrice(10.0)
		def runnerPrice1PriceArray = new ArrayOfPrice()
		runnerPrice1PriceArray.getPrice().add(runnerPrice1Price)
		def runnerPrice1 = new RunnerPrices()
		runnerPrice1.setSelectionId(123)
		runnerPrice1.setBestPricesToBack(runnerPrice1PriceArray);
		
		def runnerPrice2Price = new Price()
		runnerPrice2Price.setPrice(5.0)
		def runnerPrice2PriceArray = new ArrayOfPrice()
		runnerPrice2PriceArray.getPrice().add(runnerPrice2Price)
		def runnerPrice2 = new RunnerPrices()
		runnerPrice2.setSelectionId(456)
		runnerPrice2.setBestPricesToBack(runnerPrice2PriceArray);
		
		def runnerPrice3Price = new Price()
		runnerPrice3Price.setPrice(2.0)
		def runnerPrice3PriceArray = new ArrayOfPrice()
		runnerPrice3PriceArray.getPrice().add(runnerPrice3Price)
		def runnerPrice3 = new RunnerPrices()
		runnerPrice3.setSelectionId(123456789)
		runnerPrice3.setBestPricesToBack(runnerPrice3PriceArray);
		
		def runnerPrice4Price = new Price()
		runnerPrice4Price.setPrice(2.6)
		def runnerPrice4PriceArray = new ArrayOfPrice()
		runnerPrice4PriceArray.getPrice().add(runnerPrice4Price)
		def runnerPrice4 = new RunnerPrices()
		runnerPrice4.setSelectionId(789)
		runnerPrice4.setBestPricesToBack(runnerPrice4PriceArray);
		
		def prices = new ArrayOfRunnerPrices()
		prices.getRunnerPrices().add(runnerPrice1)
		prices.getRunnerPrices().add(runnerPrice2)
		prices.getRunnerPrices().add(runnerPrice3)
		prices.getRunnerPrices().add(runnerPrice4)		
		marketPrices.setRunnerPrices(prices)
		
		//
		// setup market data
		//
		def runner1 = new Runner()
		runner1.setSelectionId(123)
		runner1.setName("Liverpool")		
		
		def runner2 = new Runner()
		runner2.setSelectionId(456)
		runner2.setName("Cardiff")
		
		def runner3 = new Runner()
		runner3.setSelectionId(123456789)
		runner3.setName("Swansea")
		
		def runner4 = new Runner()
		runner4.setSelectionId(789)
		runner4.setName("Fulham")
		
		def runners = new ArrayOfRunner()
		runners.getRunner().add(runner1)
		runners.getRunner().add(runner2)
		runners.getRunner().add(runner3)
		runners.getRunner().add(runner4)
		market.setRunners(runners)
		
	}	
	
	def "get favourite"() {
		
		def marketId = 1;
		
		def getMarketPricesResp = new GetMarketPricesResp();
		getMarketPricesResp.setMarketPrices(marketPrices)
		
		when(mockExchangeService.getMarketPrices(
			((GetMarketPricesReq)anyObject()))).thenReturn(getMarketPricesResp);
				
		given:
		def marketsService = new MarketsService(mockExchangeService, mockExchangeHeader)
		
		when:             
		def favouriteId = marketsService.getFavourite(marketId)                    
		
		then:             
		assert favouriteId == 123456789
		
	}

	def "get selection id with OK error code"() {

		def marketId = 1;
		def selectionName = "Swansea";
		
		def getMarketResp = new GetMarketResp();
		getMarketResp.setErrorCode(GetMarketErrorEnum.OK)
		getMarketResp.setMarket(market)
		
		when(mockExchangeService.getMarket(
			((GetMarketReq)anyObject()))).thenReturn(getMarketResp);
				
		given:
		def marketsService = new MarketsService(mockExchangeService, mockExchangeHeader)
		
		when:
		def selectionId = marketsService.getSelectionId(marketId, selectionName)
		
		then:
		assert selectionId == 123456789
				
	}
	
	def "get selection id with OK error code but with unknown selection name"() {
		
		def marketId = 1;
		def selectionName = "Chelsea";
		
		def getMarketResp = new GetMarketResp();
		getMarketResp.setErrorCode(GetMarketErrorEnum.OK)
		getMarketResp.setMarket(market)
		
		when(mockExchangeService.getMarket(
			((GetMarketReq)anyObject()))).thenReturn(getMarketResp);
				
		given:
		def marketsService = new MarketsService(mockExchangeService, mockExchangeHeader)
		
		when:
		def selectionId = marketsService.getSelectionId(marketId, selectionName)
		
		then:
		assert selectionId == null
				
	}
	
	def "get selection id with INVALID_MARKET error code"() {
		
		def marketId = 1;
		def selectionName = "Swansea";
		
		def getMarketResp = new GetMarketResp();
		getMarketResp.setErrorCode(GetMarketErrorEnum.INVALID_MARKET)
		getMarketResp.setMarket(market)
		
		when(mockExchangeService.getMarket(
			((GetMarketReq)anyObject()))).thenReturn(getMarketResp);
				
		given:
		def marketsService = new MarketsService(mockExchangeService, mockExchangeHeader)
		
		when:
		def selectionId = marketsService.getSelectionId(marketId, selectionName)
		
		then:
		assert selectionId == null
						
	}
	
}

