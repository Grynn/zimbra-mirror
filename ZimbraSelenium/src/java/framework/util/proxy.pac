function FindProxyForURL(url, host)
{
// variable strings to return
var proxy_yes = "PROXY localhost:4444";
var proxy_no = "DIRECT";
if (shExpMatch(url, "*selenium-server*")) { return proxy_yes; }

// Dont Proxy anything else
return proxy_no;
}