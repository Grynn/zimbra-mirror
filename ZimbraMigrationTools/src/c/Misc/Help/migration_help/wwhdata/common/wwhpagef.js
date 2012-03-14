// Copyright (c) 2000-2011 Quadralay Corporation.  All rights reserved.
//

function  WWHGetWWHFrame(ParamToBookDir,
                         ParamRedirect)
{
  var  Frame = null;


  // Set reference to top level help frame
  //
  try
  {
    if ((typeof(parent.WWHHelp) != "undefined") &&
        (parent.WWHHelp != null))
    {
      Frame = eval("parent");
    }
    else if ((typeof(parent.parent.WWHHelp) != "undefined") &&
             (parent.parent.WWHHelp != null))
    {
      Frame = eval("parent.parent");
    }
  }
  catch (exception)
  {
    // Assume we've got a security situation on our hands
    //
  }

  // Redirect if Frame is null
  //
  if ((Frame == null) &&
      (ParamRedirect))
  {
    var  bPerformRedirect = true;
    var  Agent;


    // No redirect if running Netscape 4.x
    //
    Agent = navigator.userAgent.toLowerCase();
    if ((Agent.indexOf("mozilla") != -1) &&
        (Agent.indexOf("spoofer") == -1) &&
        (Agent.indexOf("compatible") == -1))
    {
      var  MajorVersion;


      MajorVersion = parseInt(navigator.appVersion)
      if (MajorVersion < 5)
      {
        bPerformRedirect = false;  // Skip redirect for Netscape 4.x
      }
    }

    if (bPerformRedirect)
    {
      var  BaseFilename;


      BaseFilename = location.href.substring(location.href.lastIndexOf("/") + 1, location.href.length);

      if (ParamToBookDir.length > 0)
      {
        var  RelativePathList = ParamToBookDir.split("/");
        var  PathList         = location.href.split("/");
        var  BaseList = new Array();
        var  MaxIndex;
        var  Index;
        var  RelativePathComponent;

        // Trim base file component
        //
        PathList.length--;
        for (MaxIndex = RelativePathList.length, Index = 0 ; Index < MaxIndex ; Index++)
        {
          RelativePathComponent = RelativePathList[Index];

          if (
              (RelativePathComponent == "")
               ||
              (RelativePathComponent == ".")
             )
          {
            // Do nothing!
            //
          }
          else if (RelativePathComponent == "..")
          {
            BaseList[BaseList.length] = PathList[PathList.length - 1];
            PathList.length = PathList.length - 1;
          }
          else
          {
            BaseList[BaseList.length] = RelativePathComponent;
          }
        }

        // Reverse compoment list before determining base file path
        //
        BaseList.reverse();

        // Build path
        //
        if (BaseList.length > 0)
        {
          BaseFilename = BaseList.join("/") + "/" + BaseFilename;
        }
        else
        {
          BaseFilename = BaseFilename;
        }
      }

      window.top.location.replace(WWHToWWHelpDirectory() + ParamToBookDir + "wwhelp/wwhimpl/api.htm?context=" + WWHBookData_Context() + "&file=" + BaseFilename + "&single=true");
    }
  }

  return Frame;
}

function  WWHShowPopup(ParamContext,
                       ParamLink,
                       ParamEvent)
{
  if (WWHFrame != null)
  {
    if ((ParamEvent == null) &&
        (typeof(window.event) != "undefined"))
    {
      ParamEvent = window.event;  // Older IE browsers only store event in window.event
    }

    WWHFrame.WWHHelp.fShowPopup(ParamContext, ParamLink, ParamEvent);
  }
}

function  WWHPopupLoaded()
{
  if (WWHFrame != null)
  {
    WWHFrame.WWHHelp.fPopupLoaded();
  }
}

function  WWHHidePopup()
{
  if (WWHFrame != null)
  {
    WWHFrame.WWHHelp.fHidePopup();
  }
}

function  WWHClickedPopup(ParamContext,
                          ParamLink,
                          ParamPopupLink)
{
  if (WWHFrame != null)
  {
    WWHFrame.WWHHelp.fClickedPopup(ParamContext, ParamLink, ParamPopupLink);
  }
}

function  WWHShowTopic(ParamContext,
                       ParamTopic)
{
  if (WWHFrame != null)
  {
    WWHFrame.WWHHelp.fShowTopic(ParamContext, ParamTopic);
  }
}

function  WWHUpdate()
{
  var  bVarSuccess = true;


  if (WWHFrame != null)
  {
    bVarSuccess = WWHFrame.WWHHelp.fUpdate(location.href);

    // Only update if "?" is not present (and therefore has priority)
    //
    if (location.href.indexOf("?") == -1)
    {
      // Start check hash polling
      //
      if ((WWHFrame.WWHBrowser.mBrowser == 2) ||  // Shorthand for IE
          (WWHFrame.WWHBrowser.mBrowser == 4))    // Shorthand for Netscape 6.0 (Mozilla)
      {
        if (WWHFrame.location.hash.length > 0)
        {
          WWHFrame.WWHHelp.mLastHash = WWHFrame.location.hash;
          WWHFrame.WWHHelp.mCheckHashTimeoutID = setTimeout(WWHFrame.WWHHelp.fGetFrameReference("WWHDocumentFrame") + ".WWHCheckHash()", 100);
        }
      }
    }
  }

  return bVarSuccess;
}

function  WWHCheckHash()
{
  var  VarURL;

  // Clear timeout ID
  //
  WWHFrame.WWHHelp.mCheckHashTimeoutID = null;

  // Change detected?
  //
  if ((WWHFrame.WWHHelp.mLastHash.length > 0) &&
      (WWHFrame.location.hash != WWHFrame.WWHHelp.mLastHash))
  {
    if ((WWHFrame.WWHHelp.mLastHash.indexOf("topic=") > 0) &&
        (WWHFrame.location.hash.indexOf("#href=") == 0))
    {
      // Context-sensitive link resolved
      // Update last hash and keep polling
      //
      WWHFrame.WWHHelp.mLastHash = WWHFrame.location.hash;
      WWHFrame.WWHHelp.mCheckHashTimeoutID = setTimeout(WWHFrame.WWHHelp.fGetFrameReference("WWHDocumentFrame") + ".WWHCheckHash()", 100);
    }
    else
    {
      // Set context document
      //
      WWHFrame.WWHHelp.mLastHash = "";
      WWHFrame.WWHHelp.fSetContextDocument(WWHFrame.location.href);
    }
  }
  else
  {
    // Keep polling
    //
    WWHFrame.WWHHelp.mLastHash = WWHFrame.location.hash;
    WWHFrame.WWHHelp.mCheckHashTimeoutID = setTimeout(WWHFrame.WWHHelp.fGetFrameReference("WWHDocumentFrame") + ".WWHCheckHash()", 100);
  }
}

function  WWHUnload()
{
  var  bVarSuccess = true;


  if (WWHFrame != null)
  {
    // Stop check hash polling
    //
    if ((WWHFrame.WWHHelp.mCheckHashTimeoutID != null) &&
        (typeof(WWHFrame.WWHHelp.mCheckHashTimeoutID) != "undefined"))
    {
      clearTimeout(WWHFrame.WWHHelp.mCheckHashTimeoutID);
      WWHFrame.WWHHelp.mCheckHashTimeoutID = null;
      WWHFrame.WWHHelp.mLastHash = "";
    }

    if (typeof(WWHFrame.WWHHelp) != "undefined")
    {
      bVarSuccess = WWHFrame.WWHHelp.fUnload();
    }
  }

  return bVarSuccess;
}

function  WWHHandleKeyDown(ParamEvent)
{
  var  bVarSuccess = true;


  if (WWHFrame != null)
  {
    bVarSuccess = WWHFrame.WWHHelp.fHandleKeyDown(ParamEvent);
  }

  return bVarSuccess;
}

function  WWHHandleKeyPress(ParamEvent)
{
  var  bVarSuccess = true;


  if (WWHFrame != null)
  {
    bVarSuccess = WWHFrame.WWHHelp.fHandleKeyPress(ParamEvent);
  }

  return bVarSuccess;
}

function  WWHHandleKeyUp(ParamEvent)
{
  var  bVarSuccess = true;


  if (WWHFrame != null)
  {
    bVarSuccess = WWHFrame.WWHHelp.fHandleKeyUp(ParamEvent);
  }

  return bVarSuccess;
}

function  WWHClearRelatedTopics()
{
  if (WWHFrame != null)
  {
    WWHFrame.WWHRelatedTopics.fClear();
  }
}

function  WWHAddRelatedTopic(ParamText,
                             ParamContext,
                             ParamFileURL)
{
  if (WWHFrame != null)
  {
    WWHFrame.WWHRelatedTopics.fAdd(ParamText, ParamContext, ParamFileURL);
  }
}

function  WWHRelatedTopicsInlineHTML()
{
  var  HTML = "";


  if (WWHFrame != null)
  {
    HTML = WWHFrame.WWHRelatedTopics.fInlineHTML();
  }

  return HTML;
}

function  WWHDoNothingHREF()
{
  // Nothing to do.
  //
}

function  WWHShowRelatedTopicsPopup(ParamEvent)
{
  if (WWHFrame != null)
  {
    if ((ParamEvent == null) &&
        (typeof(window.event) != "undefined"))
    {
      ParamEvent = window.event;  // Older IE browsers only store event in window.event
    }

    WWHFrame.WWHRelatedTopics.fShowAtEvent(ParamEvent);
  }
}

function  WWHShowALinksPopup(ParamKeywordArray,
                             ParamEvent)
{
  if (WWHFrame != null)
  {
    if ((ParamEvent == null) &&
        (typeof(window.event) != "undefined"))
    {
      ParamEvent = window.event;  // Older IE browsers only store event in window.event
    }

    WWHFrame.WWHALinks.fShow(ParamKeywordArray, ParamEvent);
  }
}

function  WWHRelatedTopicsDivTag()
{
  var  RelatedTopicsDivTag = "";


  if (WWHFrame != null)
  {
    RelatedTopicsDivTag = WWHFrame.WWHRelatedTopics.fPopupHTML();
  }

  return RelatedTopicsDivTag;
}

function  WWHPopupDivTag()
{
  var  PopupDivTag = "";


  if (WWHFrame != null)
  {
    PopupDivTag = WWHFrame.WWHHelp.fPopupHTML();
  }

  return PopupDivTag;
}

function  WWHALinksDivTag()
{
  var  ALinksDivTag = "";


  if (WWHFrame != null)
  {
    ALinksDivTag = WWHFrame.WWHALinks.fPopupHTML();
  }

  return ALinksDivTag;
}
