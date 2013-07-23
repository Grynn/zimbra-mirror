/**
 * DO NOT EDIT! This file is generated.
 * <p>
 * Any copy of this file checked into source control is merely for
 * convenience and should not be edited in any way.
 * <p>
 * Generated at Sun Sep 25 14:19:30 PDT 2011
 * @private
 */
AjxTimezoneData = {};

AjxTimezoneData.TRANSITION_YEAR = 2011;

AjxTimezoneData.TIMEZONE_RULES = [
	{ serverId: "Etc/GMT+12", clientId: "Etc/GMT+12", score: 100,  standard: { offset: -720, tzname: "GMT+12" } },
	{ serverId: "Pacific/Midway", clientId: "Pacific/Midway", score: 100,  standard: { offset: -660, tzname: "SST" } },
	{ serverId: "Pacific/Honolulu", clientId: "Pacific/Honolulu", score: 200,  standard: { offset: -600, tzname: "HST" } },
	{ serverId: "America/Anchorage", clientId: "America/Anchorage", score: 200, 
	  standard: { offset: -540, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "AKST" },
	  daylight: { offset: -480, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "AKDT" }
	},
	{ serverId: "America/Los_Angeles", clientId: "America/Los_Angeles", score: 200, 
	  standard: { offset: -480, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "PST" },
	  daylight: { offset: -420, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "PDT" }
	},
	{ serverId: "America/Tijuana", clientId: "America/Tijuana", score: 100, 
	  standard: { offset: -480, mon: 10, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "PST" },
	  daylight: { offset: -420, mon: 4, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "PDT" }
	},
	{ serverId: "America/Chihuahua", clientId: "America/Chihuahua", score: 100, 
	  standard: { offset: -420, mon: 10, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "MST" },
	  daylight: { offset: -360, mon: 4, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "MDT" }
	},
	{ serverId: "America/Denver", clientId: "America/Denver", score: 200, 
	  standard: { offset: -420, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "MST" },
	  daylight: { offset: -360, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "MDT" }
	},
	{ serverId: "America/Phoenix", clientId: "America/Phoenix", score: 200,  standard: { offset: -420, tzname: "MST" } },
	{ serverId: "America/Chicago", clientId: "America/Chicago", score: 200, 
	  standard: { offset: -360, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "CST" },
	  daylight: { offset: -300, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "CDT" }
	},
	{ serverId: "America/Guatemala", clientId: "America/Guatemala", score: 100,  standard: { offset: -360 } },
	{ serverId: "America/Mexico_City", clientId: "America/Mexico_City", score: 100, 
	  standard: { offset: -360, mon: 10, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "CST" },
	  daylight: { offset: -300, mon: 4, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "CDT" }
	},
	{ serverId: "America/Regina", clientId: "America/Regina", score: 200,  standard: { offset: -360, tzname: "CST" } },
	{ serverId: "America/Bogota", clientId: "America/Bogota", score: 100,  standard: { offset: -300 } },
	{ serverId: "America/Indiana/Indianapolis", clientId: "America/Indiana/Indianapolis", score: 100, 
	  standard: { offset: -300, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "EST" },
	  daylight: { offset: -240, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "EDT" }
	},
	{ serverId: "America/New_York", clientId: "America/New_York", score: 200, 
	  standard: { offset: -300, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "EST" },
	  daylight: { offset: -240, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "EDT" }
	},
	{ serverId: "America/Caracas", clientId: "America/Caracas", score: 100,  standard: { offset: -270, tzname: "VET" } },
	{ serverId: "America/Asuncion", clientId: "America/Asuncion", score: 100, 
	  standard: { offset: -240, mon: 4, week: 2, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 4, 10 ], tzname: "PYT" },
	  daylight: { offset: -180, mon: 10, week: 1, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 10, 2 ], tzname: "PYST" }
	},
	{ serverId: "America/Cuiaba", clientId: "America/Cuiaba", score: 100, 
	  standard: { offset: -240, mon: 2, week: 3, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 2, 20 ], tzname: "AMT" },
	  daylight: { offset: -180, mon: 10, week: 3, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 10, 16 ], tzname: "AMST" }
	},
	{ serverId: "America/Guyana", clientId: "America/Guyana", score: 100,  standard: { offset: -240, tzname: "GYT" } },
	{ serverId: "America/Halifax", clientId: "America/Halifax", score: 100, 
	  standard: { offset: -240, mon: 11, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 11, 6 ], tzname: "AST" },
	  daylight: { offset: -180, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "ADT" }
	},
	{ serverId: "America/Santiago", clientId: "America/Santiago", score: 100, 
	  standard: { offset: -240, mon: 4, week: 1, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "CLT" },
	  daylight: { offset: -180, mon: 10, week: 2, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 10, 9 ], tzname: "CLST" }
	},
	{ serverId: "America/St_Johns", clientId: "America/St_Johns", score: 100, 
	  standard: { offset: -210, mon: 11, week: 1, wkday: 1, hour: 0, min: 1, sec: 0, trans: [ 2011, 11, 6 ], tzname: "NST" },
	  daylight: { offset: -150, mon: 3, week: 2, wkday: 1, hour: 0, min: 1, sec: 0, trans: [ 2011, 3, 13 ], tzname: "NDT" }
	},
	{ serverId: "America/Argentina/Buenos_Aires", clientId: "America/Argentina/Buenos_Aires", score: 100,  standard: { offset: -180 } },
	{ serverId: "America/Cayenne", clientId: "America/Cayenne", score: 100,  standard: { offset: -180, tzname: "GFT" } },
	{ serverId: "America/Godthab", clientId: "America/Godthab", score: 100, 
	  standard: { offset: -180, mon: 10, week: -1, wkday: 1, hour: 1, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "WGT" },
	  daylight: { offset: -120, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "WGST" }
	},
	{ serverId: "America/Montevideo", clientId: "America/Montevideo", score: 100, 
	  standard: { offset: -180, mon: 3, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 13 ], tzname: "UYT" },
	  daylight: { offset: -120, mon: 10, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 2 ], tzname: "UYST" }
	},
	{ serverId: "America/Sao_Paulo", clientId: "America/Sao_Paulo", score: 100, 
	  standard: { offset: -180, mon: 2, week: 3, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 2, 20 ], tzname: "BRT" },
	  daylight: { offset: -120, mon: 10, week: 3, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 10, 16 ], tzname: "BRST" }
	},
	{ serverId: "Atlantic/South_Georgia", clientId: "Atlantic/South_Georgia", score: 100,  standard: { offset: -120, tzname: "GST" } },
	{ serverId: "Atlantic/Azores", clientId: "Atlantic/Azores", score: 100, 
	  standard: { offset: -60, mon: 10, week: -1, wkday: 1, hour: 1, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "AZOT" },
	  daylight: { offset: 0, mon: 3, week: -1, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "AZOST" }
	},
	{ serverId: "Atlantic/Cape_Verde", clientId: "Atlantic/Cape_Verde", score: 100,  standard: { offset: -60, tzname: "CVT" } },
	{ serverId: "Africa/Casablanca", clientId: "Africa/Casablanca", score: 100, 
	  standard: { offset: 0, mon: 8, week: 2, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 8, 14 ], tzname: "WET" },
	  daylight: { offset: 60, mon: 5, week: 1, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 5, 1 ], tzname: "WEST" }
	},
	{ serverId: "Africa/Monrovia", clientId: "Africa/Monrovia", score: 100,  standard: { offset: 0, tzname: "GMT" } },
	{ serverId: "Europe/London", clientId: "Europe/London", score: 100, 
	  standard: { offset: 0, mon: 10, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "GMT/BST" },
	  daylight: { offset: 60, mon: 3, week: -1, wkday: 1, hour: 1, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "GMT/BST" }
	},
	{ serverId: "UTC", clientId: "UTC", score: 100,  standard: { offset: 0, tzname: "UTC" } },
	{ serverId: "Africa/Algiers", clientId: "Africa/Algiers", score: 100,  standard: { offset: 60, tzname: "CET" } },
	{ serverId: "Africa/Windhoek", clientId: "Africa/Windhoek", score: 100, 
	  standard: { offset: 60, mon: 4, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "WAT" },
	  daylight: { offset: 120, mon: 9, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 9, 4 ], tzname: "WAST" }
	},
	{ serverId: "Europe/Belgrade", clientId: "Europe/Belgrade", score: 100, 
	  standard: { offset: 60, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "CET" },
	  daylight: { offset: 120, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "CEST" }
	},
	{ serverId: "Europe/Berlin", clientId: "Europe/Berlin", score: 200, 
	  standard: { offset: 60, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "CET" },
	  daylight: { offset: 120, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "CEST" }
	},
	{ serverId: "Europe/Brussels", clientId: "Europe/Brussels", score: 100, 
	  standard: { offset: 60, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "CET" },
	  daylight: { offset: 120, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "CEST" }
	},
	{ serverId: "Europe/Warsaw", clientId: "Europe/Warsaw", score: 100, 
	  standard: { offset: 60, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "CET" },
	  daylight: { offset: 120, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "CEST" }
	},
	{ serverId: "Africa/Cairo", clientId: "Africa/Cairo", score: 100, 
	  standard: { offset: 120, mon: 9, week: -1, wkday: 5, hour: 0, min: 0, sec: 0, trans: [ 2011, 9, 29 ], tzname: "EET" },
	  daylight: { offset: 180, mon: 4, week: -1, wkday: 6, hour: 0, min: 0, sec: 0, trans: [ 2011, 4, 29 ], tzname: "EEST" }
	},
	{ serverId: "Africa/Harare", clientId: "Africa/Harare", score: 100,  standard: { offset: 120, tzname: "CAT" } },
	{ serverId: "Asia/Amman", clientId: "Asia/Amman", score: 100, 
	  standard: { offset: 120, mon: 10, week: -1, wkday: 6, hour: 1, min: 0, sec: 0, trans: [ 2011, 10, 28 ], tzname: "EET" },
	  daylight: { offset: 180, mon: 3, week: -1, wkday: 5, hour: 23, min: 59, sec: 59, trans: [ 2011, 3, 31 ], tzname: "EEST" }
	},
	{ serverId: "Asia/Beirut", clientId: "Asia/Beirut", score: 100, 
	  standard: { offset: 120, mon: 10, week: -1, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "EET" },
	  daylight: { offset: 180, mon: 3, week: -1, wkday: 1, hour: 0, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "EEST" }
	},
	{ serverId: "Asia/Jerusalem", clientId: "Asia/Jerusalem", score: 100, 
	  standard: { offset: 120, mon: 9, week: 2, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 9, 11 ], tzname: "IST" },
	  daylight: { offset: 180, mon: 3, week: -1, wkday: 6, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 25 ], tzname: "IDT" }
	},
	{ serverId: "Europe/Athens", clientId: "Europe/Athens", score: 200, 
	  standard: { offset: 120, mon: 10, week: -1, wkday: 1, hour: 4, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "EET" },
	  daylight: { offset: 180, mon: 3, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "EEST" }
	},
	{ serverId: "Europe/Helsinki", clientId: "Europe/Helsinki", score: 100, 
	  standard: { offset: 120, mon: 10, week: -1, wkday: 1, hour: 4, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "EET" },
	  daylight: { offset: 180, mon: 3, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "EEST" }
	},
	{ serverId: "Europe/Minsk", clientId: "Europe/Minsk", score: 100, 
	  standard: { offset: 120, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "EET" },
	  daylight: { offset: 180, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "EEST" }
	},
	{ serverId: "Africa/Nairobi", clientId: "Africa/Nairobi", score: 200,  standard: { offset: 180, tzname: "EAT" } },
	{ serverId: "Asia/Baghdad", clientId: "Asia/Baghdad", score: 100,  standard: { offset: 180 } },
	{ serverId: "Asia/Kuwait", clientId: "Asia/Kuwait", score: 100,  standard: { offset: 180, tzname: "AST" } },
	{ serverId: "Europe/Moscow", clientId: "Europe/Moscow", score: 100, 
	  standard: { offset: 180, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "MSK/MSD" },
	  daylight: { offset: 240, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "MSK/MSD" }
	},
	{ serverId: "Asia/Tehran", clientId: "Asia/Tehran", score: 100, 
	  standard: { offset: 210, mon: 9, week: 4, wkday: 4, hour: 0, min: 0, sec: 0, trans: [ 2011, 9, 28 ], tzname: "IRST" },
	  daylight: { offset: 270, mon: 3, week: 4, wkday: 2, hour: 0, min: 0, sec: 0, trans: [ 2011, 3, 28 ], tzname: "IRDT" }
	},
	{ serverId: "Asia/Baku", clientId: "Asia/Baku", score: 100, 
	  standard: { offset: 240, mon: 10, week: -1, wkday: 1, hour: 5, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "AZT" },
	  daylight: { offset: 300, mon: 3, week: -1, wkday: 1, hour: 4, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "AZST" }
	},
	{ serverId: "Asia/Muscat", clientId: "Asia/Muscat", score: 100,  standard: { offset: 240, tzname: "GST" } },
	{ serverId: "Asia/Tbilisi", clientId: "Asia/Tbilisi", score: 200,  standard: { offset: 240, tzname: "GET" } },
	{ serverId: "Asia/Yerevan", clientId: "Asia/Yerevan", score: 100, 
	  standard: { offset: 240, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "AMT" },
	  daylight: { offset: 300, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "AMST" }
	},
	{ serverId: "Indian/Mauritius", clientId: "Indian/Mauritius", score: 100,  standard: { offset: 240 } },
	{ serverId: "Asia/Kabul", clientId: "Asia/Kabul", score: 100,  standard: { offset: 270, tzname: "AFT" } },
	{ serverId: "Asia/Karachi", clientId: "Asia/Karachi", score: 200,  standard: { offset: 300 } },
	{ serverId: "Asia/Tashkent", clientId: "Asia/Tashkent", score: 100,  standard: { offset: 300, tzname: "UZT" } },
	{ serverId: "Asia/Yekaterinburg", clientId: "Asia/Yekaterinburg", score: 100, 
	  standard: { offset: 300, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "YEKT" },
	  daylight: { offset: 360, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "YEKST" }
	},
	{ serverId: "Asia/Colombo", clientId: "Asia/Colombo", score: 100,  standard: { offset: 330, tzname: "IST" } },
	{ serverId: "Asia/Kolkata", clientId: "Asia/Kolkata", score: 200,  standard: { offset: 330, tzname: "IST" } },
	{ serverId: "Asia/Almaty", clientId: "Asia/Almaty", score: 100,  standard: { offset: 360, tzname: "ALMT" } },
	{ serverId: "Asia/Dhaka", clientId: "Asia/Dhaka", score: 100,  standard: { offset: 360 } },
	{ serverId: "Asia/Novosibirsk", clientId: "Asia/Novosibirsk", score: 100, 
	  standard: { offset: 360, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "NOVT" },
	  daylight: { offset: 420, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "NOVST" }
	},
	{ serverId: "Asia/Rangoon", clientId: "Asia/Rangoon", score: 100,  standard: { offset: 390, tzname: "MMT" } },
	{ serverId: "Asia/Bangkok", clientId: "Asia/Bangkok", score: 100,  standard: { offset: 420, tzname: "ICT" } },
	{ serverId: "Asia/Krasnoyarsk", clientId: "Asia/Krasnoyarsk", score: 100, 
	  standard: { offset: 420, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "KRAT" },
	  daylight: { offset: 480, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "KRAST" }
	},
	{ serverId: "Asia/Hong_Kong", clientId: "Asia/Hong_Kong", score: 200,  standard: { offset: 480 } },
	{ serverId: "Asia/Irkutsk", clientId: "Asia/Irkutsk", score: 100, 
	  standard: { offset: 480, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "IRKT" },
	  daylight: { offset: 540, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "IRKST" }
	},
	{ serverId: "Asia/Kuala_Lumpur", clientId: "Asia/Kuala_Lumpur", score: 100,  standard: { offset: 480, tzname: "MYT" } },
	{ serverId: "Asia/Taipei", clientId: "Asia/Taipei", score: 100,  standard: { offset: 480 } },
	{ serverId: "Asia/Ulaanbaatar", clientId: "Asia/Ulaanbaatar", score: 100,  standard: { offset: 480 } },
	{ serverId: "Australia/Perth", clientId: "Australia/Perth", score: 100,  standard: { offset: 480, tzname: "WST" } },
	{ serverId: "Asia/Seoul", clientId: "Asia/Seoul", score: 100,  standard: { offset: 540 } },
	{ serverId: "Asia/Tokyo", clientId: "Asia/Tokyo", score: 200,  standard: { offset: 540 } },
	{ serverId: "Asia/Yakutsk", clientId: "Asia/Yakutsk", score: 100, 
	  standard: { offset: 540, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "YAKT" },
	  daylight: { offset: 600, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "YAKST" }
	},
	{ serverId: "Australia/Adelaide", clientId: "Australia/Adelaide", score: 100, 
	  standard: { offset: 570, mon: 4, week: 1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "CST" },
	  daylight: { offset: 630, mon: 10, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 2 ], tzname: "CST" }
	},
	{ serverId: "Australia/Darwin", clientId: "Australia/Darwin", score: 100,  standard: { offset: 570, tzname: "CST" } },
	{ serverId: "Asia/Vladivostok", clientId: "Asia/Vladivostok", score: 100, 
	  standard: { offset: 600, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "VLAT" },
	  daylight: { offset: 660, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "VLAST" }
	},
	{ serverId: "Australia/Brisbane", clientId: "Australia/Brisbane", score: 200,  standard: { offset: 600, tzname: "EST" } },
	{ serverId: "Australia/Hobart", clientId: "Australia/Hobart", score: 100, 
	  standard: { offset: 600, mon: 4, week: 1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "EST" },
	  daylight: { offset: 660, mon: 10, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 2 ], tzname: "EST" }
	},
	{ serverId: "Australia/Sydney", clientId: "Australia/Sydney", score: 200, 
	  standard: { offset: 600, mon: 4, week: 1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "EST" },
	  daylight: { offset: 660, mon: 10, week: 1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 2 ], tzname: "EST" }
	},
	{ serverId: "Pacific/Guam", clientId: "Pacific/Guam", score: 100,  standard: { offset: 600, tzname: "ChST" } },
	{ serverId: "Asia/Magadan", clientId: "Asia/Magadan", score: 100,  standard: { offset: 720, tzname: "MAGT" } },
	{ serverId: "Asia/Kamchatka", clientId: "Asia/Kamchatka", score: 100, 
	  standard: { offset: 720, mon: 10, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 10, 30 ], tzname: "PETT" },
	  daylight: { offset: 780, mon: 3, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "PETST" }
	},
	{ serverId: "Pacific/Auckland", clientId: "Pacific/Auckland", score: 100, 
	  standard: { offset: 720, mon: 4, week: 1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 4, 3 ], tzname: "NZST" },
	  daylight: { offset: 780, mon: 9, week: -1, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 9, 25 ], tzname: "NZDT" }
	},
	{ serverId: "Pacific/Fiji", clientId: "Pacific/Fiji", score: 100, 
	  standard: { offset: 720, mon: 3, week: -1, wkday: 1, hour: 3, min: 0, sec: 0, trans: [ 2011, 3, 27 ], tzname: "FJT" },
	  daylight: { offset: 780, mon: 10, week: 4, wkday: 1, hour: 2, min: 0, sec: 0, trans: [ 2011, 10, 23 ], tzname: "FJST" }
	},
	{ serverId: "Pacific/Tongatapu", clientId: "Pacific/Tongatapu", score: 100,  standard: { offset: 780 } }
];
