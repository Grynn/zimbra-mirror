var ZMTB_TaskActions = function(zmtb)
{
	ZMTB_Actions.call(this, zmtb);
	zmtb.getRequestManager().addUpdateListener(this);
	
	var This = this;
	this._folderMan.setFilter("tasklists", {type:"task", exclude:[3]});
	//Menu items
	document.getElementById("ZimTB-NewTask").addEventListener("click",function(event){
		This.newTaskCommand();
		document.getElementById("ZimTB-NewTask-Subject").focus();
	},false);
	document.getElementById("ZimTB-ViewTasks").addEventListener("click",function(event){
		This._rqManager.goToPath("?app=tasks")
	},false);
	document.getElementById("ZimTB-NewTaskList").addEventListener("click",function(event){
		This.openActions("ZimTB-NewTaskList-Bar");
		document.getElementById("ZimTB-NewTaskList-Name").focus();
	},false);
	document.getElementById("ZimTB-NewRemTaskList").addEventListener("click",function(event){
		This.openActions("ZimTB-NewRemTaskList-Bar");
		document.getElementById("ZimTB-NewRemTaskList-Name").focus();
	},false);
	document.getElementById("ZimTB-SharedTaskList").addEventListener("click",function(event){
		This.openActions("ZimTB-LinkToTaskList-Bar");
		document.getElementById("ZimTB-LinkToTaskList-Name").focus();
	},false);
		
	//Tasks
	document.getElementById("ZimTB-NewTask-Create").addEventListener("command", function(){
		This.newTask(document.getElementById("ZimTB-NewTask-TaskList").selectedItem.value, document.getElementById("ZimTB-NewTask-Subject").value, document.getElementById("ZimTB-NewTask-Location").value, document.getElementById("ZimTB-NewTask-StartDate").dateValue, document.getElementById("ZimTB-NewTask-EndDate").dateValue);
		This.hideActions();
	}, false);
	document.getElementById("ZimTB-NewTask-Close").addEventListener("command", function(){
			This.hideActions();
			document.getElementById("ZimTB-NewTask-Subject").value = "";
			document.getElementById("ZimTB-NewTask-Location").value = "";
	}, false);
	
	//Link to Task List
	document.getElementById("ZimTB-LinkToTaskList-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-LinkToTaskList-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("taskaction_linkedtasklist_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-LinkToTaskList-Owner").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("taskaction_linkedtasklist_needowner"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-LinkToTaskList-Path").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("taskaction_linkedtasklist_needpath"), null, "failure");
			return;
		}
		This.newLinked(document.getElementById("ZimTB-LinkToTaskList-Name").value, "task", 1, document.getElementById("ZimTB-LinkToTaskList-Owner").value, document.getElementById("ZimTB-LinkToTaskList-Path").value)
		This.hideActions();
		document.getElementById("ZimTB-LinkToTaskList-Name").value="";
	}, false);
	document.getElementById("ZimTB-LinkToTaskList-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//New Task List
	document.getElementById("ZimTB-NewTaskList-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewTaskList-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("taskaction_newtasklist_needname"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewTaskList-Name").value, "task", 1);
		This.hideActions();
		document.getElementById("ZimTB-NewTaskList-Name").value="";
	}, false);
	document.getElementById("ZimTB-NewTaskList-Close").addEventListener("command", function(){This.hideActions()}, false);
	
	//Remote Task List
	document.getElementById("ZimTB-NewRemTaskList-Create").addEventListener("command", function(){
		if(document.getElementById("ZimTB-NewRemTaskList-Name").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("taskaction_newremtasklist_needname"), null, "failure");
			return;
		}
		else if(document.getElementById("ZimTB-NewRemTaskList-URL").value=="")
		{
			This._zmtb.notify(This._localstrings.getString("taskaction_newremtasklist_needurl"), null, "failure");
			return;
		}
		This.newFolder(document.getElementById("ZimTB-NewRemTaskList-Name").value, "task", 1, document.getElementById("ZimTB-NewRemTaskList-URL").value);
		This.hideActions();
		document.getElementById("ZimTB-NewRemTaskList-Name").value="";
		document.getElementById("ZimTB-NewRemTaskList-Name").URL="";
	}, false);
	document.getElementById("ZimTB-NewRemTaskList-Close").addEventListener("command", function(){This.hideActions()}, false);
}

ZMTB_TaskActions.prototype = new ZMTB_Actions();
ZMTB_TaskActions.prototype.constructor = ZMTB_TaskActions;

ZMTB_TaskActions.prototype.enable = function()
{
	document.getElementById("ZimTB-Tasks-Button").disabled = false;
}

ZMTB_TaskActions.prototype.disable = function()
{
	document.getElementById("ZimTB-Tasks-Button").disabled = true;
}

ZMTB_TaskActions.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.code)
		return;
	if(responseObj.Body.CreateTaskResponse)
		this._zmtb.notify(this._localstrings.getString("taskaction_newtask_success"), null, "success");
	if(responseObj.Body.CreateFolderResponse && responseObj.Body.CreateFolderResponse.folder)
	{
		for (var i=0; i < responseObj.Body.CreateFolderResponse.folder.length; i++)
		{
			if(responseObj.Body.CreateFolderResponse.folder[i].view == "task")
			{
				if(responseObj.Body.CreateFolderResponse.folder[i].url)
					this._zmtb.notify(this._localstrings.getString("taskaction_newremtasklist_success"), null, "success");
				else
					this._zmtb.notify(this._localstrings.getString("taskaction_newtasklist_success"), null, "success");
			}
		};
	}
	if(responseObj.Body.CreateMountpointResponse && responseObj.Body.CreateMountpointResponse.link)
	{
		for (var i=0; i < responseObj.Body.CreateMountpointResponse.link.length; i++)
			if(responseObj.Body.CreateMountpointResponse.link[i].view == "task")
				this._zmtb.notify(this._localstrings.getString("taskaction_linkedtasklist_success"), null, "success");
	}
}

ZMTB_TaskActions.prototype.reset = function() {}

ZMTB_TaskActions.prototype.newTaskCommand = function()
{
	this.openActions("ZimTB-NewTask-Bar");
	document.getElementById("ZimTB-NewTask-Subject").focus();
	this._populateList(document.getElementById("ZimTB-NewTask-TaskList"), this._folderMan.getFolders("tasklists"));
	for (var i=0; i < document.getElementById("ZimTB-NewTask-TaskList").itemCount; i++)
		if(document.getElementById("ZimTB-NewTask-TaskList").getItemAtIndex(i).getAttribute("value") == 15) //Default tasks folder ID
			document.getElementById("ZimTB-NewTask-TaskList").selectedIndex=i;
	var now = new Date();
	var sm = 1800000-now.getTime()%1800000+now.getTime();
	var start = new Date()
	start.setTime(sm);
	var end = new Date();
	end.setTime(1800000+start.getTime());
	document.getElementById("ZimTB-NewTask-StartDate").dateValue = start;
	document.getElementById("ZimTB-NewTask-EndDate").dateValue = end;
}

ZMTB_TaskActions.prototype.newTask = function(cal, subj, loc, startdate, enddate)
{
	var sd = ZMTB_AjxSoapDoc.create("CreateTaskRequest", ZMTB_RequestManager.NS_MAIL);
	var st = startdate.getUTCFullYear().toString()+((startdate.getUTCMonth()+1).toString().length==2?(startdate.getUTCMonth()+1):"0"+(startdate.getUTCMonth()+1)).toString()+(startdate.getUTCDate().toString().length==2?startdate.getUTCDate():"0"+startdate.getUTCDate()).toString();
	var et = enddate.getUTCFullYear().toString()+((enddate.getUTCMonth()+1).toString().length==2?(enddate.getUTCMonth()+1):"0"+(enddate.getUTCMonth()+1)).toString()+(enddate.getUTCDate().toString().length==2?enddate.getUTCDate():"0"+enddate.getUTCDate()).toString();
	var m =  {
				"l":cal, 
				"su":subj,
			 	"inv":{ 
					"comp":{"name":subj, 
							"loc":loc, 
							"s":{"d":st}, 
							"e":{"d":et}
							}
						}
			};
	sd.set("m", m);
	this._rqManager.sendRequest(sd);
	this._rqManager.updateAll();
	
}