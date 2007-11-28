/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

function Com_Flightexplorer_Fasttrack() {
}

Com_Flightexplorer_Fasttrack.prototype = new ZmZimletBase;
Com_Flightexplorer_Fasttrack.prototype.constructor = Com_Flightexplorer_Fasttrack;
//Map of airline codes 

Com_Flightexplorer_Fasttrack.airlines3 = {};
Com_Flightexplorer_Fasttrack.airlines3["EIN"] = "Aer Lingus";
Com_Flightexplorer_Fasttrack.airlines3["SER"] = "Aerocalifornia";
Com_Flightexplorer_Fasttrack.airlines3["AMX"] = "Aerovias De Mexico";
Com_Flightexplorer_Fasttrack.airlines3["AMM"] = "Air 2000";
Com_Flightexplorer_Fasttrack.airlines3["ACA"] = "Air Canada";
Com_Flightexplorer_Fasttrack.airlines3["JZA"] = "Air Canada Jazz";
Com_Flightexplorer_Fasttrack.airlines3["CRQ"] = "Air Creebec";
Com_Flightexplorer_Fasttrack.airlines3["AFR"] = "Air France";
Com_Flightexplorer_Fasttrack.airlines3["GGN"] = "Air Georgian";
Com_Flightexplorer_Fasttrack.airlines3["AJM"] = "Air Jamaica";
Com_Flightexplorer_Fasttrack.airlines3["AKL"] = "Air Kilroe";
Com_Flightexplorer_Fasttrack.airlines3["AMW"] = "Air Midwest";
Com_Flightexplorer_Fasttrack.airlines3["RSI"] = "Air Sunshine";
Com_Flightexplorer_Fasttrack.airlines3["TSC"] = "Air Transat";
Com_Flightexplorer_Fasttrack.airlines3["UKA"] = "Air UK";
Com_Flightexplorer_Fasttrack.airlines3["AWI"] = "Air Wisconsin";
Com_Flightexplorer_Fasttrack.airlines3["ABX"] = "Airborne Express";
Com_Flightexplorer_Fasttrack.airlines3["TRS"] = "AirTran";
Com_Flightexplorer_Fasttrack.airlines3["ASA"] = "Alaska Airlines";
Com_Flightexplorer_Fasttrack.airlines3["AER"] = "Alaska Central Express";
Com_Flightexplorer_Fasttrack.airlines3["AZA"] = "Alitalia";
Com_Flightexplorer_Fasttrack.airlines3["AAH"] = "Aloha Airlines";
Com_Flightexplorer_Fasttrack.airlines3["AWE"] = "America West";
Com_Flightexplorer_Fasttrack.airlines3["AAL"] = "American Airlines";
Com_Flightexplorer_Fasttrack.airlines3["EGF"] = "American Eagle";
Com_Flightexplorer_Fasttrack.airlines3["AMT"] = "American Trans Air";
Com_Flightexplorer_Fasttrack.airlines3["AMF"] = "Ameriflight";
Com_Flightexplorer_Fasttrack.airlines3["AJI"] = "Ameristar";
Com_Flightexplorer_Fasttrack.airlines3["CIR"] = "Arctic Circle Air";
Com_Flightexplorer_Fasttrack.airlines3["SYX"] = "Astral";
Com_Flightexplorer_Fasttrack.airlines3["MDC"] = "Atlantic Aero";
Com_Flightexplorer_Fasttrack.airlines3["BLR"] = "Atlantic Coast";
Com_Flightexplorer_Fasttrack.airlines3["CAA"] = "Atlantic Southeast";
Com_Flightexplorer_Fasttrack.airlines3["CHP"] = "Aviacsa";
Com_Flightexplorer_Fasttrack.airlines3["BHS"] = "Bahamasair Holdings";
Com_Flightexplorer_Fasttrack.airlines3["BKA"] = "Bankair";
Com_Flightexplorer_Fasttrack.airlines3["BVN"] = "Baron Aviation";
Com_Flightexplorer_Fasttrack.airlines3["BLS"] = "Bearskin Lake Air";
Com_Flightexplorer_Fasttrack.airlines3["BMI"] = "Bemoair";
Com_Flightexplorer_Fasttrack.airlines3["BRG"] = "Bering Air";
Com_Flightexplorer_Fasttrack.airlines3["BSY"] = "Big Sky";
Com_Flightexplorer_Fasttrack.airlines3["BAL"] = "Britannia";
Com_Flightexplorer_Fasttrack.airlines3["BAW"] = "British Airways";
Com_Flightexplorer_Fasttrack.airlines3["SHT"] = "British Airways Shuttle";
Com_Flightexplorer_Fasttrack.airlines3["BMA"] = "British Midland Airways";
Com_Flightexplorer_Fasttrack.airlines3["BRT"] = "British Regional";
Com_Flightexplorer_Fasttrack.airlines3["LXJ"] = "Business Jet Solutions";
Com_Flightexplorer_Fasttrack.airlines3["BEE"] = "Busy Bee";
Com_Flightexplorer_Fasttrack.airlines3["KAP"] = "Cape Air";
Com_Flightexplorer_Fasttrack.airlines3["CMY"] = "Cape Smythe";
Com_Flightexplorer_Fasttrack.airlines3["CPA"] = "Cathay Pacific";
Com_Flightexplorer_Fasttrack.airlines3["GLR"] = "Central Mountain Air";
Com_Flightexplorer_Fasttrack.airlines3["CAL"] = "China Airlines";
Com_Flightexplorer_Fasttrack.airlines3["MYT"] = "China Airlines";
Com_Flightexplorer_Fasttrack.airlines3["COM"] = "Comair";
Com_Flightexplorer_Fasttrack.airlines3["UCA"] = "Commutair";
Com_Flightexplorer_Fasttrack.airlines3["MXA"] = "Compania Mexicana";
Com_Flightexplorer_Fasttrack.airlines3["COA"] = "Continental Airlines";
Com_Flightexplorer_Fasttrack.airlines3["BTA"] = "Continental Express";
Com_Flightexplorer_Fasttrack.airlines3["CMI"] = "Continental Micronesia";
Com_Flightexplorer_Fasttrack.airlines3["CEA"] = "Corporate Express";
Com_Flightexplorer_Fasttrack.airlines3["DAL"] = "Delta Airlines";
Com_Flightexplorer_Fasttrack.airlines3["DHL"] = "DHL Airways";
Com_Flightexplorer_Fasttrack.airlines3["EZE"] = "Eastern Airways";
Com_Flightexplorer_Fasttrack.airlines3["TUD"] = "Eastern Airways Express";
Com_Flightexplorer_Fasttrack.airlines3["EZY"] = "EasyJet";
Com_Flightexplorer_Fasttrack.airlines3["JEM"] = "Emerald";
Com_Flightexplorer_Fasttrack.airlines3["CFS"] = "Empire";
Com_Flightexplorer_Fasttrack.airlines3["EVA"] = "Eva Airways";
Com_Flightexplorer_Fasttrack.airlines3["EJA"] = "Executive Jet";
Com_Flightexplorer_Fasttrack.airlines3["EJM"] = "Executive Jet Express";
Com_Flightexplorer_Fasttrack.airlines3["FAB"] = "First Air";
Com_Flightexplorer_Fasttrack.airlines3["FIV"] = "Five Star";
Com_Flightexplorer_Fasttrack.airlines3["ACT"] = "Flight Line";
Com_Flightexplorer_Fasttrack.airlines3["OPT"] = "Flight Options";
Com_Flightexplorer_Fasttrack.airlines3["FLX"] = "Florida Express";
Com_Flightexplorer_Fasttrack.airlines3["FFT"] = "Frontier Airlines";
Com_Flightexplorer_Fasttrack.airlines3["FTA"] = "Frontier Flying";
Com_Flightexplorer_Fasttrack.airlines3["GBL"] = "Great Britain Airways";
Com_Flightexplorer_Fasttrack.airlines3["GOE"] = "Go Fly";
Com_Flightexplorer_Fasttrack.airlines3["GLA"] = "Great Lakes Aviation";
Com_Flightexplorer_Fasttrack.airlines3["GFT"] = "Gulfstream";
Com_Flightexplorer_Fasttrack.airlines3["HAL"] = "Hawaiian Airlines";
Com_Flightexplorer_Fasttrack.airlines3["QXE"] = "Horizon Air";
Com_Flightexplorer_Fasttrack.airlines3["IBE"] = "Iberia Airlines";
Com_Flightexplorer_Fasttrack.airlines3["ISA"] = "Island";
Com_Flightexplorer_Fasttrack.airlines3["JAL"] = "Japan Air Lines";
Com_Flightexplorer_Fasttrack.airlines3["JEA"] = "Jersey European";
Com_Flightexplorer_Fasttrack.airlines3["JBU"] = "JetBlue Airways";
Com_Flightexplorer_Fasttrack.airlines3["JIA"] = "Jetstream";
Com_Flightexplorer_Fasttrack.airlines3["KHA"] = "Kitty Hawk";
Com_Flightexplorer_Fasttrack.airlines3["KLM"] = "KLM Royal Dutch Airlines";
Com_Flightexplorer_Fasttrack.airlines3["KAL"] = "Korean Airlines";
Com_Flightexplorer_Fasttrack.airlines3["LAB"] = "LAB. Flying Service";
Com_Flightexplorer_Fasttrack.airlines3["LOG"] = "Logan Air";
Com_Flightexplorer_Fasttrack.airlines3["DLH"] = "Lufthansa";
Com_Flightexplorer_Fasttrack.airlines3["MSK"] = "Maersk Air";
Com_Flightexplorer_Fasttrack.airlines3["FRL"] = "Maria Eligonzales Farelas";
Com_Flightexplorer_Fasttrack.airlines3["MRA"] = "Martinaire";
Com_Flightexplorer_Fasttrack.airlines3["ASH"] = "Mesa Airlines";
Com_Flightexplorer_Fasttrack.airlines3["MES"] = "Mesaba";
Com_Flightexplorer_Fasttrack.airlines3["MEP"] = "Midwest Express";
Com_Flightexplorer_Fasttrack.airlines3["MTN"] = "Mountain Air Cargo";
Com_Flightexplorer_Fasttrack.airlines3["FLG"] = "Northwest Express";
Com_Flightexplorer_Fasttrack.airlines3["NWA"] = "Northwest Airlines";
Com_Flightexplorer_Fasttrack.airlines3["PDT"] = "Pace";
Com_Flightexplorer_Fasttrack.airlines3["PCO"] = "Pacific Coastal Airline";
Com_Flightexplorer_Fasttrack.airlines3["PEN"] = "Peninsula";
Com_Flightexplorer_Fasttrack.airlines3["ALO"] = "Pennsylvania Commuter";
Com_Flightexplorer_Fasttrack.airlines3["PAG"] = "Perimeter Aviation";
Com_Flightexplorer_Fasttrack.airlines3["WDY"] = "Phoenix Airline Services";
Com_Flightexplorer_Fasttrack.airlines3["LBQ"] = "Quest Diagnostics";
Com_Flightexplorer_Fasttrack.airlines3["REX"] = "Ram Air Freight";
Com_Flightexplorer_Fasttrack.airlines3["RYN"] = "Ryan Aviation";
Com_Flightexplorer_Fasttrack.airlines3["RYR"] = "Ryanair";
Com_Flightexplorer_Fasttrack.airlines3["SAS"] = "Scandinavian Airlines";
Com_Flightexplorer_Fasttrack.airlines3["SLI"] = "Servicios Aereos Litoral";
Com_Flightexplorer_Fasttrack.airlines3["TCF"] = "Shuttle America";
Com_Flightexplorer_Fasttrack.airlines3["SKW"] = "Sky West";
Com_Flightexplorer_Fasttrack.airlines3["SSV"] = "SkyService";
Com_Flightexplorer_Fasttrack.airlines3["SGK"] = "Skyward Aviation";
Com_Flightexplorer_Fasttrack.airlines3["SWA"] = "Southwest";
Com_Flightexplorer_Fasttrack.airlines3["NKS"] = "Spirit Airlines";
Com_Flightexplorer_Fasttrack.airlines3["SAY"] = "Suckling";
Com_Flightexplorer_Fasttrack.airlines3["HKA"] = "Superior Aviation";
Com_Flightexplorer_Fasttrack.airlines3["SWR"] = "SwissAir";
Com_Flightexplorer_Fasttrack.airlines3["TAI"] = "Taca International";
Com_Flightexplorer_Fasttrack.airlines3["TNR"] = "Tanana Air";
Com_Flightexplorer_Fasttrack.airlines3["LOF"] = "Trans States";
Com_Flightexplorer_Fasttrack.airlines3["TAO"] = "Transportes Aeromar";
Com_Flightexplorer_Fasttrack.airlines3["USA"] = "US Airways";
Com_Flightexplorer_Fasttrack.airlines3["CJC"] = "US Airways Express";
Com_Flightexplorer_Fasttrack.airlines3["USC"] = "US Check";
Com_Flightexplorer_Fasttrack.airlines3["UAL"] = "United Airlines";
Com_Flightexplorer_Fasttrack.airlines3["VIR"] = "Virgin Atlantic";
Com_Flightexplorer_Fasttrack.airlines3["VLM"] = "Vlaamse Luchttransportmaatscha";
Com_Flightexplorer_Fasttrack.airlines3["VNA"] = "Warbelows Air Ventures";
Com_Flightexplorer_Fasttrack.airlines3["PCM"] = "WestAir";
Com_Flightexplorer_Fasttrack.airlines3["WJA"] = "WestJet Airlines";

Com_Flightexplorer_Fasttrack.airlines2 = new Object();
Com_Flightexplorer_Fasttrack.airlines2["TZ"] = "ATA Airlines";
Com_Flightexplorer_Fasttrack.airlines2["EI"] = "Aer Lingus";
Com_Flightexplorer_Fasttrack.airlines2["AM"] = "Aeromexico";
Com_Flightexplorer_Fasttrack.airlines2["AC"] = "Air Canada";
Com_Flightexplorer_Fasttrack.airlines2["CA"] = "Air China";
Com_Flightexplorer_Fasttrack.airlines2["AF"] = "Air France";
Com_Flightexplorer_Fasttrack.airlines2["IJ"] = "Air Liberte";
Com_Flightexplorer_Fasttrack.airlines2["NZ"] = "Air New Zealand";
Com_Flightexplorer_Fasttrack.airlines2["FL"] = "Air Tran";
Com_Flightexplorer_Fasttrack.airlines2["TS"] = "Air Transat (Canada)";
Com_Flightexplorer_Fasttrack.airlines2["GB"] = "Airborne Express";
Com_Flightexplorer_Fasttrack.airlines2["AS"] = "Alaska Airlines";
Com_Flightexplorer_Fasttrack.airlines2["AZ"] = "Alitalia";
Com_Flightexplorer_Fasttrack.airlines2["NH"] = "All Nippon Airways";
Com_Flightexplorer_Fasttrack.airlines2["AQ"] = "Aloha Airlines";
Com_Flightexplorer_Fasttrack.airlines2["HP"] = "America West";
Com_Flightexplorer_Fasttrack.airlines2["AA"] = "American Airlines";
Com_Flightexplorer_Fasttrack.airlines2["AN"] = "Ansett Australia";
Com_Flightexplorer_Fasttrack.airlines2["AV"] = "Avianca";
Com_Flightexplorer_Fasttrack.airlines2["UP"] = "Bahamasair";
Com_Flightexplorer_Fasttrack.airlines2["JV"] = "Bearskin Airlines";
Com_Flightexplorer_Fasttrack.airlines2["GQ"] = "Big Sky Airways";
Com_Flightexplorer_Fasttrack.airlines2["BU"] = "Braathens";
Com_Flightexplorer_Fasttrack.airlines2["BA"] = "British Airways";
Com_Flightexplorer_Fasttrack.airlines2["BD"] = "British Midland";
Com_Flightexplorer_Fasttrack.airlines2["ED"] = "CCAir";
Com_Flightexplorer_Fasttrack.airlines2["C6"] = "CanJet";
Com_Flightexplorer_Fasttrack.airlines2["CX"] = "Cathy Pacific";
Com_Flightexplorer_Fasttrack.airlines2["MU"] = "China Eastern Airlines";
Com_Flightexplorer_Fasttrack.airlines2["CZ"] = "China Southern Airlines";
Com_Flightexplorer_Fasttrack.airlines2["CO"] = "Continental Airlines";
Com_Flightexplorer_Fasttrack.airlines2["DL"] = "Delta Airlines";
Com_Flightexplorer_Fasttrack.airlines2["BR"] = "EVA Airways";
Com_Flightexplorer_Fasttrack.airlines2["LY"] = "El Al Israel Airlines";
Com_Flightexplorer_Fasttrack.airlines2["AY"] = "Finnair";
Com_Flightexplorer_Fasttrack.airlines2["RF"] = "Florida West Airlines";
Com_Flightexplorer_Fasttrack.airlines2["F9"] = "Frontier Airlines";
Com_Flightexplorer_Fasttrack.airlines2["GA"] = "Garuda";
Com_Flightexplorer_Fasttrack.airlines2["HQ"] = "Harmony Airways";
Com_Flightexplorer_Fasttrack.airlines2["HA"] = "Hawaiian Airlines";
Com_Flightexplorer_Fasttrack.airlines2["IB"] = "Iberia Airlines";
Com_Flightexplorer_Fasttrack.airlines2["FI"] = "Icelandair";
Com_Flightexplorer_Fasttrack.airlines2["IC"] = "Indian Airlines";
Com_Flightexplorer_Fasttrack.airlines2["IR"] = "Iran Air";
Com_Flightexplorer_Fasttrack.airlines2["JD"] = "Japan Air System";
Com_Flightexplorer_Fasttrack.airlines2["JL"] = "Japan Airlines";
Com_Flightexplorer_Fasttrack.airlines2["QJ"] = "Jet Airways";
Com_Flightexplorer_Fasttrack.airlines2["B6"] = "JetBlue Airways";
Com_Flightexplorer_Fasttrack.airlines2["KL"] = "KLM Royal Dutch Airlines";
Com_Flightexplorer_Fasttrack.airlines2["KE"] = "Korean Air Lines";
Com_Flightexplorer_Fasttrack.airlines2["WJ"] = "Labrador Airways LTD";
Com_Flightexplorer_Fasttrack.airlines2["LH"] = "Lufthansa";
Com_Flightexplorer_Fasttrack.airlines2["MY"] = "MAXjet";
Com_Flightexplorer_Fasttrack.airlines2["MH"] = "Malaysian Airline";
Com_Flightexplorer_Fasttrack.airlines2["YV"] = "Mesa Airlines";
Com_Flightexplorer_Fasttrack.airlines2["MX"] = "Mexicana";
Com_Flightexplorer_Fasttrack.airlines2["GL"] = "Miami Air Intl.";
Com_Flightexplorer_Fasttrack.airlines2["YX"] = "Midwest Airlines";
Com_Flightexplorer_Fasttrack.airlines2["NW"] = "Northwest Airlines";
Com_Flightexplorer_Fasttrack.airlines2["OA"] = "Olympic Airways";
Com_Flightexplorer_Fasttrack.airlines2["PR"] = "Philippine Airlines";
Com_Flightexplorer_Fasttrack.airlines2["PO"] = "Polar Air";
Com_Flightexplorer_Fasttrack.airlines2["QF"] = "Qantas Airways";
Com_Flightexplorer_Fasttrack.airlines2["SN"] = "Sabena";
Com_Flightexplorer_Fasttrack.airlines2["S6"] = "Salmon Air";
Com_Flightexplorer_Fasttrack.airlines2["SV"] = "Saudi Arabian Airlines";
Com_Flightexplorer_Fasttrack.airlines2["SK"] = "Scandinavian Airlines (SAS)";
Com_Flightexplorer_Fasttrack.airlines2["YR"] = "Scenic Airlines";
Com_Flightexplorer_Fasttrack.airlines2["SQ"] = "Singapore Airlines";
Com_Flightexplorer_Fasttrack.airlines2["SA"] = "South African Airways";
Com_Flightexplorer_Fasttrack.airlines2["WN"] = "Southwest Airlines";
Com_Flightexplorer_Fasttrack.airlines2["JK"] = "Spanair";
Com_Flightexplorer_Fasttrack.airlines2["NK"] = "Spirit Airlines";
Com_Flightexplorer_Fasttrack.airlines2["SY"] = "Sun Country Airlines";
Com_Flightexplorer_Fasttrack.airlines2["LX"] = "Swiss Int'l Airllines";
Com_Flightexplorer_Fasttrack.airlines2["TG"] = "Thai Airways";
Com_Flightexplorer_Fasttrack.airlines2["TK"] = "Turkish Airlines";
Com_Flightexplorer_Fasttrack.airlines2["US"] = "US Airways";
Com_Flightexplorer_Fasttrack.airlines2["UA"] = "United Airlines";
Com_Flightexplorer_Fasttrack.airlines2["VP"] = "VASP";
Com_Flightexplorer_Fasttrack.airlines2["RG"] = "Varig";
Com_Flightexplorer_Fasttrack.airlines2["VS"] = "Virgin Atlantic";
Com_Flightexplorer_Fasttrack.airlines2["WS"] = "WestJet Airlines";
Com_Flightexplorer_Fasttrack.airlines2["MF"] = "Xiamen Airlines";

Com_Flightexplorer_Fasttrack.mapIATA2ICAO = new Object();
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["EI"] = "EIN";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["AC"] = "ACA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["AF"] = "AFR";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["GB"] = "ABX";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["AS"] = "ASA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["AZ"] = "AZA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["AQ"] = "AAH";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["HP"] = "AWE";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["AA"] = "AAL";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["BA"] = "BAW";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["CO"] = "COA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["DL"] = "DAL";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["F9"] = "FFT";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["HA"] = "HAL";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["IB"] = "IBE";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["B6"] = "JBU";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["KL"] = "KLM";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["LH"] = "DLH";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["YV"] = "ASH";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["NW"] = "NWA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["US"] = "USA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["UA"] = "UAL";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["VS"] = "VIR";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["WS"] = "WJA";
Com_Flightexplorer_Fasttrack.mapIATA2ICAO["NK"] = "NKS";

Com_Flightexplorer_Fasttrack.airwww = new Object();
Com_Flightexplorer_Fasttrack.airwww["AirTran Airways"] = "http://www.airtran.com/";
Com_Flightexplorer_Fasttrack.airwww["American Airlines"] = "http://www.aa.com";
Com_Flightexplorer_Fasttrack.airwww["America West"] = "http://www.americawest.com/";
Com_Flightexplorer_Fasttrack.airwww["ATA Airlines"] = "http://www.ata.com/";
Com_Flightexplorer_Fasttrack.airwww["Alaska Airlines"] = "http://www.alaskaair.com/";
Com_Flightexplorer_Fasttrack.airwww["Alaska Airlines"] = "http://www.alaskaair.com/";
Com_Flightexplorer_Fasttrack.airwww["Aloha Airlines"] = "http://www.alohaairlines.com/";
Com_Flightexplorer_Fasttrack.airwww["Air Canada"] = "http://www.aircanada.com";
Com_Flightexplorer_Fasttrack.airwww["Air France"] = "http://www.airfrance.com";
Com_Flightexplorer_Fasttrack.airwww["Delta Airlines"] = "http://www.delta.com";
Com_Flightexplorer_Fasttrack.airwww["Continental Airlines"] = "http://www.continental.com/";
Com_Flightexplorer_Fasttrack.airwww["Frontier Airlines"] = "http://www.frontierairlines.com/";
Com_Flightexplorer_Fasttrack.airwww["Hawaiian Airlines"] = "http://www.hawaiianair.com";
Com_Flightexplorer_Fasttrack.airwww["Northwest Airlines"] = "http://www.nwa.com";
Com_Flightexplorer_Fasttrack.airwww["British Airways"] = "http://www.britishairways.com";
Com_Flightexplorer_Fasttrack.airwww["JetBlue Airways"] = "http://www.jetblue.com/";
Com_Flightexplorer_Fasttrack.airwww["United Airlines"] = "http://www.united.com";
Com_Flightexplorer_Fasttrack.airwww["US Airways"] = "http://www.usairways.com/";

Com_Flightexplorer_Fasttrack.airphones = new Object();
Com_Flightexplorer_Fasttrack.airphones["Adria Airways"] = "49-69-25730";
Com_Flightexplorer_Fasttrack.airphones["Aero California"] = "800-237-6225";
Com_Flightexplorer_Fasttrack.airphones["Aeroflot"] = "888-340-6400";
Com_Flightexplorer_Fasttrack.airphones["Aero Lineas Argentinas"] = "800-333-0276";
Com_Flightexplorer_Fasttrack.airphones["Aer Lingus"] = "888-474-7424";
Com_Flightexplorer_Fasttrack.airphones["Aero Mexico"] = "800-237-6639";
Com_Flightexplorer_Fasttrack.airphones["Air Afrique"] = "800-456-9192";
Com_Flightexplorer_Fasttrack.airphones["Air Aruba"] = "800-882-7822";
Com_Flightexplorer_Fasttrack.airphones["Air Canada"] = "888-247-2262";
Com_Flightexplorer_Fasttrack.airphones["Air Caribbean"] = "809-623-2500";
Com_Flightexplorer_Fasttrack.airphones["Air Europa"] = "888-238-7672";
Com_Flightexplorer_Fasttrack.airphones["Air Fiji"] = "800-677-4277";
Com_Flightexplorer_Fasttrack.airphones["Air France"] = "800-237-2747";
Com_Flightexplorer_Fasttrack.airphones["Air Jamaica"] = "800-523-5585";
Com_Flightexplorer_Fasttrack.airphones["Air Lanka"] = "800-247-5265";
Com_Flightexplorer_Fasttrack.airphones["Air New Zealand"] = "800-262-1234";
Com_Flightexplorer_Fasttrack.airphones["Air Pacific"] = "800-227-4446";
Com_Flightexplorer_Fasttrack.airphones["Air Ukraine"] = "800-857-2463";
Com_Flightexplorer_Fasttrack.airphones["Alaska Airlines"] = "800-426-0333";
Com_Flightexplorer_Fasttrack.airphones["Alitalia"] = "800-223-5730";
Com_Flightexplorer_Fasttrack.airphones["All Nippon Airways"] = "800-235-9262";
Com_Flightexplorer_Fasttrack.airphones["Aloha Airlines"] = "800-227-4900";
Com_Flightexplorer_Fasttrack.airphones["America West"] = "800-235-9292";
Com_Flightexplorer_Fasttrack.airphones["American Airlines"] = "800-433-7300";
Com_Flightexplorer_Fasttrack.airphones["Asiana Airlines"] = "800-227-4262";
Com_Flightexplorer_Fasttrack.airphones["Austrian Airlines"] = "800-843-0002";
Com_Flightexplorer_Fasttrack.airphones["Avianca Airlines"] = "800-284-2622";
Com_Flightexplorer_Fasttrack.airphones["Bearskin Airlines"] = "800-465-2327";
Com_Flightexplorer_Fasttrack.airphones["British Airways"] = "800-247-9297";
Com_Flightexplorer_Fasttrack.airphones["British Midland Airways"] = "800-788-0555";
Com_Flightexplorer_Fasttrack.airphones["Cathay Pacific Airlines"] = "800 233 2742"
Com_Flightexplorer_Fasttrack.airphones["China Airlines"] = "800-227-5118";
Com_Flightexplorer_Fasttrack.airphones["China Southern Airlines"] = "888-338-8988";
Com_Flightexplorer_Fasttrack.airphones["Continental Airlines"] = "800-525-0280";
Com_Flightexplorer_Fasttrack.airphones["Copa Airlines"] = "800-892-2672";
Com_Flightexplorer_Fasttrack.airphones["Croatian Airlines"] = "800-247-5353";
Com_Flightexplorer_Fasttrack.airphones["Czech Airlines"] = "800-223-2365";
Com_Flightexplorer_Fasttrack.airphones["Delta Airlines"] = "800-221-1212";
Com_Flightexplorer_Fasttrack.airphones["El Al Israel Airlines"] = "800-223-6700";
Com_Flightexplorer_Fasttrack.airphones["Emirates"] = "800-777-3999";
Com_Flightexplorer_Fasttrack.airphones["Ethiopian Air"] = "800-445-2733";
Com_Flightexplorer_Fasttrack.airphones["Finnair"] = "800-950-5000";
Com_Flightexplorer_Fasttrack.airphones["First Air"] = "800-267-1247";
Com_Flightexplorer_Fasttrack.airphones["Frontier Airlines"] = "800-432-1359";
Com_Flightexplorer_Fasttrack.airphones["Gulf Air"] = "800-553-2824";
Com_Flightexplorer_Fasttrack.airphones["Hawaiian Airlines"] = "800-367-5320";
Com_Flightexplorer_Fasttrack.airphones["Horizon Air"] = "800-554-2924";
Com_Flightexplorer_Fasttrack.airphones["Iberia Airlines"] = "800-772-4642";
Com_Flightexplorer_Fasttrack.airphones["Icelandair"] = "800-223-5500";
Com_Flightexplorer_Fasttrack.airphones["Japan Airlines"] = "800-525-3663";
Com_Flightexplorer_Fasttrack.airphones["JetBlue Airways"] = "800-538-2583";
Com_Flightexplorer_Fasttrack.airphones["Kenya Airways"] = "800-343-2506";
Com_Flightexplorer_Fasttrack.airphones["KLM Royal Dutch Airlines"] = "800-374-7747";
Com_Flightexplorer_Fasttrack.airphones["Korean Airlines"] = "800-438-5000";
Com_Flightexplorer_Fasttrack.airphones["Lacsa"] = "800-225-2272";
Com_Flightexplorer_Fasttrack.airphones["LAN Chile Airlines"] = "866-435-9526";
Com_Flightexplorer_Fasttrack.airphones["Lauda Air"] = "800-645-3880";
Com_Flightexplorer_Fasttrack.airphones["LOT Polish Airways"] = "800-223-0593";
Com_Flightexplorer_Fasttrack.airphones["Lufthansa"] = "800-645-3880";
Com_Flightexplorer_Fasttrack.airphones["Malaysia Airlines"] = "800-421-8641";
Com_Flightexplorer_Fasttrack.airphones["Malev Hungarian"] = "800-223-6884";
Com_Flightexplorer_Fasttrack.airphones["Mexicana Airlines"] = "800-531-7921";
Com_Flightexplorer_Fasttrack.airphones["Midwest Express Airlines"] = "800-452-2022";
Com_Flightexplorer_Fasttrack.airphones["Northwest Airlines"] = "800-225-2525";
Com_Flightexplorer_Fasttrack.airphones["Philippine Airlines"] = "800-435-9725";
Com_Flightexplorer_Fasttrack.airphones["Polynesian Airlines"] = "800-223-4277";
Com_Flightexplorer_Fasttrack.airphones["Qantas"] = "800-227-4500";
Com_Flightexplorer_Fasttrack.airphones["Royal Air Maroc"] = "800-344-6726";
Com_Flightexplorer_Fasttrack.airphones["Royal Jordanian"] = "800-223-0470";
Com_Flightexplorer_Fasttrack.airphones["Saudi Arabian Airlines"] = "800-472-8342";
Com_Flightexplorer_Fasttrack.airphones["Scandinavian Airlines (SAS)"] = "800-221-2350";
Com_Flightexplorer_Fasttrack.airphones["Singapore Airlines"] = "800-742-3333";
Com_Flightexplorer_Fasttrack.airphones["South African Airways"] = "800-722-9675";
Com_Flightexplorer_Fasttrack.airphones["Southwest Airlines"] = "800-435-9792";
Com_Flightexplorer_Fasttrack.airphones["Spanair"] = "888-545-5757";
Com_Flightexplorer_Fasttrack.airphones["Spirit Airlines"] = "800-772-7117";
Com_Flightexplorer_Fasttrack.airphones["TACA International"] = "800-535-8780";
Com_Flightexplorer_Fasttrack.airphones["Tap Air Portugal"] = "800-221-7370";
Com_Flightexplorer_Fasttrack.airphones["Thai Airways"] = "800-426-5204";
Com_Flightexplorer_Fasttrack.airphones["Ukraine Intl Airlines"] = "800-876-0114";
Com_Flightexplorer_Fasttrack.airphones["United Airlines"] = "800-241-6522";
Com_Flightexplorer_Fasttrack.airphones["US Airways"] = "800-428-4322";
Com_Flightexplorer_Fasttrack.airphones["Varig Brazilian Airlines"] = "800-468-2744";
Com_Flightexplorer_Fasttrack.airphones["Vasp"] = "800-723-8277";
Com_Flightexplorer_Fasttrack.airphones["Virgin Atlantic Airways"] = "800-862-8621";
Com_Flightexplorer_Fasttrack.airphones["WestJet Airlines"] = "800-538-5696";
Com_Flightexplorer_Fasttrack.airphones["Yugoslav Airlines"] = "800-752-6528";
Com_Flightexplorer_Fasttrack.airphones["Zambia Airways"] = "800-223-1136";

Com_Flightexplorer_Fasttrack.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var airlineName = "";
	var match0, match1, match5;
	var len = matchContext.length-2;
	if(matchContext[0]) 
		match0 = String(matchContext[0]).replace(/[\s\n\t\)\(\]\[\}\{.,\-]+/g, "");;
	if(matchContext[1])
		match1 = String(matchContext[1]).replace(/[\s\n\t\)\(\]\[\}\{.,\-]+/g, "");
	if(matchContext[len])
		match5 = String(matchContext[len]).replace(/[\s\n\t\)\(\]\[\}\{.,\-]+/g, "");
	
	if(Com_Flightexplorer_Fasttrack.airlines3[match1]) {
		airlineName = Com_Flightexplorer_Fasttrack.airlines3[match1];
	} else if(Com_Flightexplorer_Fasttrack.airlines2[match1]) {
		airlineName = Com_Flightexplorer_Fasttrack.airlines2[match1];	
	} else {
		airlineName = match1;
	}
	canvas.innerHTML = "Click to find the status of the " + airlineName + " flight " + match5;
};

Com_Flightexplorer_Fasttrack.prototype.doubleClicked =
function () {
	this.singleClicked();
}

Com_Flightexplorer_Fasttrack.prototype.menuItemSelected = 
function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
			this.createPropertyEditor();
		break;
   }
}


Com_Flightexplorer_Fasttrack.prototype.add2CAL = 
function (menuItemId, flightData,flightCode) {
	var appt = null;
	var startDate = new Date();
	var endDate = new Date();
	var notes = "";
	var durationStr = "";	
	var name = "";
	if(menuItemId=="ADD2CAL") {
		if(flightData) {
			startDate = ((new Date(flightData.EDT)) > (new Date(flightData.PDT))) ? (new Date(flightData.PDT)) : (new Date(flightData.EDT));
			try {
				if(flightData.Duration) {
					durationMS = flightData.Duration;
					var hours =  Math.floor(durationMS / AjxDateUtil.MSEC_PER_HOUR);		
					var minutes =  Math.floor((durationMS-hours*AjxDateUtil.MSEC_PER_HOUR) / 60000);
						
					if(hours) {
						durationStr += hours +" hours ";
					}
					if(minutes) {
						durationStr += minutes +" minutes";
					}
				}
			} catch (ex) {
				//
			}
		
			var departureNotes = ["Flight ", flightCode, " from ",flightData.OriginLocation," to ", flightData.DestinationLocation, "\nDuration: ",durationStr, "\nDeparture information:\n Airport: ", flightData.OriginName, "\n City: ", flightData.OriginLocation].join("");
			var notesPDT = "";
			var notesEDT = "";			
			var notesPTA = "";			
			var notesETA = "";			
			var notesDepTemp = "";						
			var notesDepWeath = "";									
			var notesArrTemp = "";	
			var notesArrWeath = "";			
		
			if(flightData.PDT)
				notesPDT = ["\n Scheduled departure (local time): ", flightData.PDT].join("");
				
			if(flightData.EDT)
				notesEDT = ["\n Estimated departure (local time): ", flightData.EDT].join("");

			if(flightData.OriginTemp) {
				notesDepTemp = ["\n Temperature at origin: ", flightData.OriginTemp, " F"].join("");
			}
			if(flightData.OriginWeatherDesc) {
				notesDepWeath = ["\n Weather at origin: ", flightData.OriginWeatherDesc].join("");
			}

			
			var arrivalNotes = ["\n\nArrival information:\n Airport: ", flightData.DestinationName, "\n City: ", flightData.DestinationLocation].join("");
			if(flightData.PTA)
				notesPTA = ["\n Scheduled arrival (local time): ", flightData.PTA].join("");	
		
			if(flightData.ETA)
				notesETA = ["\n Estimated arrival (local time): ", flightData.ETA].join("");	
			
			if(flightData.DestinationTemp) {
				notesArrTemp = ["\n Temperature at destination: ", flightData.DestinationTemp, " F"].join("");
			}
			if(flightData.DestinationWeatherDesc) {
				notesArrWeath = ["\n Weather at destination: ", flightData.DestinationWeatherDesc].join("");
			}
			try {
				if(flightData.Duration) {
					endDate = new Date(startDate.getTime()+flightData.Duration);			
				} else {
					endDate = (new Date(flightData.ETA)) > (new Date(flightData.PTA)) ? (new Date(flightData.PTA)) : (new Date(flightData.ETA));
				}
			} catch (ex) {
			 //
			}
			notes = [departureNotes,notesPDT,notesEDT,notesDepTemp,notesDepWeath,arrivalNotes,notesPTA,notesETA,notesArrTemp,notesArrWeath].join("");
		}
		name = ["Flight ", flightCode].join("");		
	} 
	appt = this.createAppointment(startDate);
	appt.setEndDate(AjxDateUtil.roundTimeMins(endDate,5));
	if(notes) {
		appt.notesTopPart = new ZmMimePart();
		appt.notesTopPart.setContentType(ZmMimeTable.TEXT_PLAIN);
		appt.notesTopPart.setContent(notes);
	}
	if(name)
		appt.setName(name);

	//AjxDispatcher.run("GetApptComposeController").show(appt);				
	AjxDispatcher.run("GetCalController")._showQuickAddDialog(appt, false);
}

Com_Flightexplorer_Fasttrack.prototype.createAppointment = 
function(startDate) {
	var mins = startDate.getMinutes();
	if(mins > 5) {
		mins = (Math.floor( (mins/5) )) * 5;
		startDate.setMinutes(mins);
	} else {
		startDate.setMinutes(0);
	}
	var newAppt = new ZmAppt();
	newAppt.setStartDate(startDate);

	newAppt.setEndDate(newAppt.getStartTime() + ZmCalViewController.DEFAULT_APPOINTMENT_DURATION);
	newAppt.resetRepeatWeeklyDays();
	newAppt.resetRepeatMonthlyDayList();
	newAppt.resetRepeatYearlyMonthsList(startDate.getMonth());
	newAppt.resetRepeatCustomDayOfWeek();
	return newAppt;
}

Com_Flightexplorer_Fasttrack.prototype.singleClicked =
function () {
	if(!this.fastrackDlg)
		this.fastrackDlg =  new FlightStatusDlg(this.getShell(), null, this);
		
	this.fastrackDlg.setZimlet(this);
	this.fastrackDlg.popup();
}

Com_Flightexplorer_Fasttrack.prototype.clicked =
function(spanElement, contentObjText, matchContext, canvas) {
	var airlineName = "N/A";
	var match0, match1, match5;
	var len = matchContext.length-2;
	if(matchContext[0]) 
		match0 = String(matchContext[0]).replace(/[\s\n\t\)\(\]\[\}\{.,\-]+/g, "");;
	if(matchContext[1])
		match1 = String(matchContext[1]).replace(/[\s\n\t\)\(\]\[\}\{.,\-]+/g, "");
	if(matchContext[len])
		match5 = String(matchContext[len]).replace(/[\s\n\t\)\(\]\[\}\{.,\-]+/g, "");
	
	if(Com_Flightexplorer_Fasttrack.airlines3[match1]) {
		airlineName = Com_Flightexplorer_Fasttrack.airlines3[match1];
	} else if(Com_Flightexplorer_Fasttrack.airlines2[match1]) {
		airlineName = Com_Flightexplorer_Fasttrack.airlines2[match1];	
	} else {
		airlineName = match1;
	}
	
	if(!this.fastrackDlg)
		this.fastrackDlg =  new FlightStatusDlg(this.getShell(), null, this)
		
	this.fastrackDlg.setZimlet(this);
	this.fastrackDlg.popup();
		
	this.fastrackDlg.setFlight(match0,airlineName,match1,match5);
};


Com_Flightexplorer_Fasttrack.prototype.getFlightDataAndImage = 
function(callback,code,ariline) {
	var password = password = this.getUserProperty("password");
	if(!password)
		password = this._zimletContext.getConfig("password");
			
	var username = this.getUserProperty("username");	
	if(!username)
		username = this._zimletContext.getConfig("username");	

	var getUrl = this._zimletContext.getConfig("GetFlightDataAndImageEx");
	var feUrl = [getUrl,"?Userid=",username,"&Password=",password,"&ACID=",code,
	"&Alias=Aircraft&ScratchPad=Zimlet&ImageFlags=111111&ImageWidth=300&ImageHeight=300"].join("");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(feUrl);
	AjxRpc.invoke(null, url, null, new AjxCallback(this, this.dataCallback, [callback]), true);
}

Com_Flightexplorer_Fasttrack.prototype.getFlightData = 
function(code) {
	var password = password = this.getUserProperty("password");
	if(!password)
		this._zimletContext.getConfig("password");
			
	var username = this.getUserProperty("username");	
	if(!username)
		username = this._zimletContext.getConfig("username");	
	

	var getUrl = this._zimletContext.getConfig("GetFlightDataEx");
	var feUrl = [getUrl,"?Userid=",username,"&Password=",password,"&ACID=",code].join("");
	var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(feUrl);
	var result = AjxRpc.invoke(null, url, null, null,true);
	if (!result.success) {
		return null;
	}
	if (!result.xml || !result.xml.documentElement) {	
		return null;
	}
	var flightInfo = this.parseResponse(result);
	return flightInfo;
}

Com_Flightexplorer_Fasttrack.prototype.parseResponse = 
function(result) {
	var resp;
	var resArray = new Array();	
	var fStatus;
	var fOrigin;
	var fDestination;
	var fImagePath;
	var fEDT;
	var fETA;
	var fPDT;
	var fPTA;
	var fOriginName;
	var fOriginTime;	
	var fOriginTemp;
	var fOriginLocation;
	var fOriginWeatherDesc;
	var fDestinationName;
	var fDestinationTime;
	var fDestinationLocation;
	var fDestinationTemp;
	var fDestinationWeatherDesc;
	var fAircraftName;
	var fAirlineName;		
	if (AjxEnv.isIE) { 
		resp = result.xml.documentElement;	
		var childNodes = result.xml.documentElement.childNodes;
		var cnt = childNodes.length;
		for(var ix =0;ix<cnt;ix++) {
			var child = childNodes[ix];
			if(child && child.firstChild && child.nodeName) {
				switch(child.nodeName) {
					case "Status":
						fStatus  = child.firstChild.nodeValue;
					break;
					case "Origin":
						fOrigin  = child.firstChild.nodeValue;
					break;
					case "Destination":
						fDestination  = child.firstChild.nodeValue;
					break;	
					case "OriginName":
						fOriginName  = child.firstChild.nodeValue;
					break;		
					case "DestinationName":
						fDestinationName  = child.firstChild.nodeValue;
					break;			
					case "OriginLocation":
						fOriginLocation  = child.firstChild.nodeValue;
					break;		
					case "DestinationLocation":
						fDestinationLocation  = child.firstChild.nodeValue;
					break;	
					case "ImagePath":
						fImagePath  = child.firstChild.nodeValue;
					break;			
					case "EDT":
						fEDT  = child.firstChild.nodeValue;
					break;
					case "PDT":
						fPDT  = child.firstChild.nodeValue;
					break;						
					case "ETA":
						fETA  = child.firstChild.nodeValue;
					break;	
					case "PTA":
						fPTA  = child.firstChild.nodeValue;
					break;		
					case "AircraftName":
						fAircraftName  = child.firstChild.nodeValue;
					break;		
					case "AirlineName":
						fAirlineName  = child.firstChild.nodeValue;
					break;		
					case "OriginTime":
						fOriginTime  = child.firstChild.nodeValue;
					break;	
					case "DestinationTime":
						fDestinationTime  = child.firstChild.nodeValue;
					break;	
					case "OriginTemp":
						fOriginTemp  = child.firstChild.nodeValue;
					break;												
					case "DestinationTemp":
						fDestinationTemp  = child.firstChild.nodeValue;
					break;	
					case "OriginWeatherDesc":
						fOriginWeatherDesc = child.firstChild.nodeValue;
					break;	
					case "DestinationWeatherDesc":
						fDestinationWeatherDesc = child.firstChild.nodeValue;
					break;																																																																			
				}
			}
		} 		
	} else {
		resp = result.xml;
		var nodes = resp.getElementsByTagName("Status");			
		if(nodes && nodes.length) 
			fStatus  = nodes[0].textContent;

		nodes = resp.getElementsByTagName("Origin");						
		if(nodes && nodes.length)
			fOrigin = nodes[0].textContent;
		 						
		nodes = resp.getElementsByTagName("Destination");									
		if(nodes && nodes.length)			
			fDestination = nodes[0].textContent;

		nodes = resp.getElementsByTagName("OriginName");						
		if(nodes && nodes.length)
			fOriginName = nodes[0].textContent;
		 						
		nodes = resp.getElementsByTagName("DestinationName");									
		if(nodes && nodes.length)			
			fDestinationName = nodes[0].textContent;
			
		nodes = resp.getElementsByTagName("OriginLocation");						
		if(nodes && nodes.length)
			fOriginLocation = nodes[0].textContent;
		 						
		nodes = resp.getElementsByTagName("DestinationLocation");									
		if(nodes && nodes.length)			
			fDestinationLocation = nodes[0].textContent;
			
		nodes = resp.getElementsByTagName("ImagePath");															
		if(nodes && nodes.length)													
			fImagePath = nodes[0].textContent;
			
		nodes = resp.getElementsByTagName("EDT");															
		if(nodes && nodes.length)													
			fEDT = nodes[0].textContent;		

		nodes = resp.getElementsByTagName("PDT");															
		if(nodes && nodes.length)													
			fPDT = nodes[0].textContent;		

		nodes = resp.getElementsByTagName("ETA");															
		if(nodes && nodes.length)													
			fETA = nodes[0].textContent;						

		nodes = resp.getElementsByTagName("PTA");															
		if(nodes && nodes.length)													
			fPTA = nodes[0].textContent;						

		nodes = resp.getElementsByTagName("AircraftName");			
		if(nodes && nodes.length)
			fAircraftName  = nodes[0].textContent;

		nodes = resp.getElementsByTagName("AirlineName");			
		if(nodes && nodes.length)
			fAirlineName = nodes[0].textContent;
		
		nodes = resp.getElementsByTagName("OriginTime");			
		if(nodes && nodes.length)
			fOriginTime = nodes[0].textContent;			
			
		nodes = resp.getElementsByTagName("DestinationTime");			
		if(nodes && nodes.length)
			fDestinationTime = nodes[0].textContent;			

		nodes = resp.getElementsByTagName("OriginTemp");			
		if(nodes && nodes.length)
			fOriginTemp = nodes[0].textContent;			

		nodes = resp.getElementsByTagName("DestinationTemp");			
		if(nodes && nodes.length)
			fDestinationTemp = nodes[0].textContent;	
			
		nodes = resp.getElementsByTagName("OriginWeatherDesc");			
		if(nodes && nodes.length)
			fOriginWeatherDesc = nodes[0].textContent;			
			
		nodes = resp.getElementsByTagName("DestinationWeatherDesc");			
		if(nodes && nodes.length)
			fDestinationWeatherDesc = nodes[0].textContent;			
	}
	
	var startDate = ((new Date(fEDT)) > (new Date(fPDT))) ? (new Date(fPDT)) : (new Date(fEDT));
	var endDate = (new Date(fETA)) > (new Date(fPTA)) ? (new Date(fPTA)) : (new Date(fETA));
	if(fOriginTime && fDestinationTime) {
		var originLocalTime = new Date(fOriginTime);
		var destLocalTime = new Date(fDestinationTime);
		var endDateMils = endDate.valueOf();
		if(originLocalTime > destLocalTime) {
			var timeDiff =  originLocalTime.getTime() - destLocalTime.getTime();
			endDateMils += 	timeDiff;
		} else {
			var timeDiff = destLocalTime.getTime() - originLocalTime.getTime();
			endDateMils -= 	timeDiff;
		}
		endDate = new Date(endDateMils);
	}
	var fDuration = endDate.getTime() - startDate.getTime();	
	return {Status:fStatus,Origin:fOrigin,Destination:fDestination,
	ImagePath:fImagePath,EDT:fEDT,ETA:fETA,PDT:fPDT,PTA:fPTA,OriginName:fOriginName,
	DestinationName:fDestinationName,DestinationLocation:fDestinationLocation,
	OriginLocation:fOriginLocation,AircraftName:fAircraftName,AirlineName:fAirlineName,
	OriginTime:fOriginTime,DestinationTime:fDestinationTime,OriginTemp:fOriginTemp,
	DestinationTemp:fDestinationTemp,OriginWeatherDesc:fOriginWeatherDesc,DestinationWeatherDesc:fDestinationWeatherDesc,Duration:fDuration};
}

Com_Flightexplorer_Fasttrack.prototype.dataCallback = 
function(callback, result) {
	if (!result.success) {
		DBG.println("!result.success text:" + result.text);
		if(callback)
			callback.run();
		this.getShell().setBusy(false);
        return;
    }
	var flightInfo = null;
	try {
		if (!result.xml || !result.xml.documentElement) {
			DBG.println("!result.xml");
			if(callback)
				callback.run();
			this.getShell().setBusy(false);
		} else {
			flightInfo = this.parseResponse(result);
			//result.xml.getElementsByTagName("country")[0].attributes.name.value
			//process the response
//			var responseNode = resp.getElementsByTagName("Response");
		}
	} catch (ex) {
		//DBG.println(AjxDebug.DBG1, ex.dump());
		if(callback)
			callback.run();
		this.getShell().setBusy(false);
		//canvas.innerHTML = "<div><b>XML parsing resulted in error.</b></div>";
		return;
	}
	if(flightInfo && callback) {
		callback.run(flightInfo);	
	}
	appCtxt.getShell().setBusy(false);
}



