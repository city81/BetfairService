package com.city81.betfair.service;

import java.util.Date;
import java.util.List;

import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.ArrayOfMUBet;
import com.betfair.publicapi.types.exchange.v5.BetPersistenceTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.global.v3.ArrayOfMarketSummary;
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
 * NOTE: methods are synchronised to ensure they are not called in such a way as to 
 * exceed the throttling thresholds.
 * 
 * @author geraint.jones
 * 
 */
public class BetfairService {

	private BFExchangeService bfExchangeService;
	private BFGlobalService bfGlobalService;
	private com.betfair.publicapi.types.exchange.v5.APIRequestHeader exchangeHeader;
	private com.betfair.publicapi.types.global.v3.APIRequestHeader globalHeader;
	private EventsService eventsService;
	private MarketsService marketsService;
	private AccountsService accountsService;
	private BetsService betsService;

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
		this.marketsService = new MarketsService(bfExchangeService,
				exchangeHeader);
		this.accountsService = new AccountsService(bfExchangeService,
				exchangeHeader);
		this.betsService = new BetsService(bfExchangeService,
				exchangeHeader);
	}

	/**
	 * Obtain the id of the favourite for a specific market
	 * 
	 * @param marketId market identifier
	 * @return Integer the identifier of the shortest priced selection
	 */
	public synchronized Integer getFavourite(int marketId) {	
		return this.marketsService.getFavourite(marketId);				
	}	
	
	/**
	 * Obtain the id of a suitable runner for scalping 
	 * 
	 * @param marketId market identifier
	 * @return Integer the identifier of the suitable scalping runner
	 */
	public synchronized Integer getScalpingRunner(int marketId) {	
		return this.marketsService.getScalpingRunner(marketId);				
	}	
	
	/**
	 * Obtain the ids of runner between two odds
	 * 
	 * @param marketId market identifier
	 * @param lowOdds low odds boundary
	 * @param highOdds high odds boundary
	 * @return List<Integer> the identifiers of the suitable runners
	 */
	public synchronized List<Integer> getRunnersInPriceRange(int marketId, double lowOdds, double highOdds) {	
		return this.marketsService.getRunnersInPriceRange(marketId, lowOdds, highOdds);				
	}	
	
	/**
	 * Update unmatched bets for a specific market and selection.
	 * 
	 * @param marketId market identifier
	 * @param selectionId selection identifier. Can be null.
	 */
	public synchronized void updateUnmatchedBets(Integer marketId, Integer selectionId) {	
		this.betsService.updateUnmatchedBets(marketId, selectionId);			
	}		
		
	/**
	 * Obtain current bets for a specific bet status, market and selection.
	 * 
	 * @param betStatus status of bet eg U Unmatched
	 * @param marketId market identifier. Can be null.
	 * @param selectionId selection identifier. Can be null.
	 * @return ArrayOfBet an array of current bets. Can be null if no bets found.
	 */
	public synchronized ArrayOfBet getCurrentBets(BetStatusEnum betStatus, 
			Integer marketId, Integer selectionId) {
		return this.betsService.getCurrentBets(betStatus, marketId, selectionId);
	}	
	
	/**
	 * Obtain the account balance for the logged in account.
	 *  
	 * @return double account balance
	 */
	public synchronized double getAccountBalance() {	
		return this.accountsService.getAccountBalances().getBalance();	
	}	
	
	/**
	 * Obtain the available to bet account balance for the logged in account.
	 *  
	 * @return double available to bet account balance
	 */
	public synchronized double getAvailableAccountBalance() {	
		return this.accountsService.getAccountBalances().getAvailBalance();	
	}	


	/**
	 * Obtain the identifier of a selection for a given market and a selection 
	 * name
	 *  
	 * @param marketId market identifier.
	 * @param selectionName name of selection eg Man Utd, Draw, 
	 * 		Swansea/Fulham, Camelot, Astrology 
	 * @return Integer selection identifier.
	 */
	public synchronized Integer getSelectionId(int marketId, String selectionName) {
		return this.marketsService.getSelectionId(marketId, selectionName);
	}

	/**
  	 * Obtain the selection names for a given market
  	 * 
  	 * @param marketId market identifier.
  	 * @return List<String> list of selection names
  	 */
	public synchronized List<String> getSelectionNames(int marketId) {
		return this.marketsService.getSelectionNames(marketId);				
	}
	
	/**
	 * Obtain the start time of a particular market.
	 *  
	 * @param marketId market identifier.
	 * @return Date start time of market
	 */
	public synchronized Date getMarketStartTime(int marketId) {
		return this.marketsService.getMarketStartTime(marketId);
	}
	
	/**
	 * Obtain the identifier of a market for a given event identifer, event name and 
	 * market name.
	 *  
	 * @param eventId event identifier eg 2022802 - Premier League
	 * @param eventName event name eg Man Utd v Man City
	 * @param marketName market name eg Match Odds, Half Time
	 * @return Integer market identifier.
	 */
	public synchronized Integer getMarketId(int eventId, String eventName, String marketName) {
		return this.eventsService.getMarketId(eventId, eventName, marketName);
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
	public synchronized void placeBet(BetTypeEnum betType, int marketId, int selectionId, 
			double price, double stake, BetPersistenceTypeEnum betPersistenceType) {
		this.betsService.placeBet(betType, marketId, selectionId,
				price, stake, betPersistenceType);			
	}
	
	/**
	 * Cencels bets for a particular market and selection.
	 * 
	 * @param marketId market identifier.
	 * @param selectionId selection identifier.
	 */
	public synchronized void cancelBets(int marketId, int selectionId) {
		this.betsService.cancelBets(marketId, selectionId);			
	}
	
	/**
	 * Obtain the collection of Market Summary objects for a given event parent id.
	 *  
	 * @param parentEventId parent event identifier eg 13 - Racing Markets
	 * @return ArrayOfMarketSummary collection of Market Summary objects
	 */
	public synchronized ArrayOfMarketSummary getMarketSummaries(int parentEventId) {
		return this.eventsService.getMarketSummaries(parentEventId);
	}
	
	/**
	 * Obtain the compressed market prices for a particular market.
	 *  
	 * @param marketId market identifier.
	 * @return String compressed market prices
	 */
	public synchronized String getMarketPricesCompressed(int marketId) {
		return this.marketsService.getMarketPricesCompressed(marketId);
	}
	
	/**
	 * Obtain the profit (or loss) on a particular selection.
	 * 
	 * @param marketId market identifier.
	 * @param selectionId selection identifier.
	 */
	public synchronized double getSelectionProfitLoss(int marketId, int selectionId) {
		return this.betsService.getSelectionProfitLoss(marketId, selectionId);			
	}
	
}
