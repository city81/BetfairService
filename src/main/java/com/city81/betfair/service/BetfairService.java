package com.city81.betfair.service;

import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.BetPersistenceTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.global.v3.LoginReq;
import com.betfair.publicapi.types.global.v3.LoginResp;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService_Service;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService_Service;

/**
 * This class provides services for placing specific bets eg a bet on either a
 * home win, an away win or a draw for a fixture between two teams.
 * 
 * @author geraint.jones
 * 
 */
public class BetfairService {

	private BFExchangeService bfExchangeService;
	private BFGlobalService bfGlobalService;
	private com.betfair.publicapi.types.exchange.v5.APIRequestHeader exchangeHeader;
	private com.betfair.publicapi.types.global.v3.APIRequestHeader globalHeader;
	private PlaceBetsService placeBetsService;
	private EventsService eventsService;
	private MarketsService marketsService;
	private AccountsService accountsService;
	private MonitorBetsService monitorBetsService;

	/**
	 * Constructor taking as arguments the BFExchangeService used to execute the
	 * bets, and the APIRequestHeader which contains the session ie login
	 * username and password.
	 * 
	 */
	public BetfairService(String username, String password) {

		BFGlobalService_Service globalService = new BFGlobalService_Service();
		this.bfGlobalService = globalService.getBFGlobalService();

		LoginReq req = new LoginReq();
		req.setUsername(username);
		req.setPassword(password);
		req.setProductId(82);
		LoginResp loginResp = this.bfGlobalService.login(req);

		BFExchangeService_Service exchangeService = new BFExchangeService_Service();
		this.bfExchangeService = exchangeService.getBFExchangeService();

		this.exchangeHeader = new com.betfair.publicapi.types.exchange.v5.APIRequestHeader();
		this.exchangeHeader.setClientStamp(0);
		this.exchangeHeader.setSessionToken(loginResp.getHeader()
				.getSessionToken());

		this.globalHeader = new com.betfair.publicapi.types.global.v3.APIRequestHeader();
		this.globalHeader.setClientStamp(0);
		this.globalHeader.setSessionToken(loginResp.getHeader()
				.getSessionToken());

		// create services
		this.eventsService = new EventsService(bfGlobalService, globalHeader);
		this.placeBetsService = new PlaceBetsService(bfExchangeService,
				exchangeHeader);
		this.marketsService = new MarketsService(bfExchangeService,
				exchangeHeader);
		this.accountsService = new AccountsService(bfExchangeService,
				exchangeHeader);
		this.monitorBetsService = new MonitorBetsService(bfExchangeService,
				exchangeHeader);
	}

	/**
	 * 
	 * @param marketId
	 * @return
	 */
	public synchronized Integer getFavourite(int marketId) {	
		return this.marketsService.getFavourite(marketId);				
	}	
	
	/**
	 * 
	 * @param marketIds
	 * @return
	 */
	public synchronized void updateUnmatchedBets(Integer marketId) {	
		this.marketsService.updateUnmatchedBets(marketId);			
	}		
		
	/**
	 * 
	 * @param marketId
	 * @param betStatus
	 * @return
	 */
	public synchronized ArrayOfBet getCurrentBets(Integer marketId, BetStatusEnum betStatus ) {
		return this.monitorBetsService.getCurrentBets(marketId, betStatus);
	}

	/**
	 * 
	 * @return
	 */
	public synchronized double getAccountBalance() {	
		return this.accountsService.getAccountBalances().getBalance();	
	}	
	
	/**
	 * 
	 * @return
	 */
	public synchronized double getAvailableAccountBalance() {	
		return this.accountsService.getAccountBalances().getAvailBalance();	
	}	

	/**
	 * 
	 * @param marketId
	 * @param selectionName
	 * @return
	 */
	public synchronized Integer getSelectionId(int marketId, String selectionName) {
		return this.marketsService.getSelectionId(marketId, selectionName);
	}
	
	/**
	 * 
	 * @param eventId
	 * @param eventName
	 * @param marketName
	 * @return
	 */
	public synchronized Integer getMarketId(int eventId, String eventName, String marketName) {
		return this.eventsService.getMarketId(eventId, eventName, marketName);
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
	public synchronized void placeBet(BetTypeEnum betType, int marketId, int selectionId, 
			double price, double stake, BetPersistenceTypeEnum betPersistenceType) {
		this.placeBetsService.placeBet(betType, marketId, selectionId,
				price, stake, betPersistenceType);			
	}
	
}
