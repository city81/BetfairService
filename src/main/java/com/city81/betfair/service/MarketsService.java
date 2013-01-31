package com.city81.betfair.service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.betfair.publicapi.types.exchange.v5.APIErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetCurrentBetsResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedResp;
import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.ArrayOfRunnerPrices;
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

/**
 * This class provides services for retrieving events.
 * 
 * @author geraint.jones
 * 
 */
public class MarketsService {

	private BFExchangeService bfExchangeService;
	private APIRequestHeader exchangeHeader;

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
	}

	/**
	 * Obtain the id of the favourite for a specific market
	 * 
	 * @param marketId
	 *            market identifier
	 * @return Integer the identifier of the shortest priced selection
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
	 * Obtain the id of  a suitable runner for scalping
	 *
	 * @param marketId
	 *            market identifier
	 * @return Integer the identifier of the suitable runner for scalping
	 */
	public Integer getScalpingRunner(int marketId) {

		Integer shortestOddsAgainstRunnerId = null;
		
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
		double secondBestBackPrice = 1000.0;

		Integer favSelectionId = null;
		Integer secondFavSelectionId = null;

		Price price = null;

		// find second fav
		if ((arrayOfRunnerPrices.getRunnerPrices() != null)
				&& (arrayOfRunnerPrices.getRunnerPrices().size() > 0)) {

			for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices().size(); i++) {

				runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);
				price = runnerPrices.getBestPricesToBack().getPrice().get(0);

				if ((price != null) && (price.getPrice() < bestBackPrice)) {
					secondBestBackPrice = bestBackPrice;
					secondFavSelectionId = favSelectionId; 
					bestBackPrice = price.getPrice();
					favSelectionId = runnerPrices.getSelectionId();
				} else if ((price != null) && (price.getPrice() < secondBestBackPrice)) {
					secondBestBackPrice = price.getPrice();
					secondFavSelectionId = runnerPrices.getSelectionId();
				}
			}
		}

		if (bestBackPrice < 2.0) {
			if (secondBestBackPrice < 8.0) {	
				shortestOddsAgainstRunnerId = secondFavSelectionId;			
			}
		} else {
			if (bestBackPrice < 8.0) {
				shortestOddsAgainstRunnerId = favSelectionId;
			}
		}

		return shortestOddsAgainstRunnerId;
	}

	/**
	 * Obtain the ids of runners between two odds
	 *
	 * @param marketId market identifier
	 * @param lowOdds low odds boundary
	 * @param highOdds high odds boundary
	 * @return List<Integer> the identifiers of the suitable selections
	 */
	public List<Integer> getRunnersInPriceRange(int marketId, double lowOdds, double highOdds) {

		List<Integer> runnersList = new ArrayList<Integer>();
		
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

		Price price = null;

		// find second fav
		if ((arrayOfRunnerPrices.getRunnerPrices() != null)
				&& (arrayOfRunnerPrices.getRunnerPrices().size() > 0)) {

			for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices().size(); i++) {

				runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);
				price = runnerPrices.getBestPricesToBack().getPrice().get(0);

				if (price != null) {

					if ((price.getPrice() > lowOdds) && 
						(price.getPrice() < highOdds)) {
						runnersList.add(runnerPrices.getSelectionId());
					}
				}
			}
		}

		return runnersList;
	}

	/**
	 * Obtain the start time of of a particular market.
	 * 
	 * @param marketId
	 *            market identifier.
	 * @return Date start time of market
	 */
	public Date getMarketStartTime(int marketId) {

		Date startTime = null;

		// identify selection but due to throttling wait for 12 secs before
		// calling
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
			startTime = getMarketResp.getMarket().getMarketTime()
					.toGregorianCalendar().getTime();
		}

		return startTime;
	}

	/**
	 * Obtain the identifier of a selection for a given market and a selection
	 * name
	 * 
	 * @param marketId
	 *            market identifier.
	 * @param selectionName
	 *            name of selection eg Man Utd, Draw, Swansea/Fulham, Camelot,
	 *            Astrology return Integer selection identifier.
	 */
	public Integer getSelectionId(int marketId, String selectionName) {

		// identify selection but due to throttling wait for 12 secs before
		// calling
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// do nothing
		}

		Integer selectionId = null;

		GetMarketReq getMarketReq = new GetMarketReq();
		getMarketReq.setHeader(exchangeHeader);
		getMarketReq.setMarketId(marketId);

		// get the market
		GetMarketResp getMarketResp = bfExchangeService.getMarket(getMarketReq);

		if (getMarketResp.getErrorCode().equals(GetMarketErrorEnum.OK)) {

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
		}
		
		return selectionId;
	}

        /**
	 * Obtain the compressed market prices for a particular market.
	 *
	 * @param marketId market identifier.
	 * @return String compressed market prices
	 */
        public synchronized String getMarketPricesCompressed(int marketId) {

		GetMarketPricesCompressedReq getMarketPricesCompressedReq 
			= new GetMarketPricesCompressedReq();
		getMarketPricesCompressedReq.setHeader(exchangeHeader);
		getMarketPricesCompressedReq.setMarketId(marketId);

		GetMarketPricesCompressedResp getMarketPricesCompressedResp = null;
		
		boolean throttledExceeded = true;
		
		while (throttledExceeded) {
			
			getMarketPricesCompressedResp = 
				bfExchangeService.getMarketPricesCompressed(getMarketPricesCompressedReq);

			if ((getMarketPricesCompressedResp.getHeader().getErrorCode() != null) &&
				(getMarketPricesCompressedResp.getHeader().getErrorCode().equals(APIErrorEnum.EXCEEDED_THROTTLE))) {
				
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
			} else {
				
				throttledExceeded = false;
				
			}
			
		}
		
		return getMarketPricesCompressedResp.getMarketPrices();
	}

}

