
- Create "openid" directory under /opt/zimbra/lib/ext; copy "openid4java-0.9.5.jar" and "openidconsumer.jar" under it

- Copy "formredirection.jsp" file to /opt/zimbra/jetty/webapps/zimbra/public directory

- To initiate OpenID-based sign-in, or, to associate/link an "open-id" with a user's account (to enable OpenID-based
  sign-in in future) who is already logged-in into Zimbra:

  Browse to:

    <zimbra_host_base_url>/service/extension/openid/consumer?openid_identifier=<user-supplied-identifier>

    e.g.

    <zimbra_host_base_url>/service/extension/openid/consumer?openid_identifier=yahoo.com