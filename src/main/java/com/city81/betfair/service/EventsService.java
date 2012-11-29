package com.city81.betfair.service;

import java.util.ArrayList;
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
			
//			System.out.println(markets.get(i).getMarketName());
			
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

//			System.out.println(events.get(i).getEventName());
			
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
	
	 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList getRacingMarkets() {
 
        GetEventsReq req = new GetEventsReq();
        req.setHeader(globalHeader);
        req.setEventParentId(13);
        GetEventsResp getEventsResp = bfGlobalService.getEvents(req);
        System.out.println("Get Racing Markets");
        System.out.println();
        ArrayOfMarketSummary marketSummary = getEventsResp.getMarketItems();
 
        ArrayList marketInfoList = new ArrayList();
        RacingMarketInfo marketInfo = null;
 
        if ((marketSummary != null) && (marketSummary.getMarketSummary() != null)) {
        	
        
        for (int i = 0; i < marketSummary.getMarketSummary().size(); i++) {
 
            if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Place") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("US") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("RSA") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("ITA") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Rsa") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("UAE") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Dist") >= 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("AvB") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("NTF") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("FRA") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("GER") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("F/C") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("SWE") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("PA") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Aus") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Group") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("NZL") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Double") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("W/O") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("Fav") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().indexOf("RFC") > 0) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Cork")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Gal")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Dun")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Kil")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Ros")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("List")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Cur")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Lim")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Naa")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Nav")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Wex")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Sli")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Gow")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Thur")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Wood")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Tip")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Fai")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Chant")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Clon")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Leo")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Down")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Bal")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Pun")) {
            } else if (marketSummary.getMarketSummary().get(i).getMarketName().startsWith("Tra")) {
            } else {
                System.out.println(marketSummary.getMarketSummary().get(i).getStartTime());
                System.out.println(marketSummary.getMarketSummary().get(i).getMarketName());
                marketInfo = new RacingMarketInfo();
                marketInfo.marketId =
                        marketSummary.getMarketSummary().get(i).getMarketId();
                marketInfo.marketName =
                        marketSummary.getMarketSummary().get(i).getMarketName();
                marketInfo.marketStartTime =
                        marketSummary.getMarketSummary().get(i).getStartTime();
                marketInfoList.add(marketInfo);

            }
        }
        }
        return marketInfoList;	

    }
    
}
