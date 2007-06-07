
/**
 * @author Charles Cao
 * ZaRequestMgr: used to send the soap request to the server and handle the following events:
 * 		1) show the busy dialog when it is a synchronous call and takes long time
 */
ZaRequestMgr = function () {}

/**
 * 
 * @param csfeParams: the parameters used by ZmCsfeCommand to send the request to the server
 * @param params: other parameters used by the ZaRequestMgr. 
 * 			Typical parameter is the controller
 * 
 */
ZaRequestMgr.invoke = function (csfeParams, params) {
	var command = new ZmCsfeCommand();
	var controller = (params != null ? params.controller : null) ;
	//add the busy icon for the synchronous calls
	if (!csfeParams.asyncMode && controller) {
		controller._shell.setBusyDialogText(ZaMsg.splashScreenLoading);
		var cancelCallback = new AjxCallback(controller, controller.cancelBusyOverlay);
		controller._shell.setBusy(true, null, true, null, cancelCallback);
	}
	
	var response = command.invoke(csfeParams) ;
	
	if (!csfeParams.asyncMode && controller) {
		controller._shell.setBusy(false); //remove the busy overlay
	}
	if (! csfeParams.asyncMode)	{
		return 	response;
	}
		
	
}