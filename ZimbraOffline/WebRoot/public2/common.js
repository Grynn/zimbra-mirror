
		var serviceFieldsToHide = [
			"accountTypeRow",
			"receivingMailRow",
			"usernameRow",
			"mailServerRow",
			"mailSecureRow",
			"sendingMailRow",
			"smtpServerRow",
			"smtpSecureRow",
			"smtpAuthRow",
			"smtp_auth_settings_row",
			"replyToRow"
		];

		function showYahooPage() {
			Ajax.showPanel("newService");
			showServiceSettings({
				protocol_pop:true,
				protocol_imap:false,
				server_host:"pop.mail.yahoo.com",
				server_secure:true,
				server_port:995,
				smtp_host:"smtp.mail.yahoo.com",
				smtp_secure:true,
				smtp_port:465,
				smtp_auth:true,
				service_hint:'',
				from_display:'',
				password:'',
				sync_interval:5,
				email:"@yahoo.com",
				email_hint:"include the '@yahoo.com'",
				service:"Yahoo! Mail",
				folder_name_display:"Yahoo! Mail",
				pop_folder_new:true,
				pageTitle:"Yahoo! Mail Plus Account Setup",
				instructions:"Enter your full name and your Yahoo! Mail Plus email and password below.",
				settings_hint:"<a id='toggleSettings' href='javascript:toggleServiceSettings()'>Show advanced settings<\/a>"
			}, serviceFieldsToHide, "from_display");
		}
		
		function showGmailPage() {
			Ajax.showPanel("newService");
			showServiceSettings({
				protocol_pop:false,
				protocol_imap:true,
				server_host:"imap.gmail.com",
				server_secure:true,
				server_port:993,
				smtp_host:"smtp.gmail.com",
				smtp_secure:true,
				smtp_port:465,
				smtp_auth:true,
				service_hint:'',
				from_display:'',
				password:'',
				sync_interval:5,
				email:"@gmail.com",
				email_hint:"inclde the '@gmail.com'",
				username:"@gmail.com",
				username_hint:"inclde the '@gmail.com'",
				service:"Gmail Account",
				pageTitle:"Gmail Account Setup",
				instructions:"Enter your full name and your Gmail email and password below.",
				settings_hint:"<a id='toggleSettings' href='javascript:toggleServiceSettings()'>Show advanced settings<\/a>"
			}, serviceFieldsToHide, "from_display");
		}
		
		function showHotmailPage() {
			Ajax.showPanel("newService");
			showServiceSettings({
				protocol_pop:true,
				protocol_imap:false,
				server_host:"pop3.live.com",
				server_secure:true,
				server_port:995,
				smtp_host:"smtp.live.com",
				smtp_secure:true,
				smtp_port:25,
				smtp_auth:true,
				service_hint:'',
				from_display:'',
				password:'',
				sync_interval:5,
				email:"@hotmail.com",
				email_hint:"inclde the '@hotmail.com'",
				username_hint:"inclde the '@hotmail.com'",
				service:"Hotmail Account",
				pageTitle:"Windows Live Hotmail Plus Account Setup",
				instructions:"Enter your full name and your Hotmail Plus email and password below.",
				settings_hint:"<a id='toggleSettings' href='javascript:toggleServiceSettings()'>Show advanced settings<\/a>"
			}, serviceFieldsToHide, "from_display");
		}
		
		function showSetupPage() {
			Ajax.showPanel("console");
		}

		function showServiceSettings(settings, elementsToHide, focusField) {
			Ajax.setHash(settings);
			Ajax.hideList(elementsToHide);
			Ajax.focusIn(focusField);
			window.hiddenFormElements = elementsToHide;
		}
		
		function toggleServiceSettings() {
			if (!Ajax.isShown(serviceFieldsToHide[0])) {
				Ajax.hideList(serviceFieldsToHide);
				Ajax.set("toggleSettings", "Show advanced settings");
			} else {
				Ajax.showList(serviceFieldsToHide);
				Ajax.set("toggleSettings", "Hide advanced settings");
			}
		}
