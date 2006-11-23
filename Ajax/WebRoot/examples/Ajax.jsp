<!-- 
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
-->
<% 
   String contextPath = (String)request.getContextPath(); 
%>

<!-- BEGIN SCRIPT BLOCK -->
<!-- WARNING: Order matters.  Don't re-order these unless you know what you're doing! -->

<!-- bootstrap classes -->
<script type="text/javascript" src="<%= contextPath %>/js/boot/AjxEnv.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/boot/AjxCallback.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/boot/AjxLoader.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/boot/AjxPackage.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/boot/AjxTemplate.js"></script>

<!-- AJAX utility classes -->
<script type="text/javascript" src="<%= contextPath %>/js/core/AjxCore.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxUtil.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxText.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/core/AjxException.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxCookie.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/soap/AjxSoapException.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/soap/AjxSoapFault.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/soap/AjxSoapDoc.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/net/AjxRpcRequest.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/net/AjxRpc.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxWindowOpener.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxVector.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxStringUtil.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/debug/AjxDebug.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/debug/AjxDebugXmlDocument.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/xml/AjxXmlDoc.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/config/data/AjxConfig.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/core/AjxEnv.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/core/AjxImg.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/core/AjxException.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxTimedAction.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/events/AjxEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/events/AjxEventMgr.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/events/AjxListener.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxDateUtil.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxStringUtil.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxVector.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxSelectionManager.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/net/AjxPost.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxBuffer.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/util/AjxCache.js"></script>

<!-- DWT classes -->
<script type="text/javascript" src="<%= contextPath %>/js/dwt/core/Dwt.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/core/DwtException.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/core/DwtDraggable.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/graphics/DwtCssStyle.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/graphics/DwtPoint.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/graphics/DwtRectangle.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/graphics/DwtUnits.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtEventManager.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtDateRangeEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtDisposeEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtUiEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtControlEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtFocusEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtKeyEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtMouseEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtMouseEventCapture.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtListViewActionEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtSelectionEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtHtmlEditorStateEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtTreeEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtHoverEvent.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/keyboard/DwtTabGroupEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/keyboard/DwtKeyMap.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/keyboard/DwtKeyMapMgr.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/keyboard/DwtKeyMapMgrException.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/keyboard/DwtKeyboardMgr.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/keyboard/DwtTabGroup.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/dnd/DwtDragEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/dnd/DwtDragSource.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/dnd/DwtDropEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/dnd/DwtDropTarget.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtHoverMgr.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtControl.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtComposite.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtShell.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtColorPicker.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtBaseDialog.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtDialog.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtLabel.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtListView.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtButton.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtMenuItem.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtMenu.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtMessageDialog.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtHtmlEditor.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtInputField.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtSash.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtToolBar.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/graphics/DwtBorder.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtToolTip.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtStickyToolTip.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtTreeItem.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtTree.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtCalendar.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtPropertyPage.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtTabView.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtWizardDialog.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtSelect.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtAddRemove.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtAlert.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtText.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtIframe.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtXFormDialog.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtPropertySheet.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtGrouper.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/widgets/DwtProgressBar.js"></script>

<script type="text/javascript" src="<%= contextPath %>/js/dwt/events/DwtXFormsEvent.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/XFormGlobal.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/XModel.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/XModelItem.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/XForm.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/XFormItem.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/XFormChoices.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/OSelect_XFormItem.js"></script>
<script type="text/javascript" src="<%= contextPath %>/js/dwt/xforms/ButtonGrid.js"></script>
<!-- END SCRIPT BLOCK -->
