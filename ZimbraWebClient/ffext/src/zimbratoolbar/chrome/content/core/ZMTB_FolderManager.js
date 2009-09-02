var ZMTB_FolderManager = function(requestManager)
{
	this._folders = {};
	this._listeners = [];
	this._filters = {"default":ZMTB_FolderManager._DEFAULTFILTER};
	this._rqManager = requestManager;
}
/*
Filter paramaters: 	first //Initial folders to add to list
					exclude //Folders to exclude from list
					type //Type of folders to include in list
					root //Whether to include root in list
					search //Whether to include search folders

*/
ZMTB_FolderManager._DEFAULTFILTER = {first:[2, 5, 6, 4], exclude:[3, 14], type:"message", search:true};

ZMTB_FolderManager.prototype.setFilter = function(name, filter)
{
	this._filters[name] = filter;
}

ZMTB_FolderManager.prototype.getFolders = function(filterName)
{
	var folders = [];
	var filter = this._filters[filterName];
	if(filter)
	{
		if(filter.root && this._folders[1])
			folders.push(this._folders[1]);
		if(filter.first)
		{
			for (var i=0; i < filter.first.length; i++)
				if(this._folders[filter.first[i]])
					folders.push(this._folders[filter.first[i]]);
		}
		for (var i in this._folders)
		{
			if(this._folders[i].parent==1)
			{
				var add = true;
				for (var j=0; j < folders.length; j++)
					if(this._folders[i].id == folders[j].id)
						add = false;
				if(add)
					folders.push(this._folders[i]);
			}
		}
		if(filter.exclude)
		{
			for (var i = folders.length - 1; i >= 0; i--)
				for (var j=0; j < filter.exclude.length; j++)
					if(folders[i].id == filter.exclude[j])
					{
						folders.splice(i, 1);
						j=filter.exclude.length; //exit loop
					}
		}
		this._addChildren(folders);
		if(filter.exclude)
		{
			for (var i = folders.length - 1; i >= 0; i--)
				for (var j=0; j < filter.exclude.length; j++)
					if(folders[i].id == filter.exclude[j])
					{
						folders.splice(i, 1);
						j=filter.exclude.length; //exit loop
					}
		}
		if(filter.type)
		{
			for (var i = folders.length - 1; i >= 0; i--)
				if(folders[i].view && folders[i].view != filter.type && !(filter.root && folders[i].id == 1))
					folders.splice(i, 1);
		}
		if(!filter.search)
		{
			for (var i = folders.length - 1; i >= 0; i--)
				if(folders[i].query)
					folders.splice(i, 1);			
		}
	}
	else
		for(var i in this._folders)
			folders.push(this._folders[i]);
			
	return folders;
}

ZMTB_FolderManager.prototype._addChildren = function(folderArray)
{
	for (var i=0; i < folderArray.length; i++) 
	{
		if(folderArray[i].id == 1) continue;
		var children = this._getChildren(folderArray[i].id);
		this._addChildren(children);
		for (var j=0; j < children.length; j++)
		{
			var add = true;
			for (var k=0; k < folderArray.length; k++)
			{
				if(folderArray[k].id == children[j].id)
					add=false;
			};
			if(add)
			{
				folderArray.splice(i+1, 0, children[j]);
				i+=1;
			}
		}
	}
}

ZMTB_FolderManager.prototype.receiveUpdate = function(responseObj)
{
	if(responseObj.code)
		return;
	else
	{
		if(responseObj.Header.context.notify)
		{
			for (var i=0; i < responseObj.Header.context.notify.length; i++)
			{
				var notify = responseObj.Header.context.notify[i];
				if(notify.modified && notify.modified.folder)
				{
					var fs = notify.modified.folder;
					for (var i=0; i < fs.length; i++)
					{
						if(this._folders[fs[i].id])
						{
							if(fs[i].l)
								this._folders[fs[i].id].parent = fs[i].l;
							if(fs[i].name)
								this._folders[fs[i].id].name = fs[i].name;
							if(fs[i].u)
								this._folders[fs[i].id].unread = fs[i].u;
						}
					};
					this._updateListeners();
				}
				if(notify.created && notify.created.folder)
				{
					this._addFolders(notify.created.folder);
					this._updateListeners();
				}
				if(notify.deleted && notify.deleted.id)
				{
					var ids = notify.deleted.id.split(",");
					for (var i=0; i < ids.length; i++)
						if(this._folders[ids[i]])
							delete this._folders[ids[i]];
					this._updateListeners();
				}
			}
		}
		if(responseObj.Body.GetFolderResponse)
		{
			this.reset();
			this._addFolders(responseObj.Body.GetFolderResponse.folder);
			this._updateListeners();
		}
		else if(responseObj.Header.context.refresh)
		{
			this._addFolders(responseObj.Header.context.refresh.folder);
			this._updateListeners();
		}
		else if(responseObj.Body.BatchResponse && responseObj.Body.BatchResponse.GetFolderResponse)
		{
			this.reset();
			for (var i=0; i < responseObj.Body.BatchResponse.GetFolderResponse.length; i++)
				this._addFolders(responseObj.Body.BatchResponse.GetFolderResponse[i].folder); //This is an array
			this._updateListeners();
		}
	}
}

ZMTB_FolderManager.prototype.reset = function()
{
	this._folders = {};
}

ZMTB_FolderManager.prototype.registerListener = function(listener)
{
	this._listeners.push(listener);
}

ZMTB_FolderManager.prototype.removeListener = function(listener)
{
	for (var i = this._listeners.length - 1; i >= 0; i--)
		if(this._listeners[i] == listener)
		{
			this._listeners.splice(i, 1);
			return;
		}
}

ZMTB_FolderManager.prototype._updateListeners = function()
{
	for (var i=0; i < this._listeners.length; i++)
		this._listeners[i].updateFolders();
}

//Recursive
ZMTB_FolderManager.prototype._addFolders = function(fs)
{
	for (var i=0; i < fs.length; i++)
	{
		this._folders[fs[i].id] = {id:fs[i].id, name:fs[i].name, parent:fs[i].l, unread:(fs[i].u?fs[i].u:0), view:fs[i].view};
		if(fs[i].url)
			this._folders[fs[i].id].rss = true;
		if(fs[i].query)
			this._folders[fs[i].id].query = fs[i].query;
		if(fs[i].search)
			this._addFolders(fs[i].search);
		if(fs[i].folder)
			this._addFolders(fs[i].folder);
	}
}


ZMTB_FolderManager.prototype.getFullPath = function(folderId, root)
{
	if(!this._folders[folderId])
		return "";
	if(this._folders[folderId].parent == 1 && !root)
		return this._folders[folderId].name;
	else if(folderId == 1)
		return root;
	if(this._folders[folderId].parent)
		return this.getFullPath(this._folders[folderId].parent, root)+"/"+this._folders[folderId].name;
	else
		return this._folders[folderId].name;
	
}

ZMTB_FolderManager.prototype.getDepth = function(folderId, root)
{
	if(!this._folders[folderId])
		return 0;
	else if(!this._folders[folderId].parent || (!root && this._folders[folderId].parent==1) || this._folders[folderId].id==1)
		return 0;
	return this.getDepth(this._folders[folderId].parent)+1;
}

ZMTB_FolderManager.prototype.getFolder = function(folderId)
{
	return this._folders[folderId];
}

ZMTB_FolderManager.prototype._getChildren = function(folderId)
{
	var children = [];
	for(var i in this._folders)
		if(this._folders[i].parent == folderId)
			children.push(this._folders[i]);
	return children;
}

ZMTB_FolderManager.prototype._getFolderByName = function(folderName)
{
	for(var i in this._folders)
		if(this._folders[i].name == folderName)
			return this._folders[i];
}

ZMTB_FolderManager.prototype._getFolderId = function(folderName)
{
	for(var i=0; i<this._folders.length; i++)
		if(this._folders[i].name == folderName)
			return this._folders[i].id;
}
