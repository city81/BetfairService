package com.city81.betfair.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.betfair.publicapi.types.exchange.v5.APIRequestHeader;
import com.betfair.publicapi.types.exchange.v5.ArrayOfBet;
import com.betfair.publicapi.types.exchange.v5.ArrayOfRunnerPrices;
import com.betfair.publicapi.types.exchange.v5.Bet;
import com.betfair.publicapi.types.exchange.v5.BetPersistenceTypeEnum;
import com.betfair.publicapi.types.exchange.v5.BetStatusEnum;
import com.betfair.publicapi.types.exchange.v5.BetTypeEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketErrorEnum;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesCompressedResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketPricesResp;
import com.betfair.publicapi.types.exchange.v5.GetMarketReq;
import com.betfair.publicapi.types.exchange.v5.GetMarketResp;
import com.betfair.publicapi.types.exchange.v5.MarketPrices;
import com.betfair.publicapi.types.exchange.v5.MarketStatusEnum;
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

	private static DecimalFormat stakeFormatTwoDec = new DecimalFormat("##.##");
	private static DecimalFormat stakeFormatFourDec = new DecimalFormat(
			"####.##");

	private BFExchangeService bfExchangeService;
	private APIRequestHeader exchangeHeader;

	private PlaceBetsService placeBetsService;
	private MonitorBetsService monitorBetsService;
	private AccountsService accountsService;

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
		this.accountsService = new AccountsService(bfExchangeService,
				exchangeHeader);
	}

	/**
   * 
   */
	public void monitorMarket(int marketId, Integer selectionId) {

		// printout account info
		this.accountsService.getAccountBalances();

		GetMarketPricesReq getMarketPricesReq = new GetMarketPricesReq();
		getMarketPricesReq.setHeader(exchangeHeader);
		getMarketPricesReq.setMarketId(marketId);
		GetMarketPricesResp getMarketPricesResp = null;
		ArrayOfRunnerPrices arrayOfRunnerPrices = null;
		RunnerPrices runnerPrices = null;

		// loop
		boolean marketActive = true;
		double avgPrice1 = 0.0;
		double avgPrice2 = 0.0;
		double avgPrice3 = 0.0;

		ArrayOfBet unmatchedBets = null;
		double lastPrice7 = 0.0;
		double lastPrice6 = 0.0;
		double lastPrice5 = 0.0;
		double lastPrice4 = 0.0;
		double lastPrice3 = 0.0;
		double lastPrice2 = 0.0;
		double lastPrice1 = 0.0;
		double stake = 0.0;

		List<Price> backAmountList = null;
		List<Price> layAmountList = null;

		int count = 0;

		while (marketActive) {

			// get the market prices
			getMarketPricesResp = bfExchangeService
					.getMarketPrices(getMarketPricesReq);

			// if market active and not in play then
			if (getMarketPricesResp.getMarketPrices().getMarketStatus()
					.equals(MarketStatusEnum.ACTIVE)
					&& (getMarketPricesResp.getMarketPrices().getDelay() == 0)) {

				arrayOfRunnerPrices = getMarketPricesResp.getMarketPrices()
						.getRunnerPrices();

				for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices()
						.size(); i++) {

					runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);

					// if selection id set then only print selection id's
					// details

					if (selectionId.equals(runnerPrices.getSelectionId())) {

						// get amounts
						backAmountList = runnerPrices.getBestPricesToBack()
								.getPrice();

						layAmountList = runnerPrices.getBestPricesToLay()
								.getPrice();

						// update last seven prices
						lastPrice7 = lastPrice6;
						lastPrice6 = lastPrice5;
						lastPrice5 = lastPrice4;
						lastPrice4 = lastPrice3;
						lastPrice3 = lastPrice2;
						lastPrice2 = lastPrice1;
						lastPrice1 = runnerPrices.getLastPriceMatched();

						if (lastPrice7 != 0.0) {

							// avgPrice1 = (lastPrice7 + lastPrice5 +
							// lastPrice3) / 3.0;
							// avgPrice2 = (lastPrice6 + lastPrice4 +
							// lastPrice2) / 3.0;
							// avgPrice3 = (lastPrice5 + lastPrice3 +
							// lastPrice1) / 3.0;

							// check averages
							// if ((avgPrice1 > avgPrice2)
							// && (avgPrice2 > avgPrice3)) {
							//
							// // check amounts
							// if ((backAmountList.get(0).getAmountAvailable() +
							// backAmountList
							// .get(1).getAmountAvailable()) < (layAmountList
							// .get(0).getAmountAvailable() + layAmountList
							// .get(1).getAmountAvailable())) {
							//
							// // if no unmatched bets
							// unmatchedBets = this.monitorBetsService
							// .getCurrentBets(marketId,
							// BetStatusEnum.U);
							//
							// if (unmatchedBets == null) {
							//
							// stake = calculateStake(backAmountList
							// .get(0).getPrice());
							//
							// // place back bet
							// this.placeBetsService.placeBet(
							// BetTypeEnum.B, marketId,
							// selectionId, backAmountList
							// .get(0).getPrice(),
							// stake,
							// BetPersistenceTypeEnum.IP);
							//
							// // place lay bet
							// this.placeBetsService
							// .placeBet(
							// BetTypeEnum.L,
							// marketId,
							// selectionId,
							// PriceOperator
							// .decrementPrice(backAmountList
							// .get(0)
							// .getPrice()),
							// stake,
							// BetPersistenceTypeEnum.IP);

							System.out
									.println(count
											+ ","
											+ selectionId
											+ ","
											+ lastPrice1
											+ ","
											+ backAmountList.get(1).getPrice()
											+ ","
											+ backAmountList.get(1)
													.getAmountAvailable()
											+ ","
											+ backAmountList.get(0).getPrice()
											+ ","
											+ backAmountList.get(0)
													.getAmountAvailable()
											+ ","
											+ layAmountList.get(0).getPrice()
											+ ","
											+ layAmountList.get(0)
													.getAmountAvailable()
											+ ","
											+ layAmountList.get(1).getPrice()
											+ ","
											+ layAmountList.get(1)
													.getAmountAvailable());

							count++;
							// }
							// }
							// }
						}
						break;
					}
				}
			} else {

				marketActive = false;

				// ArrayOfBet betArray = null;
				// List<Bet> bets = null;
				//
				// // print out unmatched bets
				// if ((betArray = monitorBetsService.getCurrentBets(marketId,
				// BetStatusEnum.U)) != null) {
				//
				// bets = betArray.getBet();
				//
				// for (Bet bet : bets) {
				// System.out.println(bet.getSelectionId() + " "
				// + bet.getPrice());
				// }
				//
				// } else {
				// System.out.println("No unmatched bets.");
				// }

			}

			// add wait due to throttling
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	// public void monitorMarket(int marketId, List<Integer> selectionIds) {
	//
	// // printout account info
	// this.accountsService.getAccountBalances();
	//
	// final class LastPriceHistory {
	//
	// // double lastPrice7 = 0.0;
	// // double lastPrice6 = 0.0;
	// double lastPrice5 = 0.0;
	// double lastPrice4 = 0.0;
	// double lastPrice3 = 0.0;
	// double lastPrice2 = 0.0;
	// double lastPrice1 = 0.0;
	// }
	//
	// Map<Integer, LastPriceHistory> lastPriceHistory = new HashMap<Integer,
	// LastPriceHistory>();
	//
	// GetMarketPricesReq getMarketPricesReq = new GetMarketPricesReq();
	// getMarketPricesReq.setHeader(exchangeHeader);
	// getMarketPricesReq.setMarketId(marketId);
	// GetMarketPricesResp getMarketPricesResp = null;
	// ArrayOfRunnerPrices arrayOfRunnerPrices = null;
	// RunnerPrices runnerPrices = null;
	//
	// // loop
	// boolean marketActive = true;
	// double avgPrice1 = 0.0;
	// double avgPrice2 = 0.0;
	// double avgPrice3 = 0.0;
	//
	// ArrayOfBet unmatchedBets = null;
	//
	// double stake = 0.0;
	//
	// List<Price> backAmountList = null;
	// List<Price> layAmountList = null;
	//
	// int count = 0;
	//
	// while (marketActive) {
	//
	// // get the market prices
	// getMarketPricesResp =
	// bfExchangeService.getMarketPrices(getMarketPricesReq);
	//
	// // if market active and not in play then
	// if
	// (getMarketPricesResp.getMarketPrices().getMarketStatus().equals(MarketStatusEnum.ACTIVE)
	// && (getMarketPricesResp.getMarketPrices().getDelay() == 0)) {
	//
	// arrayOfRunnerPrices = getMarketPricesResp.getMarketPrices()
	// .getRunnerPrices();
	//
	// for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices().size(); i++) {
	//
	// runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);
	//
	// // if selection id set then only print selection id's
	// // details
	// for (Integer selectionId : selectionIds) {
	//
	// // if selection id set then only print selection id's
	// // details
	// if (selectionId.equals(runnerPrices.getSelectionId())) {
	//
	// // update any unmatched bets
	// updateSelection(marketId, selectionId);
	//
	// LastPriceHistory lastPriceHistoryList = null;
	//
	// if (lastPriceHistory.containsKey(selectionId)) {
	//
	// lastPriceHistoryList = lastPriceHistory
	// .get(selectionId);
	//
	// } else {
	//
	// lastPriceHistoryList = new LastPriceHistory();
	//
	// lastPriceHistory.put(selectionId,
	// lastPriceHistoryList);
	//
	// }
	//
	//
	// // get amounts
	// backAmountList = runnerPrices.getBestPricesToBack()
	// .getPrice();
	//
	// layAmountList = runnerPrices.getBestPricesToLay()
	// .getPrice();
	//
	//
	// // update last seven prices
	// // lastPriceHistoryList.lastPrice7 = lastPriceHistoryList.lastPrice6;
	// // lastPriceHistoryList.lastPrice6 = lastPriceHistoryList.lastPrice5;
	// lastPriceHistoryList.lastPrice5 = lastPriceHistoryList.lastPrice4;
	// lastPriceHistoryList.lastPrice4 = lastPriceHistoryList.lastPrice3;
	// lastPriceHistoryList.lastPrice3 = lastPriceHistoryList.lastPrice2;
	// lastPriceHistoryList.lastPrice2 = lastPriceHistoryList.lastPrice1;
	// lastPriceHistoryList.lastPrice1 = runnerPrices.getLastPriceMatched();
	//
	// System.out.println(count + "," + selectionId
	// + ","
	// + lastPriceHistoryList.lastPrice1
	// + ","
	// + backAmountList.get(1)
	// .getPrice()
	// + ","
	// + backAmountList
	// .get(1)
	// .getAmountAvailable()
	// + ","
	// + backAmountList.get(0)
	// .getPrice()
	// + ","
	// + backAmountList
	// .get(0)
	// .getAmountAvailable()
	// + ","
	// + layAmountList.get(0)
	// .getPrice()
	// + ","
	// + layAmountList
	// .get(0)
	// .getAmountAvailable()
	// + ","
	// + layAmountList.get(1)
	// .getPrice()
	// + ","
	// + layAmountList
	// .get(1)
	// .getAmountAvailable()
	// + ","
	// + ((backAmountList.get(1).getAmountAvailable() +
	// backAmountList.get(0).getAmountAvailable()) /
	// (layAmountList.get(0).getAmountAvailable() +
	// layAmountList.get(0).getAmountAvailable()) )
	// + ","
	// + ((1.0 / layAmountList.get(0).getPrice()) * 1000.0) );
	//
	// count++;
	//
	// // if (lastPriceHistoryList.lastPrice5 != 0.0) {
	// //
	// // avgPrice1 = (lastPriceHistoryList.lastPrice5
	// // + lastPriceHistoryList.lastPrice4 + lastPriceHistoryList.lastPrice3) /
	// 3.0;
	// // avgPrice2 = (lastPriceHistoryList.lastPrice4
	// // + lastPriceHistoryList.lastPrice3 + lastPriceHistoryList.lastPrice2) /
	// 3.0;
	// // avgPrice3 = (lastPriceHistoryList.lastPrice3
	// // + lastPriceHistoryList.lastPrice2 + lastPriceHistoryList.lastPrice1) /
	// 3.0;
	// //
	// // // check averages
	// // if ((avgPrice1 > avgPrice2)
	// // && (avgPrice2 > avgPrice3)) {
	// //
	// // // check amounts
	// // if (((backAmountList.get(0).getAmountAvailable()
	// // + backAmountList.get(1).getAmountAvailable()) /
	// // (layAmountList.get(0).getAmountAvailable()
	// // + layAmountList.get(1).getAmountAvailable()) < 0.75) &&
	// // (layAmountList.get(0).getAmountAvailable() >
	// // ((1.0 / lastPriceHistoryList.lastPrice1) * 1000.0))) {
	// //
	// // // if no unmatched bets
	// // unmatchedBets = this.monitorBetsService
	// // .getCurrentBets(marketId, selectionId,
	// // BetStatusEnum.U);
	// //
	// // if (unmatchedBets == null) {
	// //
	// // stake = calculateStake(backAmountList
	// // .get(0).getPrice());
	// //
	// // // place back bet
	// // this.placeBetsService.placeBet(
	// // BetTypeEnum.B, marketId,
	// // selectionId,
	// // backAmountList.get(0).getPrice(),
	// // stake,
	// // BetPersistenceTypeEnum.IP);
	// //
	// // // place lay bet
	// // this.placeBetsService
	// // .placeBet(
	// // BetTypeEnum.L,
	// // marketId,
	// // selectionId,
	// // PriceOperator.decrementPrice(PriceOperator.decrementPrice(
	// // PriceOperator.decrementPrice(
	// // backAmountList.get(0).getPrice()))),
	// // stake,
	// // BetPersistenceTypeEnum.IP);
	// //
	// // System.out.println("bets placed.");
	// //
	// // }
	// // }
	// // }
	// // }
	// }
	// }
	// }
	// } else {
	//
	// marketActive = false;
	//
	// ArrayOfBet betArray = null;
	// List<Bet> bets = null;
	//
	// // print out unmatched bets
	// if ((betArray = monitorBetsService.getCurrentBets(marketId,
	// BetStatusEnum.U)) != null) {
	//
	// bets = betArray.getBet();
	//
	// for (Bet bet : bets) {
	// System.out.println(bet.getSelectionId() + " "
	// + bet.getPrice());
	// }
	//
	// } else {
	// System.out.println("No unmatched bets.");
	// }
	//
	//
	// }
	//
	// // add wait due to throttling
	// try {
	//
	// Thread.sleep(6000);
	//
	// } catch (InterruptedException e) {
	//
	// e.printStackTrace();
	//
	// }
	//
	// }
	//
	// }

	public void monitorMarket(int marketId, List<Integer> selectionIds) {

		final class LastPriceHistory {
			double lastPrice5 = 0.0;
			double lastPrice4 = 0.0;
			double lastPrice3 = 0.0;
			double lastPrice2 = 0.0;
			double lastPrice1 = 0.0;
		}

		Map<Integer, LastPriceHistory> lastPriceHistory = new HashMap<Integer, LastPriceHistory>();

		GetMarketPricesCompressedReq getMarketPricesCompressedReq = new GetMarketPricesCompressedReq();
		getMarketPricesCompressedReq.setHeader(exchangeHeader);
		getMarketPricesCompressedReq.setMarketId(marketId);

		GetMarketPricesCompressedResp getMarketPricesCompressedResp = null;

		// loop
		boolean marketActive = true;

		int count = 0;

		while (marketActive) {

			// get the market prices
			getMarketPricesCompressedResp = bfExchangeService
					.getMarketPricesCompressed(getMarketPricesCompressedReq);

			String[] marketInfo = getMarketPricesCompressedResp
					.getMarketPrices().split("~");
			String[] runners = getMarketPricesCompressedResp.getMarketPrices()
					.split(":");

			// if market active and not in play then
			if (marketInfo[2].equals(MarketStatusEnum.ACTIVE.value())
					&& (Integer.parseInt(marketInfo[3]) == 0)) {

				for (int i = 1; i < runners.length; i++) {

					if (runners[i].contains("%") || runners[i].contains(")")) {
						// ignore runner
						i++;
					} else {

						String[] runnerInfo = runners[i].split("~");

						for (Integer selectionId : selectionIds) {

							// if selection id set then only print selection
							// id's details
							if (selectionId == Integer.parseInt(runnerInfo[0])) {

								// update any unmatched bets
								// updateSelection(marketId, selectionId);

								LastPriceHistory lastPriceHistoryList = null;

								if (lastPriceHistory.containsKey(selectionId)) {
									lastPriceHistoryList = lastPriceHistory
											.get(selectionId);
								} else {
									lastPriceHistoryList = new LastPriceHistory();
									lastPriceHistory.put(selectionId,
											lastPriceHistoryList);
								}

								// update last five prices
								lastPriceHistoryList.lastPrice5 = lastPriceHistoryList.lastPrice4;
								lastPriceHistoryList.lastPrice4 = lastPriceHistoryList.lastPrice3;
								lastPriceHistoryList.lastPrice3 = lastPriceHistoryList.lastPrice2;
								lastPriceHistoryList.lastPrice2 = lastPriceHistoryList.lastPrice1;
								lastPriceHistoryList.lastPrice1 = Double
										.parseDouble(runnerInfo[3]);

								String[] prices = runners[i].split("\\|");
								String[] backPrices = prices[1].split("~");
								String[] layPrices = prices[2].split("~");

								System.out
										.println(count
												+ ","
												+ selectionId
												+ ","
												+ lastPriceHistoryList.lastPrice1
												+ ","
												+ backPrices[4]
												+ ","
												+ backPrices[5]
												+ ","
												+ backPrices[0]
												+ ","
												+ backPrices[1]
												+ ","
												+ layPrices[0]
												+ ","
												+ layPrices[1]
												+ ","
												+ layPrices[4]
												+ ","
												+ layPrices[5]
												+ ","
												+ Double.parseDouble(stakeFormatTwoDec.format(((Double
														.parseDouble(backPrices[5]) + Double
														.parseDouble(backPrices[1])) / (Double
														.parseDouble(layPrices[1]) + Double
														.parseDouble(layPrices[5])))))
												+ ","
												+ Double.parseDouble(stakeFormatFourDec
														.format(((1.0 / lastPriceHistoryList.lastPrice1) * 1000.0))));
								//
								count++;

								// if (lastPriceHistoryList.lastPrice5 != 0.0) {
								//
								// avgPrice1 = (lastPriceHistoryList.lastPrice5
								// + lastPriceHistoryList.lastPrice4 +
								// lastPriceHistoryList.lastPrice3) / 3.0;
								// avgPrice2 = (lastPriceHistoryList.lastPrice4
								// + lastPriceHistoryList.lastPrice3 +
								// lastPriceHistoryList.lastPrice2) / 3.0;
								// avgPrice3 = (lastPriceHistoryList.lastPrice3
								// + lastPriceHistoryList.lastPrice2 +
								// lastPriceHistoryList.lastPrice1) / 3.0;
								//
								// // check averages
								// if ((avgPrice1 > avgPrice2)
								// && (avgPrice2 > avgPrice3)) {
								//
								// // check amounts
								// if
								// (((backAmountList.get(0).getAmountAvailable()
								// + backAmountList.get(1).getAmountAvailable())
								// /
								// (layAmountList.get(0).getAmountAvailable()
								// + layAmountList.get(1).getAmountAvailable())
								// < 0.75) &&
								// (layAmountList.get(0).getAmountAvailable() >
								// ((1.0 / lastPriceHistoryList.lastPrice1) *
								// 1000.0))) {
								//
								// // if no unmatched bets
								// unmatchedBets = this.monitorBetsService
								// .getCurrentBets(marketId, selectionId,
								// BetStatusEnum.U);
								//
								// if (unmatchedBets == null) {
								//
								// stake = calculateStake(backAmountList
								// .get(0).getPrice());
								//
								// // place back bet
								// this.placeBetsService.placeBet(
								// BetTypeEnum.B, marketId,
								// selectionId,
								// backAmountList.get(0).getPrice(),
								// stake,
								// BetPersistenceTypeEnum.IP);
								//
								// // place lay bet
								// this.placeBetsService
								// .placeBet(
								// BetTypeEnum.L,
								// marketId,
								// selectionId,
								// PriceOperator.decrementPrice(PriceOperator.decrementPrice(
								// PriceOperator.decrementPrice(
								// backAmountList.get(0).getPrice()))),
								// stake,
								// BetPersistenceTypeEnum.IP);
								//
								// System.out.println("bets placed.");
								//
								// }
								// }
								// }
								// }

							}
						}
					}
				}
			} else {

				marketActive = false;

			}

			// add wait due to throttling
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	//
	//
	// public void monitorMarket(int marketId, Integer selectionId) {
	//
	// GetMarketPricesReq getMarketPricesReq = new GetMarketPricesReq();
	// getMarketPricesReq.setHeader(exchangeHeader);
	// getMarketPricesReq.setMarketId(marketId);
	// GetMarketPricesResp getMarketPricesResp = null;
	// ArrayOfRunnerPrices arrayOfRunnerPrices = null;
	// RunnerPrices runnerPrices = null;
	//
	// // loop
	// boolean marketActive = true;
	//
	// int count = 0;
	//
	// while (marketActive) {
	//
	// // get the market prices
	// getMarketPricesResp = bfExchangeService
	// .getMarketPrices(getMarketPricesReq);
	//
	// // if market active and not in play then
	// if (getMarketPricesResp.getMarketPrices().getMarketStatus().equals(
	// MarketStatusEnum.ACTIVE)
	// && (getMarketPricesResp.getMarketPrices().getDelay() == 0)) {
	//
	// arrayOfRunnerPrices = getMarketPricesResp.getMarketPrices()
	// .getRunnerPrices();
	//
	//
	// for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices().size(); i++) {
	//
	// runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);
	//
	// // if selection id set then only print selection id's details
	// if ((selectionId != null)
	// && (selectionId.intValue() == runnerPrices.getSelectionId())) {
	//
	// System.out.println(count +
	// "," + selectionId +
	// "," + runnerPrices.getLastPriceMatched()
	// + "," + runnerPrices.getBestPricesToBack().getPrice().get(1).getPrice()
	// + "," +
	// runnerPrices.getBestPricesToBack().getPrice().get(1).getAmountAvailable()
	// + "," + runnerPrices.getBestPricesToBack().getPrice().get(0).getPrice()
	// + "," +
	// runnerPrices.getBestPricesToBack().getPrice().get(0).getAmountAvailable()
	// + "," + runnerPrices.getBestPricesToLay().getPrice().get(0).getPrice()
	// + "," +
	// runnerPrices.getBestPricesToLay().getPrice().get(0).getAmountAvailable()
	// + "," + runnerPrices.getBestPricesToLay().getPrice().get(1).getPrice()
	// + "," +
	// runnerPrices.getBestPricesToLay().getPrice().get(1).getAmountAvailable()
	// );
	// break;
	// }
	// }
	// count++;
	// } else {
	// marketActive = false;
	// }
	//
	// // add wait due to throttling
	// try {
	// Thread.sleep(6000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }

	public Integer getFavourite(int marketId) {

		GetMarketPricesReq getMarketPricesReq = new GetMarketPricesReq();
		getMarketPricesReq.setHeader(exchangeHeader);
		getMarketPricesReq.setMarketId(marketId);
		GetMarketPricesResp getMarketPricesResp = null;
		MarketPrices marketPrices = null;

		// get the market prices
		getMarketPricesResp = bfExchangeService
				.getMarketPrices(getMarketPricesReq);

		// loop through all the prices adding to arrays
		marketPrices = getMarketPricesResp.getMarketPrices();

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

	public List<Integer> getFirstThreeFavourites(int marketId) {

		GetMarketPricesReq getMarketPricesReq = new GetMarketPricesReq();
		getMarketPricesReq.setHeader(exchangeHeader);
		getMarketPricesReq.setMarketId(marketId);
		GetMarketPricesResp getMarketPricesResp = null;
		MarketPrices marketPrices = null;

		// get the market prices
		getMarketPricesResp = bfExchangeService
				.getMarketPrices(getMarketPricesReq);

		// loop through all the prices adding to arrays
		marketPrices = getMarketPricesResp.getMarketPrices();

		ArrayOfRunnerPrices arrayOfRunnerPrices = marketPrices
				.getRunnerPrices();

		RunnerPrices runnerPrices = null;

		double favSelectionPrice = 1000.0;
		double secondFavSelectionPrice = 1000.0;
		double thirdFavSelectionPrice = 1000.0;

		int favSelectionId = 0;
		int secondFavSelectionId = 0;
		int thirdFavSelectionId = 0;

		Price price = null;

		// find favs
		for (int i = 0; i < arrayOfRunnerPrices.getRunnerPrices().size(); i++) {

			runnerPrices = arrayOfRunnerPrices.getRunnerPrices().get(i);
			price = runnerPrices.getBestPricesToBack().getPrice().get(0);

			if ((price != null) && (price.getPrice() < favSelectionPrice)) {

				thirdFavSelectionId = secondFavSelectionId;
				secondFavSelectionId = favSelectionId;
				favSelectionId = runnerPrices.getSelectionId();

				thirdFavSelectionPrice = secondFavSelectionPrice;
				secondFavSelectionPrice = favSelectionPrice;
				favSelectionPrice = price.getPrice();

			} else if ((price != null)
					&& (price.getPrice() < secondFavSelectionPrice)) {

				thirdFavSelectionId = secondFavSelectionId;
				secondFavSelectionId = runnerPrices.getSelectionId();

				thirdFavSelectionPrice = secondFavSelectionPrice;
				secondFavSelectionPrice = price.getPrice();

			} else if ((price != null)
					&& (price.getPrice() < thirdFavSelectionPrice)) {

				thirdFavSelectionId = runnerPrices.getSelectionId();

				thirdFavSelectionPrice = price.getPrice();

			}

		}

		List<Integer> favourites = new ArrayList<Integer>();

		favourites.add(favSelectionId);
		favourites.add(secondFavSelectionId);
		favourites.add(thirdFavSelectionId);

		return favourites;

	}

	/**
     * 
     */
	public Integer getSelectionId(int marketId, String selectionName) {

		// due to throttling wait for 12 secs
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			}
			i++;
		}

		return selectionId;
	}

	private double calculateStake(double price) {

		double stake = 2.0;

		// if (price > 6.0) {
		// stake = 2.0;
		// } else if (price < 2.0) {
		// stake = 5.0;
		// } else {
		// stake = (Double.parseDouble(stakeFormatTwoDec.format(((5.0 - (price -
		// 1.0)) * 0.75) + 2.0)));
		// }

		return stake;
	}

	public void updateSelection(int market, Integer selectionId) {

		ArrayOfBet betArray = null;
		List<Bet> bets = null;

		if ((betArray = monitorBetsService.getCurrentBets(market, selectionId,
				BetStatusEnum.U)) != null) {

			bets = betArray.getBet();

			System.out.println(bets.size() + " bets are unmatched for "
					+ selectionId);

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

		} else {
			System.out.println("All bets are unmatched for " + selectionId);
		}

	}

}
