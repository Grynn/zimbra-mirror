{
    "id": "example",
	"cols": [
        {   "width": 200,
			"portlets": [
                { "zimlet": "com_zimbra_url",
                  "height": "50%",
                  "title": "Google Search",
                  "icon": "Search",
                  "properties": {
                      "url": "http://google.com/pda"
                  }
                },
                { "zimlet": "com_zimbra_rss",
                  "height": "50%",
                  "title": "Google News",
                  "icon": "RSS",
                  "properties": {
                      "url": "http://news.google.com/?output=rss",
                      "refresh": 300000
                  }
                }
			]
		},
		{	"width": "*",
			"portlets": [
                { "zimlet": "com_zimbra_url",
                  "height": "100%",
                  "title": "Browser",
                  "icon": "Globe",
                  "properties": {
                      "url": "http://www.zimbra.com/"
                  }
                }
			]
		},
		{	"width": 200,
			"portlets": [
                { "zimlet": "com_zimbra_rss",
                  "height": "75%",
                  "title": "Yahoo! Tech News",
                  "icon": "RSS",
                  "properties": {
                      "url": "http://rss.news.yahoo.com/rss/tech",
                      "refresh": 300000
                  }
                },
                { "zimlet": "com_zimbra_rss",
                  "title": "Google News (Atom)",
                  "icon": "RSS",
                  "properties": {
                      "url": "http://news.google.com/?output=atom",
                      "refresh": 86400000
                  }
                }
			]
		}
	]
}
					
