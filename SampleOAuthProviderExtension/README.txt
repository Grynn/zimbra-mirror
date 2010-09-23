Requirement:
------------

#1,This sample app use OAuth java library(http://oauth.googlecode.com/svn/code/java/core/)
which was contributed by John Kristian, Praveen Alavilli and Dirk Balfanz.
So you have to build "oauth-1.4.jar" from the source code.



How to deploy:
--------------

-Step1, create "sampleoauthprov" directory under /opt/zimbra/lib/ext 
-Step2, put "oauth-1.4.jar" to the directory /opt/zimbra/lib and /opt/zimbra/
-Step3, put "authorize.jsp" file to /opt/zimbra/jetty/webapps/zimbra/public directory
-Step4, add localconfig below using zmlocalconfig:

oauth_consumerDescription_<consumer' key> = <consumer's description>
oauth_consumerKey_<consumer' key> = <consumer's key>
oauth_consumerSecret_<consumer' key> = <consumer's secret>

and modify zimbra_auth_provider like below: 

zimbra_auth_provider = zimbra,oauth

-Step5, restart zimbraStore zmmaiboxdctl restart

-Step6, make your consumer app get access to /service/extension/oatuh/req_token for request token,
and /service/extension/oauth/access_token for access token.
For authorization, use /service/extension/oatuh/authorization.



Limitation:
-----------

#1,OAuth OOB mode is not supported yet.
=> It is supported.(from v0.2)
#2,for single store server environment only
=> You can deploy this to multiple store server environment.(from v0.3)
#3,http request with oauth access token is accessible to all zimbra http api(SOAP,REST,,)
so consumer can get ZM_AUTH_TOKEN with oauth access token 
by using some api like SOAP AuthRequest, which I think could leads to security issue.
#4, OAuth nonce is not examined.


#1,#3,#4 will be removed from this limitation list hopefully in the near future.
For #2, I have some idea to make this app work also for multi store server environment.
But I am not sure if I will work on that.