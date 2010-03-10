Run 'ant package' - This would package the extensions into jar files inside build dir.


httphandler example
-------------------

1. Copy zimbra-extns-httphandler.jar to /opt/zimbra/lib/ext/httpHandlerExtn dir and restart server.

2. Browse to http://localhost:7070/service/extension/dummyHandler


soapservice example
-------------------

1. Copy zimbra-extns-soapservice.jar to /opt/zimbra/lib/ext/soapServiceExtn dir and restart server.

2. Execute:

   $ curl -d "<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'><soap:Body><p:HelloWorldRequest xmlns:p='urn:zimbra:examples'><caller>Vishal</caller></p:HelloWorldRequest></soap:Body></soap:Envelope>" http://localhost:7070/service/soap

   Expected response:

   <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"><soap:Header><context xmlns="urn:zimbra"/></soap:Header><soap:Body><HelloWorldResponse xmlns="urn:zimbra:examples"><reply>Hello Vishal!</reply></HelloWorldResponse></soap:Body></soap:Envelope>


customauth example
------------------

1. Copy zimbra-extns-customauth.jar to /opt/zimbra/lib/ext/customAuthExtn dir.

2. Create a file /opt/zimbra/conf/users.xml containing allow username/password pairs in the following format:

   <users>
       <user name="user1@<domain_name>" password="pass1"/>
       <user name="user2@<domain_name>" password="pass2"/>
   </users>

3. Execute:

   zmprov modifyDomain <domain_name> zimbraAuthMech custom:simple

4. Restart server and try logging-into the web UI. Authentication would now happen against the uses.xml file instead of ldap.


samlprovider example
--------------------

1. Copy zimbra-extns-samlprovider.jar to /opt/zimbra/lib/ext/samlprovider dir.

2. Copy conf/samlprovider/issued-saml-assertions.xml to /opt/zimbra/conf dir.
   Edit the value of <saml:NameID> element in this file.
   This file would be read by a dummy SAML authority (implemented as an extension HTTP handler) hosted at http://localhost:7070/service/extension/samlAuthority.

3. Execute:

   zmlocalconfig -e saml_authority_url=http://localhost:7070/service/extension/samlAuthority
   zmlocalconfig -e zimbra_auth_provider=SAML_AUTH_PROVIDER

4. Restart server.

5. Send the following no-op SOAP request to http://localhost:7070/service/soap and expect a good response:

   <soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'>
       <soap:Header>
           <context xmlns='urn:zimbra'>
               <authToken type='SAML_AUTH_PROVIDER'>b07b804c-7c29-ea16-7300-4f3d6f7928ac</authToken>
           </context>
       </soap:Header>
       <soap:Body>
           <NoOpRequest xmlns='urn:zimbraMail'/>
       </soap:Body>
   </soap:Envelope>

