package com.city81.betfair.service;

import java.text.DecimalFormat;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.ArrayOfCancelBets;
import com.betfair.publicapi.types.exchange.v5.ArrayOfPlaceBets;
import com.betfair.publicapi.types.exchange.v5.ArrayOfUpdateBets;
import com.betfair.publicapi.types.exchange.v5.BetCategoryTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetPersistenceTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.exchange.v5.CancelBets;
import com.betfair.publicapi.types.exchange.v5.CancelBetsReq;
import com.betfair.publicapi.types.exchange.v5.PlaceBets;
import com.betfair.publicapi.types.exchange.v5.PlaceBetsReq;
import com.betfair.publicapi.types.exchange.v5.PlaceBetsResp;
import com.betfair.publicapi.types.exchange.v5.UpdateBets;
import com.betfair.publicapi.types.exchange.v5.UpdateBetsReq;
import com.betfair.publicapi.types.exchange.v5.UpdateBetsResp;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;

/**
 * This class provides services for placing bets.
 * 
 * @author geraint.jones
 * 
 */
public class PlaceBetsService {

	private BFExchangeService bfExchangeService;
	private APIRequestHeader exchangeHeader;

	private DecimalFormat roundToTwoDecPlaces = new DecimalFormat("#.##");
	private static double MINIMUM_STAKE = 2.0;
	private static double MINIMUM_PRICE = 1.01;
	private static double MAXIMUM_PRICE = 1000.0;

	/**
	 * 
	 * @param d
	 * @return
	 */
	protected String formatStake(Double d) {
	    synchronized(roundToTwoDecPlaces) {
	        return roundToTwoDecPlaces.format(d);
	    }
	}	
	
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
	public PlaceBetsService(BFExchangeService bfExchangeService,
			APIRequestHeader exchangeHeader) {
		this.bfExchangeService = bfExchangeService;
		this.exchangeHeader = exchangeHeader;
	}
	
	/**
	 * 
	 * @param betType
	 * @param marketId
	 * @param selectionId
	 * @param price
	 * @param stake
	 * @param betPersistenceType
	 */
	public void placeBet(BetTypeEnum betType, int marketId, int selectionId,
			double price, double stake,
			BetPersistenceTypeEnum betPersistenceType) {

		// check the stake, if less than 2 pounds then use small stake methods
		if (stake < MINIMUM_STAKE) {
			if (betType.equals(BetTypeEnum.B)) {
				placeSmallStakeBackBet(marketId, selectionId,
					price, stake, betPersistenceType);
			} else {
				placeSmallStakeLayBet(marketId, selectionId,
					price, stake, betPersistenceType);
			}
		} else {
			placeBet(betType, marketId, selectionId,
					price, stake, betPersistenceType);
		}
		
	}
	
	/**
	 * 
	 * @param betId
	 * @param oldPrice
	 * @param newPrice
	 * @param oldStake
	 * @param newStake
	 * @param oldBetPersistenceType
	 * @param newBetPersistenceType
	 */
	public void updateBet(long betId, double oldPrice, double newPrice,
			double oldStake, double newStake,
			BetPersistenceTypeEnum oldBetPersistenceType,
			BetPersistenceTypeEnum newBetPersistenceType) {	
		this.update(betId, oldPrice, newPrice, oldStake, newStake, 
				oldBetPersistenceType, newBetPersistenceType);
	}	

	/**
	 * As the Betfair API does not allow bets less then 2 pounds, a workaround
	 * is to place a 2 pound bet at a max or min price depending on whether it
	 * is a Back or Lay bet, increase the stake which results in two bets (one
	 * at 2 pounds, the other at the small stake) and then update the small
	 * stake bet with the required price and cancel the original 2 pound bet.
	 * 
	 * @param marketId
	 *            market identifier
	 * @param selectionId
	 *            selection identifier
	 * @param price
	 *            price of bet required
	 * @param stake
	 *            stake of bet required
	 * @param betPersistenceType
	 *            type of bet eg In Play
	 */
	private void placeSmallStakeBackBet(int marketId, int selectionId,
			double price, double stake,
			BetPersistenceTypeEnum betPersistenceType) {

		//
		// Place a minimum stake bet on the selection at maximum odds
		//
		final long placeBetId = place(BetTypeEnum.B, marketId, selectionId,
				MAXIMUM_PRICE, MINIMUM_STAKE, betPersistenceType);

		//
		// Update the stake on the above bet by increasing it by the supplied
		// stake. This will result in two bets on the market ie the original minimum
		// stake bet and the smaller stake bet
		//
		final long updateBetId = update(placeBetId, MAXIMUM_PRICE,
				MAXIMUM_PRICE, MINIMUM_STAKE, (MINIMUM_STAKE + stake),
				betPersistenceType, betPersistenceType);

		//
		// Update the small stake bet with the supplied price
		//
		update(updateBetId, MAXIMUM_PRICE, price, stake, stake,
				betPersistenceType, betPersistenceType);

		//
		// Cancel the original minimum stake bet
		//
		cancel(placeBetId);

	}

	/**
	 * 
	 * @param marketId
	 * @param selectionId
	 * @param price
	 * @param stake
	 * @param betPersistenceType
	 */
	private void placeSmallStakeLayBet(int marketId, int selectionId,
			double price, double stake,
			BetPersistenceTypeEnum betPersistenceType) {

		//
		// Place a minimum stake bet on the selection at minimum odds
		//
		final long placeBetId = place(BetTypeEnum.L, marketId, selectionId,
				 MINIMUM_PRICE, MINIMUM_STAKE, betPersistenceType);		

		//
		// Update the stake on the above bet by increasing it by the supplied
		// stake. This will result in two bets on the market ie the original minimum
		// stake bet and the smaller stake bet
		//
		final long updateBetId = update(placeBetId, MINIMUM_PRICE,
				MINIMUM_PRICE, MINIMUM_STAKE, (MINIMUM_STAKE + stake),
				betPersistenceType, betPersistenceType);		

		//
		// Update the small stake bet with the supplied price
		//
		update(updateBetId, MINIMUM_PRICE, price, stake, stake,
				betPersistenceType, betPersistenceType);		

		//
		// Cancel the original minimum stake bet
		//
		cancel(placeBetId);

	}
	
	/**
	 * 
	 * @param betType
	 * @param marketId
	 * @param selectionId
	 * @param price
	 * @param stake
	 * @param betPersistenceType
	 * @return
	 */
	private long place(BetTypeEnum betType, int marketId, int selectionId,
			double price, double stake,
			BetPersistenceTypeEnum betPersistenceType) {
		
		final ArrayOfPlaceBets arrayOfPlaceBets = new ArrayOfPlaceBets();
		final PlaceBets placeBets = new PlaceBets();
		placeBets.setAsianLineId(0);
		placeBets.setBetType(betType);
		placeBets.setMarketId(marketId);
		placeBets.setPrice(price);
		placeBets.setSelectionId(selectionId);
		placeBets.setSize(stake);
		placeBets.setBetCategoryType(BetCategoryTypeEnum.E);
		placeBets.setBspLiability(10.0);
		placeBets.setBetPersistenceType(betPersistenceType);
		arrayOfPlaceBets.getPlaceBets().add(placeBets);
		final PlaceBetsReq placeBetsReq = new PlaceBetsReq();
		placeBetsReq.setHeader(exchangeHeader);
		placeBetsReq.setBets(arrayOfPlaceBets);
		final PlaceBetsResp placeBetsResp = bfExchangeService
				.placeBets(placeBetsReq);
		return placeBetsResp.getBetResults().getPlaceBetsResult().get(0).getBetId();
	
	}

	/**
	 * 
	 * @param betId
	 * @param oldPrice
	 * @param newPrice
	 * @param oldStake
	 * @param newStake
	 * @param oldBetPersistenceType
	 * @param newBetPersistenceType
	 * @return
	 */
	private long update(long betId, double oldPrice, double newPrice,
			double oldStake, double newStake,
			BetPersistenceTypeEnum oldBetPersistenceType,
			BetPersistenceTypeEnum newBetPersistenceType) {

		final ArrayOfUpdateBets arrayOfUpdateBets = new ArrayOfUpdateBets();
		final UpdateBets updateBets = new UpdateBets();
		updateBets.setBetId(betId);
		updateBets.setNewSize((Double.parseDouble(formatStake(newStake))));
		updateBets.setOldSize(oldStake);
		updateBets.setOldPrice(oldPrice);
		updateBets.setNewPrice(newPrice);
		updateBets.setOldBetPersistenceType(oldBetPersistenceType);
		updateBets.setNewBetPersistenceType(newBetPersistenceType);
		arrayOfUpdateBets.getUpdateBets().add(updateBets);
		final UpdateBetsReq updateBetsReq = new UpdateBetsReq();
		updateBetsReq.setHeader(exchangeHeader);
		updateBetsReq.setBets(arrayOfUpdateBets);
		final UpdateBetsResp updateBetsResp = bfExchangeService
				.updateBets(updateBetsReq);
		return updateBetsResp.getBetResults().getUpdateBetsResult().get(0).getNewBetId();
	}

	/**
	 * 
	 * @param betId
	 */
	private void cancel(long betId) {

		final ArrayOfCancelBets arrayOfCancelBetsSmall = new ArrayOfCancelBets();
		final CancelBets cancelBets = new CancelBets();
		cancelBets.setBetId(betId);
		arrayOfCancelBetsSmall.getCancelBets().add(cancelBets);
		final CancelBetsReq cancelBetsReq = new CancelBetsReq();
		cancelBetsReq.setHeader(exchangeHeader);
		cancelBetsReq.setBets(arrayOfCancelBetsSmall);
		bfExchangeService.cancelBets(cancelBetsReq);
	}


}
