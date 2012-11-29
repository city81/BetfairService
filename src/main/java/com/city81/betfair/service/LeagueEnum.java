package com.city81.betfair.service;

/**
 * Football Data to Betfair league name mapping
 * 
 * @author geraint.jones
 *
 */
public enum LeagueEnum {
	
	// leagues
	E0("E0",2022802),
	E1("E1",1908053),
	E2("E2",1908054),
	E3("E3",1908056),
	I1("I1",241361),
	SP1("SP1",259241);
		
	private final String footballDataName;
	private final int betfairEventId;
	
	private LeagueEnum(final String footballDataName, final int betfairEventId) {
		this.footballDataName = footballDataName;
		this.betfairEventId = betfairEventId;		
	}
	
	public static int getBetfairEventId(String footballDataName) {
		LeagueEnum matchedTeam = null;
		for (LeagueEnum team : LeagueEnum.values()) {
			if (team.footballDataName.equals(footballDataName)) {
				matchedTeam = team;
				break;
			}
		}
		return matchedTeam.betfairEventId;
	}
	
}
