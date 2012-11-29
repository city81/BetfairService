package com.city81.betfair.service;

/**
 * Football Data to Betfair team name mapping
 * 
 * @author geraint.jones
 *
 */
public enum TeamEnum {
	
	// Premier League
	ARSENAL("Arsenal","Arsenal"),
	ASTONVILLA("Aston Villa","Aston Villa"),
	CHELSEA("Chelsea","Chelsea"),
	EVERTON("Everton","Everton"),
	FULHAM("Fulham","Fulham"),
	LIVERPOOL("Liverpool","Liverpool"),
	MANCITY("Man City","Man City"),
	MANUTD("Man United","Man Utd"),
	NEWCASTLE("Newcastle","Newcastle"),
	NORWICH("Norwich","Norwich"),
	QPR("QPR","QPR"),
	READING("Reading","Reading"),
	SOUTHAMPTON("Southampton","Southampton"),
	STOKE("Stoke","Stoke"),
	SUNDERLAND("Sunderland","Sunderland"),
	SWANSEA("Swansea","Swansea"),
	TOTTENHAM("Tottenham","Tottenham"),
	WESTBROM("West Brom","West Brom"),
	WESTHAM("West Ham","West Ham"),
	WIGAN("Wigan","Wigan"),
	
	// Championship
	BARNSLEY("Barnsley","Barnsley"),
	BIRMINGHAM("Birmingham","Birmingham"),
	BLACKPOOL("Blackpool","Blackpool"),
	BLACKBURN("Blackburn","Blackburn"),
	BOLTON("Bolton","Bolton"),
	BRIGHTON("Brighton","Brighton"),
	BRISTOLCITY("Bristol City","Bristol City"),
	BURNLEY("Burnley","Burnley"),
	CARDIFF("Cardiff","Cardiff"),
	CHARLTON("Charlton","Charlton"),
	CRYSTALPALACE("Crystal Palace","C Palace"),
	DERBY("Derby","Derby"),
	HUDDERSFIELD("Huddersfield","Huddersfield"),
	HULL("Hull","Hull"),
	IPSWICH("Ipswich","Ipswich"),
	LEEDS("Leeds","Leeds"),
	LEICESTER("Leicester","Leicester"),
	MIDDLESBROUGH("Middlesbrough","Middlesbrough"),
	MILLWALL("Millwall","Millwall"),
	NOTTMFOREST("Nott'm Forest","Nottm Forest"),
	PETERBOROUGH("Peterboro","Peterborough"),
	SHEFFWED("Sheffield Weds","Sheff Wed"),
	WATFORD("Watford","Watford"),
	WOLVES("Wolves","Wolves"),
	
	// League One
	BOURNEMOUTH("Bournemouth","Bournemouth"),
	BRENTFORD("Brentford","Brentford"),
	BURY("Bury","Bury"),
	CARLISLE("Carlisle","Carlisle"),
	COLCHESTER("Colchester","Colchester"),
	COVENTRY("Coventry","Coventry"),
	CRAWLEY("Crawley Town","Crawley Town"),
	CREWE("Crewe","Crewe"),
	DONCASTER("Doncaster","Doncaster"),
	HARTLEPOOL("Hartlepool","Hartlepool"),
	LORIENT("Leyton Orient","L Orient"),
	MKDONS("Milton Keynes Dons","MK Dons"),
	NOTTSCO("Notts County","Notts Co"),
	OLDHAM("Oldham","Oldham"),
	PORTSMOUTH("Portsmouth","Portsmouth"),
	PRESTON("Preston","Preston"),
	SCUNTHORPE("Scunthorpe","Scunthorpe"),
	SHEFFUTD("Sheffield United","Sheff Utd"),
	SHREWSBURY("Shrewsbury","Shrewsbury"),
	STEVENAGE("Stevenage","Stevenage"),
	SWINDON("Swindon","Swindon"),
	TRANMERE("Tranmere","Tranmere"),
	WALSALL("Walsall","Walsall"),
	YEOVIL("Yeovil","Yeovil"),
	
	// League Two
	ACCRINGTON("Accrington","Accrington S"),
	AFCWIMBLEDON("AFC Wimbledon","AFC Wimbledon"),
	ALDERSHOT("Aldershot","Aldershot"),
	BARNET("Barnet","Barnet"),
	BRADFORD("Bradford","Bradford"),
	BRISTOLROVERS("Bristol Rvs","Bristol Rovers"),
	BURTON("Burton","Burton"),
	CHELTENHAM("Cheltenham","Cheltenham"),
	CHESTERFIELD("Chesterfield","Chesterfield"),
	DAGENHAM("Dag and Red","Dag and Red"),
	EXETER("Exeter","Exeter"),
	FLEETWOOD("Fleetwood Town","Fleetwood Town"),
	GILLINGHAM("Gillingham","Gillingham"),
	MORECAMBE("Morecambe","Morecambe"),
	NORTHAMPTON("Northampton","Northampton"),
	OXFORDUTD("Oxford","Oxford Utd"),
	PLYMOUTH("Plymouth","Plymouth"),
	PORTVALE("Port Vale","Port Vale"),
	ROCHDALE("Rochdale","Rochdale"),
	ROTHERHAM("Rotherham","Rotherham"),
	SOUTHEND("Southend","Southend"),
	TORQUAY("Torquay","Torquay"),	
	WYCOMBE("Wycombe","Wycombe"),
	YORK("York","York City"),
	
    // La Liga
    LACORUNA("La Coruna","Deportivo"),
    BARCELONA("Barcelona","Barcelona"),
    MALAGA("Malaga","Malaga"),
    VALLADOLID("Valladolid","Valladolid"),
    REALMADRID("Real Madrid","Real Madrid"),
    CELTA("Celta","Celta Vigo"),
    VALENCIA("Valencia","Valencia"),
    ATHBILBAO("Ath Bilbao","Ath Bilbao"),
    ESPANOL("Espanol","Espanyol"),
    VALLECANO("Vallecano","Rayo Vallecano"),
    GETAFE("Getafe","Getafe"),
    LEVANTE("Levante","Levante"),
    GRANADA("Granada","Granada"),
    ZARAGOZA("Zaragoza","Zaragoza"),
    OSASUNA("Osasuna","Osasuna"),
    BETIS("Betis","Betis"),
    SOCIEDAD("Sociedad","Sociedad"),
    ATHMADRID("Ath Madrid","Atl Madrid"),
    SEVILLA("Sevilla","Sevilla"),
    MALLORCA("Mallorca","Mallorca"),

    // Seria A
    JUVENTUS("Juventus","Juventus"),
    NAPOLI("Napoli","Napoli"),
    LAZIO("Lazio","Lazio"),
    MILAN("Milan","AC Milan"),
    ATALANTA("Atalanta","Atalanta"),
    SIENA("Siena","Siena"),
    CAGLIARI("Cagliari","Cagliari"),
    BOLOGNA("Bologna","Bologna"),
    CHIEVO("Chievo","Chievo"),
    FIORENTINA("Fiorentina","Fiorentina"),
    GENEA("Genoa","Genoa"),
    ROMA("Roma","Roma"),
    INTER("Inter","Inter"),
    CATANIA("Catania","Catania"),
    PALERMO("Palermo","Palermo"),
    TORINO("Torino","Torino"),
    PARMA("Parma","Parma"),
    SAMPDORIA("Sampdoria","Sampdoria"),
    UDINESE("Udinese","Udinese"),
    PESCARA("Pescara","Pescara"); 
	
	private final String footballDataName;
	private final String betfairName;
	
	private TeamEnum(final String footballDataName, final String betfairName) {
		this.footballDataName = footballDataName;
		this.betfairName = betfairName;		
	}
	
	public static String getBetfairName(String footballDataName) {
		TeamEnum matchedTeam = null;
		for (TeamEnum team : TeamEnum.values()) {
			if (team.footballDataName.equals(footballDataName)) {
				matchedTeam = team;
				break;
			}
		}
		return matchedTeam.betfairName;
	}
	
}
