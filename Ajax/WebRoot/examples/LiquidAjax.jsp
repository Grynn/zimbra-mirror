<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!-- BEGIN SCRIPT BLOCK -->
<script type="text/javascript" src="/liquidAjax/js/config/data/LsConfig.js"></script>

<script type="text/javascript" src="/liquidAjax/js/core/LsCore.js"></script>
<script type="text/javascript" src="/liquidAjax/js/core/LsEnv.js"></script>
<script type="text/javascript" src="/liquidAjax/js/core/LsException.js"></script>
<script type="text/javascript" src="/liquidAjax/js/core/LsImg.js"></script>

<script type="text/javascript" src="/liquidAjax/js/util/LsCallback.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsCookie.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsDateUtil.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsTimedAction.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsSelectionManager.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsStringUtil.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsUtil.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsVector.js"></script>
<script type="text/javascript" src="/liquidAjax/js/util/LsWindowOpener.js"></script>

<script type="text/javascript" src="/liquidAjax/js/debug/LsDebug.js"></script>
<script type="text/javascript" src="/liquidAjax/js/debug/LsDebugXmlDocument.js"></script>

<script type="text/javascript" src="/liquidAjax/js/events/LsEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/events/LsEventMgr.js"></script>
<script type="text/javascript" src="/liquidAjax/js/events/LsListener.js"></script>

<script type="text/javascript" src="/liquidAjax/js/xml/LsXmlDoc.js"></script>

<script type="text/javascript" src="/liquidAjax/js/net/LsPost.js"></script>
<script type="text/javascript" src="/liquidAjax/js/net/LsRpcRequest.js"></script>
<script type="text/javascript" src="/liquidAjax/js/net/LsRpc.js"></script>

<script type="text/javascript" src="/liquidAjax/js/soap/LsSoapException.js"></script>
<script type="text/javascript" src="/liquidAjax/js/soap/LsSoapFault.js"></script>
<script type="text/javascript" src="/liquidAjax/js/soap/LsSoapDoc.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/core/DwtImg.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/core/Dwt.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/core/DwtException.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/core/DwtDraggable.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/graphics/DwtBorder.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/graphics/DwtCssStyle.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/graphics/DwtPoint.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/graphics/DwtRectangle.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/graphics/DwtUnits.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtEventManager.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtDateRangeEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtDisposeEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtUiEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtControlEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtKeyEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtMouseEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtMouseEventCapture.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtListViewActionEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtSelectionEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtHtmlEditorStateEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtTreeEvent.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/dnd/DwtDragEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/dnd/DwtDragSource.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/dnd/DwtDropEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/dnd/DwtDropTarget.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtControl.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtComposite.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtShell.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtColorPicker.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtBaseDialog.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtDialog.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtLabel.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtListView.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtButton.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtMenuItem.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtMenu.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtMessageDialog.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtHtmlEditor.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtSash.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtToolBar.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtToolTip.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtTreeItem.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtTree.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtCalendar.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtPropertyPage.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtTabView.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtWizardDialog.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/widgets/DwtSelect.js"></script>

<script type="text/javascript" src="/liquidAjax/js/dwt/events/DwtXFormsEvent.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/XFormGlobal.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/ButtonGrid.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/XModel.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/XModelItem.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/XForm.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/XFormItem.js"></script>
<script type="text/javascript" src="/liquidAjax/js/dwt/xforms/OSelect_XFormItem.js"></script>

<!-- END SCRIPT BLOCK -->
