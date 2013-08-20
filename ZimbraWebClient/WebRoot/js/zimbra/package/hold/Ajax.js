/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
AjxPackage.require("ajax.core.AjxCore");
AjxPackage.require("ajax.util.AjxUtil");
AjxPackage.require("ajax.core.AjxException");
AjxPackage.require("ajax.util.AjxCookie");
AjxPackage.require("ajax.soap.AjxSoapException");
AjxPackage.require("ajax.soap.AjxSoapFault");
AjxPackage.require("ajax.soap.AjxSoapDoc");
AjxPackage.require("ajax.net.AjxRpcRequest");
AjxPackage.require("ajax.net.AjxRpc");
AjxPackage.require("ajax.util.AjxWindowOpener");
AjxPackage.require("ajax.util.AjxVector");
AjxPackage.require("ajax.util.AjxStringUtil");
AjxPackage.require("ajax.debug.AjxDebug");
AjxPackage.require("ajax.debug.AjxDebugXmlDocument");
AjxPackage.require("ajax.xml.AjxXmlDoc");
AjxPackage.require("ajax.xml.AjxSerializer");
AjxPackage.require("ajax.core.AjxImg");
AjxPackage.require("ajax.events.AjxEvent");
AjxPackage.require("ajax.events.AjxEventMgr");
AjxPackage.require("ajax.util.AjxTimedAction");
AjxPackage.require("ajax.net.AjxInclude");
AjxPackage.require("ajax.events.AjxListener");
AjxPackage.require("ajax.util.AjxText");
AjxPackage.require("ajax.util.AjxDateUtil");
AjxPackage.require("ajax.util.AjxSelectionManager");
AjxPackage.require("ajax.net.AjxPost");
AjxPackage.require("ajax.util.AjxBuffer");
AjxPackage.require("ajax.util.AjxTimezoneData");
AjxPackage.require("ajax.util.AjxTimezone");
AjxPackage.require("ajax.xslt.AjxXslt");
AjxPackage.require("ajax.util.AjxSHA1");
AjxPackage.require("ajax.util.AjxEmailAddress");
AjxPackage.require("ajax.util.AjxHistoryMgr");

// DWT files

AjxPackage.require("ajax.dwt.core.Dwt");
AjxPackage.require("ajax.dwt.core.DwtException");
AjxPackage.require("ajax.dwt.core.DwtDraggable");
AjxPackage.require("ajax.dwt.core.DwtDragTracker");

AjxPackage.require("ajax.dwt.graphics.DwtCssStyle");
AjxPackage.require("ajax.dwt.graphics.DwtPoint");
AjxPackage.require("ajax.dwt.graphics.DwtRectangle");
AjxPackage.require("ajax.dwt.graphics.DwtUnits");

AjxPackage.require("ajax.dwt.events.DwtEvent");
AjxPackage.require("ajax.dwt.events.DwtEventManager");
AjxPackage.require("ajax.dwt.events.DwtDateRangeEvent");
AjxPackage.require("ajax.dwt.events.DwtDisposeEvent");
AjxPackage.require("ajax.dwt.events.DwtUiEvent");
AjxPackage.require("ajax.dwt.events.DwtControlEvent");
AjxPackage.require("ajax.dwt.events.DwtFocusEvent");
AjxPackage.require("ajax.dwt.events.DwtKeyEvent");
AjxPackage.require("ajax.dwt.events.DwtMouseEvent");
AjxPackage.require("ajax.dwt.events.DwtMouseEventCapture");
AjxPackage.require("ajax.dwt.events.DwtListViewActionEvent");
AjxPackage.require("ajax.dwt.events.DwtSelectionEvent");
AjxPackage.require("ajax.dwt.events.DwtHtmlEditorStateEvent");
AjxPackage.require("ajax.dwt.events.DwtTreeEvent");
AjxPackage.require("ajax.dwt.events.DwtHoverEvent");

AjxPackage.require("ajax.dwt.keyboard.DwtTabGroupEvent");
AjxPackage.require("ajax.dwt.keyboard.DwtKeyMap");
AjxPackage.require("ajax.dwt.keyboard.DwtKeyMapMgr");
AjxPackage.require("ajax.dwt.keyboard.DwtKeyboardMgr");
AjxPackage.require("ajax.dwt.keyboard.DwtTabGroup");

AjxPackage.require("ajax.dwt.dnd.DwtDragEvent");
AjxPackage.require("ajax.dwt.dnd.DwtDragSource");
AjxPackage.require("ajax.dwt.dnd.DwtDropEvent");
AjxPackage.require("ajax.dwt.dnd.DwtDropTarget");

AjxPackage.require("ajax.dwt.widgets.DwtHoverMgr");
AjxPackage.require("ajax.dwt.widgets.DwtControl");
AjxPackage.require("ajax.dwt.widgets.DwtComposite");
AjxPackage.require("ajax.dwt.widgets.DwtShell");
AjxPackage.require("ajax.dwt.widgets.DwtColorPicker");
AjxPackage.require("ajax.dwt.widgets.DwtBaseDialog");
AjxPackage.require("ajax.dwt.widgets.DwtDialog");
AjxPackage.require("ajax.dwt.widgets.DwtLabel");
AjxPackage.require("ajax.dwt.widgets.DwtCheckbox");
AjxPackage.require("ajax.dwt.widgets.DwtRadioButton");
AjxPackage.require("ajax.dwt.widgets.DwtListView");
AjxPackage.require("ajax.dwt.widgets.DwtButton");
AjxPackage.require("ajax.dwt.widgets.DwtMenuItem");
AjxPackage.require("ajax.dwt.widgets.DwtMenu");
AjxPackage.require("ajax.dwt.widgets.DwtMessageDialog");
AjxPackage.require("ajax.dwt.widgets.DwtHtmlEditor");
AjxPackage.require("ajax.dwt.widgets.DwtInputField");
AjxPackage.require("ajax.dwt.widgets.DwtPasswordField");
AjxPackage.require("ajax.dwt.widgets.DwtSash");
AjxPackage.require("ajax.dwt.widgets.DwtToolBar");
AjxPackage.require("ajax.dwt.widgets.DwtToolTip");
AjxPackage.require("ajax.dwt.widgets.DwtTreeItem");
AjxPackage.require("ajax.dwt.widgets.DwtTree");
AjxPackage.require("ajax.dwt.widgets.DwtCalendar");
AjxPackage.require("ajax.dwt.widgets.DwtFolderChooser");
AjxPackage.require("ajax.dwt.widgets.DwtPropertyPage");
AjxPackage.require("ajax.dwt.widgets.DwtTabView");
AjxPackage.require("ajax.dwt.widgets.DwtWizardDialog");
AjxPackage.require("ajax.dwt.widgets.DwtSelect");
AjxPackage.require("ajax.dwt.widgets.DwtAlert");
AjxPackage.require("ajax.dwt.widgets.DwtText");
AjxPackage.require("ajax.dwt.widgets.DwtIframe");
AjxPackage.require("ajax.dwt.widgets.DwtPropertySheet");
AjxPackage.require("ajax.dwt.widgets.DwtGrouper");
AjxPackage.require("ajax.dwt.widgets.DwtProgressBar");
AjxPackage.require("ajax.dwt.widgets.DwtPropertyEditor");
AjxPackage.require("ajax.dwt.widgets.DwtConfirmDialog");
AjxPackage.require("ajax.dwt.widgets.DwtChooser");
AjxPackage.require("ajax.dwt.widgets.DwtGridSizePicker");
AjxPackage.require("ajax.dwt.widgets.DwtSpinner");
AjxPackage.require("ajax.dwt.widgets.DwtButtonColorPicker");
AjxPackage.require("ajax.dwt.widgets.DwtMessageComposite");
AjxPackage.require("ajax.dwt.widgets.DwtRadioButtonGroup");
