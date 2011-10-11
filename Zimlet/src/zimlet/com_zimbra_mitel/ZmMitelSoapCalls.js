function MitelSoapCalls() {
}

MitelSoapCalls.prototype.getDestinationsXML = function() {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mod=\"http://www.mitel.com/ucs/ws/model\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<mod:GetDestinations/>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getAccountInfoXML = function() {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mod=\"http://www.mitel.com/ucs/ws/model\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<mod:GetAccountInfo/>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getPhysicalDeviceInformationXML = function(deviceId) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ed4=\"http://www.ecma-international.org/standards/ecma-323/csta/ed4\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<ed4:GetPhysicalDeviceInformation>"
			, "<ed4:device>", deviceId, "</ed4:device>"
			, "</ed4:GetPhysicalDeviceInformation>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getMakeCallXML = function(fromPhoneNumber, toPhoneNumber) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ed4=\"http://www.ecma-international.org/standards/ecma-323/csta/ed4\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<ed4:MakeCall>"
			, "<ed4:callingDevice>", fromPhoneNumber, "</ed4:callingDevice>"
			, "<ed4:calledDirectoryNumber>", toPhoneNumber, "</ed4:calledDirectoryNumber>"
			, "</ed4:MakeCall>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getCallLogsXML = function(type) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mod=\"http://www.mitel.com/ucs/ws/model\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<mod:GetCallLogs>"
			, "<mod:type>", type, "</mod:type>"
			, "</mod:GetCallLogs>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getStartSessionRequestXML = function(mailboxId, pin) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:StartSessionRequest>"
			, "<Mailbox>", mailboxId, "</Mailbox>"
			, "<Pin>", pin, "</Pin>"
			, "</urn:StartSessionRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getMessagesRequestXML = function(sessionHandle) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:GetMessagesRequest>"
			, "<SessionHandle>", sessionHandle, "</SessionHandle>"
			, "</urn:GetMessagesRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getMessageUrlRequestXML = function(sessionHandle, recordingId) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:GetMessageUrlRequest>"
			, "<SessionHandle>", sessionHandle, "</SessionHandle>"
			, "<RecordingID>", recordingId, "</RecordingID>"
			, "</urn:GetMessageUrlRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getEndSessionRequestXML = function (sessionHandle) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:NpViewService\">"
			, "<soapenv:Header/>"
			, "<soapenv:Body>"
			, "<urn:EndSessionRequest>"
			, "<SessionHandle>", sessionHandle, "</SessionHandle>"
			, "</urn:EndSessionRequest>"
			, "</soapenv:Body>"
			, "</soapenv:Envelope>");

	return html.join("");
};

MitelSoapCalls.prototype.getClearConnectionXML = function (physicalDeviceId) {
	var html = [];
	html.push("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ed4=\"http://www.ecma-international.org/standards/ecma-323/csta/ed4\">"
   	, "<soapenv:Header/>"
	, "<soapenv:Body>"
    , "<ed4:ClearConnection>"
	, "<ed4:connectionToBeCleared>"
    , "<ed4:callID>" ,physicalDeviceId ,"</ed4:callID>"
    , "</ed4:connectionToBeCleared>"
    ,  "</ed4:ClearConnection>"
    , "</soapenv:Body>"
	, "</soapenv:Envelope>");

	return html.join("")
};