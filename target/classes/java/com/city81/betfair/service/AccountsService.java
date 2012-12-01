package com.city81.betfair.service;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsReq;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsResp;
import com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService;

/**
 * This class provides services for retrieving account balances and accoutn
 * statements.
 * 
 * @author geraint.jones
 * 
 */
public class AccountsService {

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
	public AccountsService(BFExchangeService bfExchangeService,
			APIRequestHeader exchangeHeader) {
		this.bfExchangeService = bfExchangeService;
		this.exchangeHeader = exchangeHeader;
	}

	public GetAccountFundsResp getAccountBalances() {

		GetAccountFundsReq getAccountFundsReq = new GetAccountFundsReq();
		getAccountFundsReq.setHeader(exchangeHeader);

		// get the account funds
		return bfExchangeService.getAccountFunds(getAccountFundsReq);

	}


}
