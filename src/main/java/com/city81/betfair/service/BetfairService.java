package com.city81.betfair.service;

import java.util.ArrayList;
import java.util.List;

import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.Bet;
import com.betfair.publicapi.types.exchange.v5.BetPersistenceTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.global.v3.LoginReq;
import com.betfair.publicapi.types.global.v3.LoginResp;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService_Service;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService_Service;
import com.city81.betfair.utilities.PriceFormat;
import com.city81.betfair.utilities.PriceFormat.DIRECTION;
import com.city81.betfair.utilities.PriceOperator;

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

		this.eventsService = new EventsService(bfGlobalService, globalHeader);
		this.placeBetsService = new PlaceBetsService(bfExchangeService,
				exchangeHeader);
		this.marketsService = new MarketsService(bfExchangeService,
				exchangeHeader);
		this.accountsService = new AccountsService(bfExchangeService,
				exchangeHeader);
	}

	public void updateMarkets(List<Integer> markets) {

		MonitorBetsService monitorBetsService = new MonitorBetsService(
				bfExchangeService, exchangeHeader);

		ArrayOfBet betArray = null;
		List<Bet> bets = null;

		for (Integer market : markets) {
			
		while ((betArray = monitorBetsService.getCurrentBets(market, BetStatusEnum.U)) != null) {
			
			bets = betArray.getBet();

			System.out.println(bets.size() + " bets are unmatched.");			

			// loop through the runners to find the market
			int i = 0;
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
			
			// wait two mins
			try {
				System.out.println("Waiting 2 mins before checking for unmatched bets.");					
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		}
		System.out.println("No bets are unmatched.");					
	}


	
	
	public void list() {
		MonitorBetsService monitorBetsService = new MonitorBetsService(
				bfExchangeService, exchangeHeader);
		ArrayOfBet betArray = monitorBetsService.getCurrentBets(null,
				BetStatusEnum.M);

		List<Bet> bets = betArray.getBet();

		// loop through the runners to find the market
		int i = 0;
		for (Bet bet : bets) {
			System.out.println(bet.getBetId());
			System.out.println(bet.getFullMarketName());
			System.out.println(bet.getSelectionName());
			System.out.println(bet.getBetType().toString());
			System.out.println(bet.getPrice());
			System.out.println();
		}
	}

	/**
	 * 
	 * @param homeTeam
	 * @param awayTeam
	 * @param selection
	 * @param outcomeOdds
	 */
	public Integer placeFootballBet(String homeTeam, String awayTeam,
			FootballSelectionEnum selection, double outcomeOdds,
			BetSelectionEnum bet, String leagueId) {
		
		// lookup betfair version of team name
		homeTeam = TeamEnum.getBetfairName(homeTeam);
		awayTeam = TeamEnum.getBetfairName(awayTeam);

		System.out.println(homeTeam + " v " + awayTeam + " " + selection + " "
				+ outcomeOdds);

		// System.out.println(Calendar.getInstance().getTime().toString());
		// lookup league event id

		int eventId = LeagueEnum.getBetfairEventId(leagueId);

		// identify market
		Integer marketId = this.eventsService.getMarketId(eventId, homeTeam
				+ " v " + awayTeam, "Match Odds");

		// System.out.println(Calendar.getInstance().getTime().toString());

		if (marketId != null) {
			// System.out.println("market id: " + marketId);
			// set the selection name
			String selectionName = null;
			if (selection.equals(FootballSelectionEnum.HOME)) {
				selectionName = homeTeam;
			} else if (selection.equals(FootballSelectionEnum.AWAY)) {
				selectionName = awayTeam;
			} else {
				selectionName = "The Draw";
			}

			// identify selection
			Integer selectionId = this.marketsService.getSelectionId(marketId,
					selectionName);
			if (selectionId != null) {
				// System.out.println("selection id: " + selectionId);

				// place bet and round outcomeOdds
				if (bet.equals(BetSelectionEnum.BACK)) {
					outcomeOdds = PriceFormat.round(DIRECTION.UP, outcomeOdds);
					System.out.println(outcomeOdds);
					this.placeBetsService.placeSmallStakeBackBet(marketId,
							selectionId, outcomeOdds, 1.50,
							BetPersistenceTypeEnum.NONE);
				} else {
					outcomeOdds = PriceFormat
							.round(DIRECTION.DOWN, outcomeOdds);
					System.out.println(outcomeOdds);
					this.placeBetsService.placeSmallStakeLayBet(marketId,
							selectionId, outcomeOdds, 1.50,
							BetPersistenceTypeEnum.NONE);
				}
				System.out.println("placed bet");
			} else {
				System.out.println("no selection found");
			}
		} else {
			System.out.println("no market found");
		}
		System.out.println();
		return marketId;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList getRacingMarkets() {		
		return this.eventsService.getRacingMarkets();				
	}

	public Integer getFavourite(int marketId) {	
		return this.marketsService.getFavourite(marketId);				
	}	
	
	public List<Integer> getFirstThreeFavourites(int marketId) {	
		return this.marketsService.getFirstThreeFavourites(marketId);				
	}	

	public void monitorMarket(int marketId, Integer selectionId) {	
		this.marketsService.monitorMarket(marketId, selectionId);	
	}	

	public void monitorMarket(int marketId, List<Integer> selectionIds) {	
		this.marketsService.monitorMarket(marketId, selectionIds);	
	}	
	
	public void account() {	
		this.accountsService.getAccountBalances();	
	}	
	
}
