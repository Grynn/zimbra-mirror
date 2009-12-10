ZaShare = function() {
	ZaItem.call(this, "ZaShare");
}
ZaShare.prototype = new ZaItem;
ZaShare.prototype.constructor = ZaShare;

ZaShare.A_ownerId = "ownerId";
ZaShare.A_ownerEmail = "ownerEmail";
ZaShare.A_ownerName = "ownerName";
ZaShare.A_folderId = "folderId";
ZaShare.A_folderPath = "folderPath";
ZaShare.A_granteeType = "granteeType";
ZaShare.A_granteeId = "granteeId";
ZaShare.A_granteeName = "granteeName";

ZaShare.prototype.initFromJS = 
function (share) {
	if(!share)
		return;
		
	this.name = share[ZaShare.A_folderPath];
	this.id = [share[ZaShare.A_ownerId], "_folder_", share[ZaShare.A_folderId]].join("");
	this[ZaShare.A_folderId] = share[ZaShare.A_folderId];
	this[ZaShare.A_ownerId] = share[ZaShare.A_ownerId];
	this[ZaShare.A_ownerEmail] = share[ZaShare.A_ownerEmail];
	this[ZaShare.A_ownerName] = share[ZaShare.A_ownerName] ? share[ZaShare.A_ownerName] : share[ZaShare.A_ownerEmail];
	this[ZaShare.A_folderPath] = share[ZaShare.A_folderPath];
	this[ZaShare.A_granteeType] = share[ZaShare.A_granteeType];
	this[ZaShare.A_granteeId] = share[ZaShare.A_granteeId];
	this[ZaShare.A_granteeName] = share[ZaShare.A_granteeName];	
}