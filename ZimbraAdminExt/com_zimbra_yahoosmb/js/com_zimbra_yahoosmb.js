/**
1. Define the components which will be exposed in the
        SMB Admin

**/
                                                    
//TODO: Must make sure it is loaded after domainadmin.js is loaded.
if (AjxEnv.hasFirebug) console.log("Start loading com_zimbra_yahoosmb.js");
function ZaSMB () {} ;

//we need a way to set the value
//need to set some values in the SMBAccount.js 
//Yahoo SMB Admin = admin + yahoo smb features (for internal yahoo smb admin use)
ZaSettings.isYahooSmbAdmin = (ZaSettings.isYahooSMB && !ZaSettings.isDomainAdmin) ;
ZaSettings.isYahooSmbPAAdmin = (ZaSettings.isYahooPA && !ZaSettings.isDomainAdmin)
//Yahoo SMB Domain Admin = domain admin + yahoo smb features (for yahoo smb customer domain admin)
ZaSettings.isYahooSmbDomainAdmin = (ZaSettings.isYahooSMB && ZaSettings.isDomainAdmin) ;
//Yahoo SMB PA user which should be decided by LDAP Attributes
ZaSettings.isYahooSmbPADomainAdmin = (ZaSettings.isYahooPA && ZaSettings.isDomainAdmin) ;

/**
 * Modify ZaSettings to load extra module for SMB admin
 */
if(ZaSettings) {
	ZaSMB.initSettings = function() {
        if (AjxEnv.hasFirebug) console.log("com_zimbra_yahoosmb.js is modifying ZaSettings");
        if (ZaSettings.isYahooSmbPADomainAdmin) {
            ZaSettings.isYahooSmbAdmin = false ;
            ZaSettings.isYahooSmbDomainAdmin = true ;

            ZaSettings.ACCOUNTS_CHPWD_ENABLED = false;
            ZaSettings.ACCOUNTS_INTEROP_ENABLED = false ;

            ZaSettings.ACCOUNTS_FEATURES_ENABLED = true;
            ZaSettings.ACCOUNTS_RESTORE_ENABLED = true;
            ZaSettings.ACCOUNTS_PREFS_ENABLED = true;
            ZaSettings.ACCOUNTS_ADVANCED_ENABLED = true;

            ZaSettings.SYSTEM_CONFIG_ENABLED = false;   //disable the tree overview configuration section
            ZaSettings.GLOBAL_CONFIG_ENABLED = true;
            ZaSettings.COSES_ENABLED= true;
            ZaSettings.DOMAINS_ENABLED= true;
            ZaSettings.ZIMLETS_ENABLED = true ;                    

            ZaSettings.SKIN_PREFS_ENABLED = false;
            ZaSettings.ACCOUNTS_REINDEX_ENABLED = false;
            ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED = false;
            ZaSettings.RESOURCES_ENABLED = false ;
       } else if (ZaSettings.isYahooSmbDomainAdmin) {
            ZaSettings.isYahooSmbAdmin = false ;
            ZaSettings.isYahooSmbDomainAdmin = true ;

            ZaSettings.ACCOUNTS_INTEROP_ENABLED = false ;

            ZaSettings.ACCOUNTS_FEATURES_ENABLED = true;
            ZaSettings.ACCOUNTS_RESTORE_ENABLED = true;
            ZaSettings.ACCOUNTS_PREFS_ENABLED = true;
            ZaSettings.ACCOUNTS_ADVANCED_ENABLED = true;


            ZaSettings.SYSTEM_CONFIG_ENABLED = false;   //disable the tree overview configuration section
            ZaSettings.GLOBAL_CONFIG_ENABLED = true;
            ZaSettings.COSES_ENABLED= true;
            ZaSettings.DOMAINS_ENABLED= true;
            ZaSettings.ZIMLETS_ENABLED = true ;

            ZaSettings.SKIN_PREFS_ENABLED = false;
            ZaSettings.ACCOUNTS_REINDEX_ENABLED = false;
            ZaSettings.ACCOUNTS_VIEW_MAIL_ENABLED = false;
            ZaSettings.RESOURCES_ENABLED = true ;
       }else { //a super admin
           // ZaSettings.isYahooSmbAdmin = true ;
            //ZaSettings.isYahooSmbDomainAdmin = false ;
        }
    }

    if(ZaSettings.initMethods)
        ZaSettings.initMethods.push(ZaSMB.initSettings);
}



//-----------------------Override the Messages ------------------------
ZaMsg.NAD_UseCosSettings = com_zimbra_yahoosmb.NAD_UseCosSettings;
ZaMsg.NAD_ResetToCOS  = com_zimbra_yahoosmb.NAD_ResetToCOS

if (AjxEnv.hasFirebug) console.log("Loaded com_zimbra_yahoosmb.js");