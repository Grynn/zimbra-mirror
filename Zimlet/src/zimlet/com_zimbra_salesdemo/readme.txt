This demo zimlet is designed to allow you to easily demo Zimlet's capabilities for different Sales verticals.
This allows to:
1. Match a text in the body
2. Show tooltip when the user hover overs it
 2.1 Show whatever html you want inside the tooltip (using templates; see templates/SalesDemo.template)
3.Add context-menu items for matched text
    3.1 Add whatever menu items you want
4. Show dialog when user clicks on the item
  4.1 Show whatever html you want inside the dialog (using templates; see templates/SalesDemo.template)
 5. Show Toolbar
  5.1  Show  whatever html you want inside the toolbar (using templates; see templates/SalesDemo.template)
  5.2 Add up to 3 toolbar buttons


0. Open data.xml file - This is where you will describe all the above mentioned parts.

1. Match a text in the body
Example: Lets say you want to match "SEM_12345_31122011" text..
<items>
    <item>
        <tooltip>
            <textToMatch>SEM_12345_31122011</textToMatch>
         </tooltip>
    </item>
 <items>

2. Show tooltip when the user hover overs it
 2.1 Show whatever html you want inside the tooltip (using templates; see templates/SalesDemo.template)
 -  Add a tag <tooltipTemplateId> and enter the template id you want to use.
 For example: In the below case, we are using TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP  as tooltip's template id

 <items>
    <item>
        <tooltip>
            <textToMatch>SEM_12345_31122011</textToMatch>
            <tooltipTemplateId>TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP</tooltipTemplateId>
         </tooltip>
    </item>
 <items>

3.Add context-menu items for matched text
   Example: Add a context menu item whose name:  "Display Data" and uses "SAP_Icon"
        <tooltip>
            <textToMatch>SEM_12345_31122011</textToMatch>
            <tooltipTemplateId>TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP</tooltipTemplateId>

            <!-- When the user right-clicks.. show this context menu-->
            <contextMenu>
                <!--A single context menu item -->
                <contextMenuItem>
                    <!-- menu item name -->
                    <name>Display Data</name>
                    <!-- menu item's icon [this is the CSS name described in Sales.css] -->
                    <icon>SAP_Icon</icon>
                </contextMenuItem>
            </contextMenu>
        </tooltip>

    3.1 Add whatever menu items you want
         - Simply repeat <contextMenuItem> with different names and icons
            <contextMenu>
                <!--A single context menu item -->
                <contextMenuItem>
                    <name>menu item1</name>
                    <icon>SAP_Icon</icon>
                </contextMenuItem>
                 <contextMenuItem>
                    <name>menu item1</name>
                    <icon>SAP_Icon</icon>
                </contextMenuItem>
            </contextMenu>

4. Show dialog when user clicks on the item
  4.1 Show whatever html you want inside the dialog (using templates; see templates/SalesDemo.template)
  To show some dialog when user clicks, simply add <popupDialog> tag
  and then point this <dialogTemplateId>  to a unique template id (similar to what we did for tooltip)
        <!--[Optional] Show a dialog when the user clicks on the Zimlet's link -->
        <popupDialog>
            <!-- Template Id(<zimletName>/templates/Sales.template) contains html to be displayed in dialog -->
            <dialogTemplateId>TEMPLATE_FOR_SAP_SEM_ITEM_DIALOG</dialogTemplateId>
        </popupDialog>


5. Show Toolbar
  5.1  Show  whatever html you want inside the toolbar (using templates; see templates/SalesDemo.template)

    Simply add <toolbar> tag.. and then set its name in <toolbarName> tag, its icon in <icon> tag and finally
    the html to be loaded from template in <toolbarTemplateId>
         <toolbar>
            <toolbarName>Healthcare toolbar</toolbarName>
            <icon>Epic_Icon</icon>
            <toolbarTemplateId>TEMPLATE_FOR_EPIC_TOOLBAR</toolbarTemplateId>
        </toolbar>


  5.2  Add up to 3 toolbar buttons
        Add a <toolbarButtons> tag and then start adding individual toolbar buttons like below.
            <toolbarButtons>
                <toolbarButton>
                    <name>Escalate to MD</name>
                    <icon>Epic_Icon</icon>
                </toolbarButton>
                <toolbarButton>
                    <name>Send Alert</name>
                    <icon>Epic_Icon</icon>
                </toolbarButton>
            </toolbarButtons>



Creating a template:
  As an example, lets take tooltip's template. In this TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP is just a unique text. To create template
  Open templates/SalesDemo.template
  Create a template like:
  <template id="TEMPLATE_FOR_SAP_SEM_ITEM_TOOLTIP">
   Put all the html inside here
   Note:
    If you are using an image, put the image in img/yourImage.png and then refer that as
    ${zimletBaseUrl}/img/yourImage.png
  </template>

Creating and referring icons:
    First copy the icon into img folder.. say: SAP_icon.png
    Open SalesDemo.css file and create a CSS class like this (by prepending "Img" to CSS className:
    .ImgSAP_icon {
        background: url("img/SAP_icon.jpg") no-repeat 0 0;
        width: 16px;
        height: 16px;
        overflow: hidden;
    }
    Then, you can refer this class in data.xml or in templates by SAP_icon (without "Img")
    
    
 Zipping this Zimlet back:
 
 Navigate *into* the Zimlet's base folder and do:
  zip -r com_zimbra_salesdemo.zip *
  For example:
  [Desktop/com_zimbra_salesdemo]$ zip -r com_zimbra_salesdemo.zip *



