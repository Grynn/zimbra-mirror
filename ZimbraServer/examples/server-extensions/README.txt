Run 'ant package' - This would package the extensions into jar files inside build dir.


httphandler example
-------------------

Copy zimbra-extns-httphandler.jar to /opt/zimbra/lib/ext/httpHandlerExtn dir and restart server.

Browse to http://localhost:7070/service/extension/dummyHandler


soapservice example
-------------------

Copy zimbra-extns-soapservice.jar to /opt/zimbra/lib/ext/soapServiceExtn dir and restart server.

Execute:

$ curl -d "<soap:Envelope xmlns:soap='http://www.w3.org/2003/05/soap-envelope'><soap:Body><p:HelloWorldRequest xmlns:p='urn:zimbra:examples'><caller>Vishal</caller></p:HelloWorldRequest></soap:Body></soap:Envelope>" http://localhost:7070/service/soap

Expected response:

<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope"><soap:Header><context xmlns="urn:zimbra"/></soap:Header><soap:Body><HelloWorldResponse xmlns="urn:zimbra:examples"><reply>Hello Vishal!</reply></HelloWorldResponse></soap:Body></soap:Envelope>