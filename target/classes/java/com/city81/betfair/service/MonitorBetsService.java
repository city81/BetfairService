package com.city81.betfair.service;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.Bet;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetsOrderByEnum;
import com.betfair.publicapi.types.exchange.v5.GetCurrentBetsReq;
import com.betfair.publicapi.types.exchange.v5.GetCurrentBetsResp;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;

/**
 * This class provides services for monitoring bets.
 * 
 * @author geraint.jones
 * 
 */
public class MonitorBetsService {

	private BFExchangeService bfExchangeService;
	private APIRequestHeader exchangeHeader;

	/**
	 * Constructor taking as arguments the BFExchangeService used to execute the
	 * bets, and the APIRequestHeader which contains the session ie login
	 * username and password
	 * 
	 * @param bfExchangeService
	 *            betfair service api
	 * @param exchangeHeader
	 *            request header containing the token
	 */
	public MonitorBetsService(BFExchangeService bfExchangeService,
			APIRequestHeader exchangeHeader) {
		this.bfExchangeService = bfExchangeService;
		this.exchangeHeader = exchangeHeader;
	}

	/**
	 * For a given market identifier and bet status, returns a list of 
	 * Bets.
	 * 
	 * @param marketId market identifier
	 * @param betStatus status of bet eg Unmatched, Matched
	 * @return an array of bets
	 */
	public ArrayOfBet getCurrentBets(Integer marketId, BetStatusEnum betStatus ) {

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
        return getMatchedBetsResp.getBets();
	}

	/**
	 * For a given market identifier and bet status, returns a list of 
	 * Bets.
	 * 
	 * @param marketId market identifier
	 * @param betStatus status of bet eg Unmatched, Matched
	 * @return an array of bets
	 */
	public ArrayOfBet getCurrentBets(Integer marketId, Integer selectionId, BetStatusEnum betStatus) {

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
        ArrayOfBet returnBets = null;
        
        if ((bets != null) && (bets.getBet().size() > 0)) {
        	for (Bet bet : bets.getBet()) {
        		if (bet.getSelectionId() == selectionId) {
        			if (returnBets == null) {
        				returnBets = new ArrayOfBet();
        			}
        			returnBets.getBet().add(bet);
        		}
        	}
        }
        
        return returnBets;
	}	
	
}
