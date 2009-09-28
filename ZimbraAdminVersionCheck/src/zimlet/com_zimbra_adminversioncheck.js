if (AjxEnv.hasFirebug) console.log("Start loading com_zimbra_adminversioncheck.js");
function ZaVersionCheck() {
	ZaItem.call(this,"ZaVersionCheck");
	this.attrs = new Object();
};

ZaOperation.VERSION_CHECK = ++ZA_OP_INDEX;

//constants
ZaVersionCheck.A_zimbraVersionCheckLastAttempt = "zimbraVersionCheckLastAttempt";
ZaVersionCheck.A_zimbraVersionCheckLastSuccess = "zimbraVersionCheckLastSuccess";
//ZaVersionCheck.A_zimbraVersionCheckLastResponse = "zimbraVersionCheckLastResponse";
ZaVersionCheck.A_zimbraVersionCheckNotificationEmail = "zimbraVersionCheckNotificationEmail";
ZaVersionCheck.A_zimbraVersionCheckInterval = "zimbraVersionCheckInterval";
ZaVersionCheck.A_zimbraVersionCheckServer = "zimbraVersionCheckServer";
ZaVersionCheck.A_zimbraVersionCheckURL = "zimbraVersionCheckURL";
ZaVersionCheck.A_zimbraVersionCheckUpdates = "updates";
ZaVersionCheck.A_zimbraVersionCheckUpdateType = "type";
ZaVersionCheck.A_zimbraVersionCheckUpdateCritical = "critical";
ZaVersionCheck.A_zimbraVersionCheckUpdateVersion = "version";
ZaVersionCheck.A_zimbraVersionCheckUpdateBuildtype = "buildtype";
ZaVersionCheck.A_zimbraVersionCheckUpdateUpdateURL = "updateURL";
ZaVersionCheck.A_zimbraVersionCheckUpdateDescription = "description";
ZaVersionCheck.A_zimbraVersionCheckUpdateShortversion = "shortversion";

ZaVersionCheck.myXModel = {	items:[
	{id:ZaVersionCheck.A_zimbraVersionCheckLastAttempt, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckLastAttempt, type: _DATETIME_},
    {id:ZaVersionCheck.A_zimbraVersionCheckLastSuccess, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckLastSuccess, type: _DATETIME_},
    {id:ZaVersionCheck.A_zimbraVersionCheckNotificationEmail, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckNotificationEmail, type: _STRING_},
    {id:ZaVersionCheck.A_zimbraVersionCheckServer, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckServer, type: _STRING_},
	{id:ZaVersionCheck.A_zimbraVersionCheckURL, ref:"attrs/" + ZaVersionCheck.A_zimbraVersionCheckURL, type: _STRING_},
	{id:ZaVersionCheck.A_zimbraVersionCheckUpdates, type:_LIST_, listItem:
		{type:_OBJECT_, 
			items: [
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateType, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateCritical, type:_ENUM_, choices: ZaModel.BOOLEAN_CHOICES},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateVersion, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateBuildtype, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateUpdateURL, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateDescription, type:_STRING_},
				{id:ZaVersionCheck.A_zimbraVersionCheckUpdateShortversion, type:_STRING_}
			]
		}
	}
]};
