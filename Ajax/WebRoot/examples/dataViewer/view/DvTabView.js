/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


function DvTabView(parent, attrs, user, app) {

    DwtTabViewPage.call(this, parent);

	this._user = user;
	this._app = app;
	this._listView = new DvListView(this, attrs, app);
}

DvTabView.prototype = new DwtTabViewPage;
DvTabView.prototype.constructor = DvTabView;

DvTabView.prototype.toString = 
function() {
	return "DvTabView";
}

DvTabView.prototype.showMe =
function() {
	DwtTabViewPage.prototype.showMe.call(this);
	this._app.setCurrentUser(this._user);
}


DvTabView.prototype.resetSize = 
function(newWidth, newHeight)  {
	DwtTabViewPage.prototype.resetSize.call(this, newWidth, newHeight);
	
	this._listView.resetHeight(newHeight);
}
