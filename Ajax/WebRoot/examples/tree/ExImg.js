/*
***** BEGIN LICENSE BLOCK *****
Version: ZAPL 1.1

The contents of this file are subject to the Zimbra AJAX Public License Version 1.1 ("License");
You may not use this file except in compliance with the License. You may obtain a copy of the
License at http://www.zimbra.com/license

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF
ANY KIND, either express or implied. See the License for the specific language governing rights
and limitations under the License.

The Original Code is: Zimbra AJAX Toolkit.

The Initial Developer of the Original Code is Zimbra, Inc.
Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
All Rights Reserved.
Contributor(s): ______________________________________.

***** END LICENSE BLOCK *****
*/

function ExImg() {
}

// Data for images = filename, width, height] Images are represented in this mode, rather than by 
// simple image URLs because of the way in which we provide support for clients over low bandwidth
// connections.


// Icons

ExImg.I_BLANK  = [null, 16, 16];
ExImg.I_FOLDER  = ["FolderIcon", 16, 16];
ExImg.I_ICON  = ["IconIcon", 16, 16];
