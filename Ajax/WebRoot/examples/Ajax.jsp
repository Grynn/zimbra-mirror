<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!-- BEGIN SCRIPT BLOCK -->
<script type="text/javascript" src="/ajax/js/config/data/LsConfig.js"></script>

<script type="text/javascript" src="/ajax/js/core/LsCore.js"></script>
<script type="text/javascript" src="/ajax/js/core/LsEnv.js"></script>
<script type="text/javascript" src="/ajax/js/core/LsException.js"></script>
<script type="text/javascript" src="/ajax/js/core/LsImg.js"></script>

<script type="text/javascript" src="/ajax/js/util/LsCallback.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsCookie.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsDateUtil.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsTimedAction.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsSelectionManager.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsStringUtil.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsUtil.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsVector.js"></script>
<script type="text/javascript" src="/ajax/js/util/LsWindowOpener.js"></script>

<script type="text/javascript" src="/ajax/js/debug/LsDebug.js"></script>
<script type="text/javascript" src="/ajax/js/debug/LsDebugXmlDocument.js"></script>

<script type="text/javascript" src="/ajax/js/events/LsEvent.js"></script>
<script type="text/javascript" src="/ajax/js/events/LsEventMgr.js"></script>
<script type="text/javascript" src="/ajax/js/events/LsListener.js"></script>

<script type="text/javascript" src="/ajax/js/xml/LsXmlDoc.js"></script>

<script type="text/javascript" src="/ajax/js/net/LsPost.js"></script>
<script type="text/javascript" src="/ajax/js/net/LsRpcRequest.js"></script>
<script type="text/javascript" src="/ajax/js/net/LsRpc.js"></script>

<script type="text/javascript" src="/ajax/js/soap/LsSoapException.js"></script>
<script type="text/javascript" src="/ajax/js/soap/LsSoapFault.js"></script>
<script type="text/javascript" src="/ajax/js/soap/LsSoapDoc.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/core/DwtImg.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/core/Dwt.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/core/DwtException.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/core/DwtDraggable.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/graphics/DwtBorder.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/graphics/DwtCssStyle.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/graphics/DwtPoint.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/graphics/DwtRectangle.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/graphics/DwtUnits.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/events/DwtEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtEventManager.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtDateRangeEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtDisposeEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtUiEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtControlEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtKeyEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtMouseEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtMouseEventCapture.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtListViewActionEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtSelectionEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtHtmlEditorStateEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/events/DwtTreeEvent.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/dnd/DwtDragEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/dnd/DwtDragSource.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/dnd/DwtDropEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/dnd/DwtDropTarget.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtControl.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtComposite.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtShell.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtColorPicker.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtBaseDialog.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtDialog.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtLabel.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtListView.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtButton.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtMenuItem.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtMenu.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtMessageDialog.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtHtmlEditor.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtSash.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtToolBar.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtToolTip.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtTreeItem.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtTree.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtCalendar.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtPropertyPage.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtTabView.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtWizardDialog.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/widgets/DwtSelect.js"></script>

<script type="text/javascript" src="/ajax/js/dwt/events/DwtXFormsEvent.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/XFormGlobal.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/ButtonGrid.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/XModel.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/XModelItem.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/XForm.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/XFormItem.js"></script>
<script type="text/javascript" src="/ajax/js/dwt/xforms/OSelect_XFormItem.js"></script>

<!-- END SCRIPT BLOCK -->
