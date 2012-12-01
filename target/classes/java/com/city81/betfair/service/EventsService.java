package com.city81.betfair.service;

import java.util.List;

import com.betfair.publicapi.types.global.v3.APIRequestHeader;
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
     * 
     */
	public Integer getMarketId(int eventId, String fixture, String marketName) {

		Integer foundId = null;
		
		// drill down to find the event
		foundId = getEventId(eventId, fixture);

		if (foundId != null) {
			// get the market id for the given market name
			foundId = getMarketId(foundId, marketName);
		}
		
		return foundId;
	}

	/**
	 * 
	 * @param eventId
	 * @param marketName
	 * @return
	 */
	public Integer getMarketId(int eventId, String marketName) {

		Integer marketId = null;

		GetEventsReq getEventsReq = new GetEventsReq();
		getEventsReq.setHeader(globalHeader);
		getEventsReq.setEventParentId(eventId);

		// get the markets
		GetEventsResp getEventsResp = bfGlobalService.getEvents(getEventsReq);
		if (!(getEventsResp.getErrorCode().equals(GetEventsErrorEnum.OK))) {
			System.out.println(getEventsResp.getErrorCode());
		}
		List<MarketSummary> markets = getEventsResp.getMarketItems()
				.getMarketSummary();

		// loop through the runners to find the market
		int i = 0;
		while ((marketId == null) && (i < markets.size())) {
						
			if (markets.get(i).getMarketName().equals(marketName)) {
				marketId = markets.get(i).getMarketId();
			}
			if (markets.get(i).getMarketName().equals(marketName+" Unmanaged")) {
				marketId = markets.get(i).getMarketId();
			}
			i++;
		}

		return marketId;
	}

	public Integer getEventId(int parentEventId, String fixture) {

		Integer eventId = null;

		GetEventsReq getEventsReq = new GetEventsReq();
		getEventsReq.setHeader(globalHeader);
		getEventsReq.setEventParentId(parentEventId);

		// get the events
		GetEventsResp getEventsResp = bfGlobalService.getEvents(getEventsReq);
		if (!(getEventsResp.getErrorCode().equals(GetEventsErrorEnum.OK))) {
			System.out.println(getEventsResp.getErrorCode());
		}
		if (!(getEventsResp.getErrorCode().equals(GetEventsErrorEnum.INVALID_EVENT_ID))) {
			List<BFEvent> events = getEventsResp.getEventItems().getBFEvent();

			// loop through the events to find the event
			int i = 0;
			while ((eventId == null) && (i < events.size())) {
			
				if (events.get(i).getEventName().contains(fixture)) {
					eventId = events.get(i).getEventId();
				} else {
					eventId = getEventId(events.get(i).getEventId(), fixture);
				}
				i++;
			}
		}

		return eventId;

	}
	
}
