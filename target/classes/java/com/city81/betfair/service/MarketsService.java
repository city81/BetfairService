package com.city81.betfair.service;

import java.util.Date;
import java.util.List;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.ArrayOfRunnerPrices;
import com.betfair.publicapi.types.exchange.v5.Bet;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketResp;
import com.betfair.publicapi.types.exchange.v5.MarketPrices;
import com.betfair.publicapi.types.exchange.v5.Price;
import com.betfair.publicapi.types.exchange.v5.Runner;
import com.betfair.publicapi.types.exchange.v5.RunnerPrices;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.city81.betfair.utilities.PriceOperator;

/**
 * This class provides services for retrieving events.
 * 
 * @author geraint.jones
 * 
 */
public class MarketsService {

	private BFExchangeService bfExchangeService;
	private APIRequestHeader exchangeHeader;

	private PlaceBetsService placeBetsService;
	private MonitorBetsService monitorBetsService;

	/**
	 * Constructor taking as arguments the BFExchangeService used to execute the
	 * bets, and the APIRequestHeader which contains the session ie login
	 * credentials
	 * 
	 * @param bfExchangeService
	 *            betfair service api
	 * @param exchangeHeader
	 *            request header containing the token
	 */
	public MarketsService(BFExchangeService bfExchangeService,
			APIRequestHeader exchangeHeader) {
		this.bfExchangeService = bfExchangeService;
		this.exchangeHeader = exchangeHeader;

		this.placeBetsService = new PlaceBetsService(bfExchangeService,
				exchangeHeader);
		this.monitorBetsService = new MonitorBetsService(bfExchangeService,
				exchangeHeader);
	}

	/**
	 * 
	 * @param marketId
	 * @return
	 */
	public Integer getFavourite(int marketId) {

		GetMarketPricesReq getMarketPricesReq = new GetMarketPricesReq();
		getMarketPricesReq.setHeader(exchangeHeader);
		getMarketPricesReq.setMarketId(marketId);

		// get the market prices
		GetMarketPricesResp getMarketPricesResp = bfExchangeService
				.getMarketPrices(getMarketPricesReq);

		// loop through all the prices adding to arrays
		MarketPrices marketPrices = getMarketPricesResp.getMarketPrices();

		ArrayOfRunnerPrices arrayOfRunnerPrices = marketPrices
				.getRunnerPrices();

		RunnerPrices runnerPrices = null;

		double bestBackPrice = 1000.0;

		Integer favSelectionId = null;

		Price price = null;

		// find fav
		if ((arrayOfRunnerPrices.getRunnerPrices() != null)
				&& (arrayOfRunnerPrices.getRunnerPrices().size() > 0)) {

			for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices().size(); i++) {

				runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);
				price = runnerPrices.getBestPricesToBack().getPrice().get(0);

				if ((price != null) && (price.getPrice() < bestBackPrice)) {
					bestBackPrice = price.getPrice();
					favSelectionId = runnerPrices.getSelectionId();
				}
			}
		}
		return favSelectionId;
	}

	/**
	 * 
	 * @param marketId
	 * @param selectionName
	 * @return
	 */
	public Date getMarketStartTime(int marketId) {

		Date startTime = null;
		
		// identify selection but due to throttling wait for 12 secs before calling
		try {
			Thread.sleep(24000);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		GetMarketReq getMarketReq = new GetMarketReq();
		getMarketReq.setHeader(exchangeHeader);
		getMarketReq.setMarketId(marketId);

		// get the market
		GetMarketResp getMarketResp = bfExchangeService.getMarket(getMarketReq);

		if (getMarketResp.getErrorCode().equals(GetMarketErrorEnum.OK)) {
			startTime = getMarketResp.getMarket().getMarketTime().toGregorianCalendar().getTime();
		} else {
			System.out.println("Marketsservice - getMarketStartTime - " + getMarketResp.getHeader().getErrorCode());
		}
		
		return startTime;
	}	
	
	
	/**
	 * 
	 * @param marketId
	 * @param selectionName
	 * @return
	 */
	public Integer getSelectionId(int marketId, String selectionName) {

		
		// identify selection but due to throttling wait for 12 secs before calling
		try {
			Thread.sleep(24000);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		Integer selectionId = null;

		GetMarketReq getMarketReq = new GetMarketReq();
		getMarketReq.setHeader(exchangeHeader);
		getMarketReq.setMarketId(marketId);

		// get the market
		GetMarketResp getMarketResp = bfExchangeService.getMarket(getMarketReq);

		if (!(getMarketResp.getErrorCode().equals(GetMarketErrorEnum.OK))) {
			System.out.println(getMarketResp.getErrorCode());
			System.out.println(getMarketResp.getHeader().getErrorCode());
		}

		List<Runner> runners = getMarketResp.getMarket().getRunners()
				.getRunner();

		// loop through the runners to find the match
		int i = 0;
		while ((selectionId == null) && (i < runners.size())) {
			if (runners.get(i).getName().equals(selectionName)) {
				selectionId = runners.get(i).getSelectionId();
				break;
			}
			i++;
		}

		return selectionId;
	}

	/**
	 * 
	 * @param markets
	 * @return
	 */
	public void updateUnmatchedBets(Integer marketId) {

		// identify selection but due to throttling wait for 12 secs before calling
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		ArrayOfBet betArray = null;
		List<Bet> bets = null;

		if ((betArray = monitorBetsService.getCurrentBets(marketId,
				BetStatusEnum.U)) != null) {

			bets = betArray.getBet();

			// loop through the runners to find the market
			for (Bet bet : bets) {

				double newPrice;

				if (bet.getBetType().equals(BetTypeEnum.B)) {
					newPrice = PriceOperator.decrementPrice(bet.getPrice());
				} else {
					newPrice = PriceOperator.incrementPrice(bet.getPrice());
				}

				this.placeBetsService.updateBet(bet.getBetId(), bet.getPrice(),
						newPrice, bet.getRemainingSize(),
						bet.getRemainingSize(), bet.getBetPersistenceType(),
						bet.getBetPersistenceType());
			}
		}
	}

	/**
	 * 
	 * @param market
	 * @param selectionId
	 */
	public void updateUnmatchedBets(Integer market, Integer selectionId) {

		// identify selection but due to throttling wait for 12 secs before calling
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// do nothing
		}
		
		ArrayOfBet betArray = null;
		List<Bet> bets = null;

		if ((betArray = monitorBetsService.getCurrentBets(market, selectionId,
				BetStatusEnum.U)) != null) {

			bets = betArray.getBet();

			for (Bet bet : bets) {

				double newPrice;

				if (bet.getBetType().equals(BetTypeEnum.B)) {
					newPrice = PriceOperator.decrementPrice(bet.getPrice());
				} else {
					newPrice = PriceOperator.incrementPrice(bet.getPrice());
				}

				this.placeBetsService.updateBet(bet.getBetId(), bet.getPrice(),
						newPrice, bet.getRemainingSize(),
						bet.getRemainingSize(), bet.getBetPersistenceType(),
						bet.getBetPersistenceType());
			}

		}

	}

}
