package com.city81.betfair.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.AccountStatementIncludeEnum;
import com.betfair.publicapi.types.exchange.v5.AccountStatementItem;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsReq;
import com.betfair.publicapi.types.exchange.v5.GetAccountFundsResp;
import com.betfair.publicapi.types.exchange.v5.GetAccountStatementReq;
import com.betfair.publicapi.types.exchange.v5.GetAccountStatementResp;
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

	public void getAccountBalances() {

		GetAccountFundsReq getAccountFundsReq = new GetAccountFundsReq();
		getAccountFundsReq.setHeader(exchangeHeader);

		// get the account funds
		GetAccountFundsResp getAccountFundsResp = bfExchangeService
				.getAccountFunds(getAccountFundsReq);
		System.out.println("Account Balance   = "
				+ getAccountFundsResp.getBalance());
		System.out.println("Available Balance = "
				+ getAccountFundsResp.getAvailBalance());

	}


}
