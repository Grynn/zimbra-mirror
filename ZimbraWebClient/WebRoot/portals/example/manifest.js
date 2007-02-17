var Portal = {
	id		: "velodrome",
	cols	: [	
		{	width:200, 
			portlets: [
						{ zimlet:"xhr", prefId:"portlet_speed_contacts", height:"50%",
							outerTemplate:"portlet", title:"My Speed Contacts", icon:"ImgPerson",
							params: { 	url:"speed_contacts.jsp"	}
						},

						{ zimlet:"xhr", prefId:"portlet_velodrome_now", height:"50%",
							outerTemplate:"portlet", title:"Velodrome Now", icon:"ImgLogo",
							params: { 	url:"velodrome_now.html"	}
						}
			]
		},
		
		{	width:"*", 
			portlets: [
						{ zimlet:"xhr", prefId:"portlet_messages", height:"100%",
							outerTemplate:"portlet", title:"Messages", icon:"ImgMessage",
							params: { 	url:"messages.html"	}
						},
			]
		},

		{	width:200, 
			portlets: [
						{ zimlet:"rss", prefId:"portlet_news", height:"50%",
							outerTemplate:"portlet", title:"News", icon:"ImgRss",
							params: { 	feed:"cnn.com/..."	}
						},
					
						{ zimlet:"xhr", prefId:"portlet_safety", height:"50%",
							outerTemplate:"portlet", title:"Safety and Security Tips", icon:"ImgAuthenticate",
							params: { 	url:"safety_tips.html"	}
						}
			]
		}
	]
}
					
