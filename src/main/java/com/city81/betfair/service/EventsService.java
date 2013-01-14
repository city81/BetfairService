package com.city81.betfair.service;

import java.util.List;

import com.betfair.publicapi.types.global.v3.APIRequestHeader;
import com.betfair.publicapi.types.global.v3.ArrayOfMarketSummary;
import com.betfair.publicapi.types.global.v3.BFEvent;
import com.betfair.publicapi.types.global.v3.GetEventsErrorEnum;
import com.betfair.publicapi.types.global.v3.GetEventsReq;
import com.betfair.publicapi.types.global.v3.GetEventsResp;
import com.betfair.publicapi.types.global.v3.MarketSummary;
import com.betfair.publicapi.v3.bfglobalservice.BFGlobalService;

/**
 * This class provides services for retrieving events.
 * 
 * @author geraint.jones
 * 
 */
public class EventsService {

	private BFGlobalService bfGlobalService;
	private APIRequestHeader globalHeader;

	/**
	 * Constructor taking as arguments the BFGlobalService used to execute the
	 * bets, and the APIRequestHeader which contains the session ie login
	 * credentials
	 * 
	 * @param bfGlobalService
	 *            betfair service api
	 * @param globalHeader
	 *            request header containing the token
	 */
	public EventsService(BFGlobalService bfGlobalService,
			APIRequestHeader globalHeader) {
		this.bfGlobalService = bfGlobalService;
		this.globalHeader = globalHeader;
	}

	/**
	 * Obtain the identifier of a market for a given event identifer, event name
	 * and market name.
	 * 
	 * @param eventId
	 *            event identifier eg 2022802 - Premier League
	 * @param eventName
	 *            event name eg Man Utd v Man City
	 * @param marketName
	 *            market name eg Match Odds, Half Time return Integer market
	 *            identifier.
	 */
	public Integer getMarketId(int eventId, String eventName, String marketName) {

		Integer foundId = null;

		// drill down to find the event
		foundId = getEventId(eventId, eventName);

		if (foundId != null) {
			// get the market id for the given market name
			foundId = getMarketId(foundId, marketName);
		}

		return foundId;
	}

	/**
	 * Obtain the identifier of a market for a given event identifer and market
	 * name.
	 * 
	 * @param eventId
	 *            event identifier eg 2022802 - Premier League
	 * @param marketName
	 *            market name eg Match Odds, Half Time return Integer market
	 *            identifier.
	 */
	public Integer getMarketId(int eventId, String marketName) {

		// identify selection but due to throttling wait for 12 secs before
		// calling
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// do nothing
		}

		Integer marketId = null;

		GetEventsReq getEventsReq = new GetEventsReq();
		getEventsReq.setHeader(globalHeader);
		getEventsReq.setEventParentId(eventId);

		// get the markets
		GetEventsResp getEventsResp = bfGlobalService.getEvents(getEventsReq);
		
		if (getEventsResp.getErrorCode().equals(GetEventsErrorEnum.OK)) {
			
			List<MarketSummary> markets = getEventsResp.getMarketItems()
					.getMarketSummary();

			// loop through the runners to find the market
			int i = 0;
			while ((marketId == null) && (i < markets.size())) {

				if (markets.get(i).getMarketName().equals(marketName)) {
					marketId = markets.get(i).getMarketId();
				}
				i++;
			}
		}
		
		return marketId;
	}

	/**
	 * Obtain the identifier of an event for a given parent event identifer and
	 * event name.
	 * 
	 * @param eventId
	 *            event identifier eg 2022802 - Premier League
	 * @param eventName
	 *            event name eg Man Utd v Man City return Integer event
	 *            identifier.
	 */
	public Integer getEventId(int parentEventId, String eventName) {

		// identify selection but due to throttling wait for 12 secs before
		// calling
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e) {
			// do nothing
		}

		Integer eventId = null;

		GetEventsReq getEventsReq = new GetEventsReq();
		getEventsReq.setHeader(globalHeader);
		getEventsReq.setEventParentId(parentEventId);

		// get the events
		GetEventsResp getEventsResp = bfGlobalService.getEvents(getEventsReq);

		if (getEventsResp.getErrorCode().equals(GetEventsErrorEnum.OK)) {

			List<BFEvent> events = getEventsResp.getEventItems().getBFEvent();

			// loop through the events to find the event
			int i = 0;
			while ((eventId == null) && (i < events.size())) {

				if (events.get(i).getEventName().contains(eventName)) {
					eventId = events.get(i).getEventId();
				} else {
					eventId = getEventId(events.get(i).getEventId(), eventName);
				}
				i++;
			}
		}

		return eventId;

	}

	/**
	 * Obtain the collection of Market Summary objects for a given event parent id.
	 *
	 * @param parentEventId parent event identifier eg 13 - Racing Markets
	 * @return ArrayOfMarketSummary collection of Market Summary objects
	 */
	public ArrayOfMarketSummary getMarketSummaries(int eventParentId) {
 
        	GetEventsReq req = new GetEventsReq();
        	req.setHeader(globalHeader);
        	req.setEventParentId(eventParentId);
        	GetEventsResp getEventsResp = bfGlobalService.getEvents(req);
        	return getEventsResp.getMarketItems();
	}

}

