package com.city81.betfair.service;

import java.text.DecimalFormat;
import java.util.List;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.ArrayOfCancelBets;
import com.betfair.publicapi.types.exchange.v5.ArrayOfPlaceBets;
import com.betfair.publicapi.types.exchange.v5.ArrayOfUpdateBets;
import com.betfair.publicapi.types.exchange.v5.Bet;
import com.betfair.publicapi.types.exchange.v5.BetCategoryTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetPersistenceTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetsOrderByEnum;
import com.betfair.publicapi.types.exchange.v5.CancelBets;
import com.betfair.publicapi.types.exchange.v5.CancelBetsReq;
import com.betfair.publicapi.types.exchange.v5.GetCurrentBetsReq;
import com.betfair.publicapi.types.exchange.v5.GetCurrentBetsResp;
import com.betfair.publicapi.types.exchange.v5.PlaceBets;
import com.betfair.publicapi.types.exchange.v5.PlaceBetsReq;
import com.betfair.publicapi.types.exchange.v5.PlaceBetsResp;
import com.betfair.publicapi.types.exchange.v5.UpdateBets;
import com.betfair.publicapi.types.exchange.v5.UpdateBetsReq;
import com.betfair.publicapi.types.exchange.v5.UpdateBetsResp;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.city81.betfair.utilities.PriceOperator;

/**
 * This class provides services for placing, updating and cancelling bets, and 
 * for obtaining information on current bets.
 * 
 * @author geraint.jones
 * 
 */
public class BetsService {

	private BFExchangeService bfExchangeService;
	private APIRequestHeader exchangeHeader;

	private DecimalFormat roundToTwoDecPlaces = new DecimalFormat("#.##");
	private static double MINIMUM_STAKE = 2.0;
	private static double MINIMUM_PRICE = 1.01;
	private static double MAXIMUM_PRICE = 1000.0;

	/**
	 * Format the supplied double to two decimal places. 
	 * 
	 * @param d the double to be formatted
	 * @return String string representation of the formatted double
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
	public BetsService(BFExchangeService bfExchangeService,
			APIRequestHeader exchangeHeader) {
		this.bfExchangeService = bfExchangeService;
		this.exchangeHeader = exchangeHeader;
	}
	
	/**
	 * Place a bet.
	 * 
	 * @param betType type of bet eg B - Back
	 * @param marketId market identifier.
	 * @param selectionId selection identifier.
	 * @param price bet odds
	 * @param stake bet stake
	 * @param betPersistenceType type of persistence eg IP - In Play
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
			place(betType, marketId, selectionId,
					price, stake, betPersistenceType);
		}
		
	}
	
	/**
	 * Update an existing unmatched bet
	 * 
	 * @param betId identifier of the bet
	 * @param oldPrice old bet odds
	 * @param newPrice new bet odds. Can be same as old bet odds.
	 * @param oldStake old bet stake
	 * @param newStake new bet stake. Can be same as old bet stake.
	 * @param oldBetPersistenceType old bet persistence type
	 * @param newBetPersistenceType new bet persistence type. Can be same as old bet stake.
	 */
	public void updateBet(long betId, double oldPrice, double newPrice,
			double oldStake, double newStake,
			BetPersistenceTypeEnum oldBetPersistenceType,
			BetPersistenceTypeEnum newBetPersistenceType) {	
		this.update(betId, oldPrice, newPrice, oldStake, newStake, 
				oldBetPersistenceType, newBetPersistenceType);
	}	

	/**
	 * Update unmatched bets for a specific market and selection.
	 * 
	 * @param marketId market identifier
	 * @param selectionId selection identifier. Can be null.
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

		if ((betArray = getCurrentBets(BetStatusEnum.U,	market, selectionId)) != null) {

			bets = betArray.getBet();

			for (Bet bet : bets) {

				double newPrice;

				if (bet.getBetType().equals(BetTypeEnum.B)) {
					newPrice = PriceOperator.decrementPrice(bet.getPrice());
				} else {
					newPrice = PriceOperator.incrementPrice(bet.getPrice());
				}

				updateBet(bet.getBetId(), bet.getPrice(),
						newPrice, bet.getRemainingSize(),
						bet.getRemainingSize(), bet.getBetPersistenceType(),
						bet.getBetPersistenceType());
			}

		}

	}
	
	/**
	 * Obtain current bets for a specific bet status, market and selection.
	 * 
	 * @param betStatus status of bet eg U Unmatched
	 * @param marketId market identifier. Can be null.
	 * @param selectionId selection identifier. Can be null.
	 * @return ArrayOfBet an array of current bets. Can be null if no bets found.
	 */
	public ArrayOfBet getCurrentBets(BetStatusEnum betStatus, Integer marketId, 
			Integer selectionId) {

        GetCurrentBetsReq getMatchedBetsReq = new GetCurrentBetsReq();
        getMatchedBetsReq.setHeader(exchangeHeader);
        getMatchedBetsReq.setBetStatus(betStatus);
        getMatchedBetsReq.setDetailed(true);
        if (marketId != null) {
        	getMatchedBetsReq.setMarketId(marketId);
        }
        getMatchedBetsReq.setOrderBy(BetsOrderByEnum.NONE);
        getMatchedBetsReq.setRecordCount(1000);
        getMatchedBetsReq.setStartRecord(0);
        GetCurrentBetsResp getMatchedBetsResp =
                bfExchangeService.getCurrentBets(getMatchedBetsReq);
        
        ArrayOfBet bets = getMatchedBetsResp.getBets();
        ArrayOfBet returnBets = new ArrayOfBet();
        
        if ((bets != null) && (bets.getBet().size() > 0)) {
        	
        	for (Bet bet : bets.getBet()) {
        		
        		// if selection id supplied only return those bets
        		// with the same selection id
        		if (selectionId != null) {
	        		if (bet.getSelectionId() == selectionId) {
	        			returnBets.getBet().add(bet);
	        		}
        		} else {
        			returnBets.getBet().add(bet);
        		}
        	}
        }
        
        	return returnBets;
	}	

	/**
	 * Obtain current bets for a specific market and selection, and cancel them.
	 * 
	 * @param marketId market identifier. Can be null.
	 * @param selectionId selection identifier. Can be null.
	 */
	public void cancelBets(int marketId, int selectionId) {

        	GetCurrentBetsReq getMatchedBetsReq = new GetCurrentBetsReq();
        	getMatchedBetsReq.setHeader(exchangeHeader);
        	getMatchedBetsReq.setBetStatus(BetStatusEnum.U);
        	getMatchedBetsReq.setDetailed(true);
        	getMatchedBetsReq.setMarketId(marketId);
        	getMatchedBetsReq.setOrderBy(BetsOrderByEnum.NONE);
        	getMatchedBetsReq.setRecordCount(1000);
        	getMatchedBetsReq.setStartRecord(0);

        	GetCurrentBetsResp getMatchedBetsResp =
                	bfExchangeService.getCurrentBets(getMatchedBetsReq);
        
        	ArrayOfBet bets = getMatchedBetsResp.getBets();
        	ArrayOfBet returnBets = new ArrayOfBet();
        
        	if ((bets != null) && (bets.getBet().size() > 0)) {
        	
        		for (Bet bet : bets.getBet()) {
        		
	        		if (bet.getSelectionId() == selectionId) {
	        			this.cancel(bet.getBetId());
	        		}
        		}
        	}
        
	}	


	/**
	 * As the Betfair API does not allow bets less then 2 pounds, a workaround
	 * is to place a 2 pound bet at a max or min price depending on whether it
	 * is a Back or Lay bet, increase the stake which results in two bets (one
	 * at 2 pounds, the other at the small stake) and then update the small
	 * stake bet with the required price and cancel the original 2 pound bet.
	 * 
	 * @param marketId market identifier
	 * @param selectionId selection identifier
	 * @param price odds of bet required
	 * @param stake stake of bet required
	 * @param betPersistenceType type of bet eg In Play
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
	 * Place a bet with a stake less than two pounds. 
	 * 
	 * @param marketId market identifier
	 * @param selectionId selection identifier
	 * @param price odds of bet required
	 * @param stake stake of bet required
	 * @param betPersistenceType type of bet eg In Play
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
	 * Place a bet
	 * 
	 * @param betType typer of bet
	 * @param marketId market identifier
	 * @param selectionId selection identifier
	 * @param price odds of bet required
	 * @param stake stake of bet required
	 * @param betPersistenceType type of bet eg In Play
	 * @return long the identifier of the bet placed
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
	 * Update an existing bet.
	 * 
	 * @param betId identifier of the bet
	 * @param oldPrice old bet odds
	 * @param newPrice new bet odds. Can be same as old bet odds.
	 * @param oldStake old bet stake
	 * @param newStake new bet stake. Can be same as old bet stake.
	 * @param oldBetPersistenceType old bet persistence type
	 * @param newBetPersistenceType new bet persistence type. Can be same as old bet stake.
	 * @return long the identifier of the bet updated
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
	 * Cancel a bet
	 * 
	 * @return betId the identifier of the bet to be cancelled
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
