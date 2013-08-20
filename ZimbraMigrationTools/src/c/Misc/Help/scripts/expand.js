/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
// Copyright (c) 2002-2011 Quadralay Corporation.  All rights reserved.
//

function  WebWorks_WriteArrow(ParamBaseURL,
                              ParamID,
                              bParamExpanded)
{
  var  VarIMGSrc;

  if ((WWHFrame != null) &&
      ( ! WWHFrame.WWHHelp.mbAccessible) &&
      ((typeof(document.all) != "undefined") ||
       (typeof(document.getElementById) != "undefined")))
  {
    document.write(" <a href=\"javascript:WebWorks_ToggleDIV('" + ParamID + "');\">");

    if ((bParamExpanded) ||
        ((typeof(WWHFrame.WWHHighlightWords) != "undefined") &&
         (WWHFrame.WWHHighlightWords != null) &&
         (WWHFrame.WWHHighlightWords.mWordList != null)))
    {
      VarIMGSrc = ParamBaseURL + "images/expanded.gif";
    }
    else
    {
      VarIMGSrc = ParamBaseURL + "images/collapse.gif";
    }

    document.write("<img id=\"" + ParamID + "_arrow\" src=\"" + VarIMGSrc + "\" border=\"0\">");
    document.write("</a>");
  }
}

function  WebWorks_WriteDIVOpen(ParamID,
                                bParamExpanded)
{
  if ((WWHFrame != null) &&
      ( ! WWHFrame.WWHHelp.mbAccessible) &&
      ((typeof(document.all) != "undefined") ||
       (typeof(document.getElementById) != "undefined")))
  {
    if ((bParamExpanded) ||
        ((typeof(WWHFrame.WWHHighlightWords) != "undefined") &&
         (WWHFrame.WWHHighlightWords != null) &&
         (WWHFrame.WWHHighlightWords.mWordList != null)))
    {
      document.write("<div id=\"" + ParamID + "\" style=\"visibility: visible; display: block;\">");
    }
    else
    {
      document.write("<div id=\"" + ParamID + "\" style=\"visibility: hidden; display: none;\">");
    }
  }
}

function  WebWorks_WriteDIVClose()
{
  if ((WWHFrame != null) &&
      ( ! WWHFrame.WWHHelp.mbAccessible) &&
      ((typeof(document.all) != "undefined") ||
       (typeof(document.getElementById) != "undefined")))
  {
    document.write("</div>");
  }
}

function  WebWorks_ToggleDIV(ParamBaseURL,
                             ParamID)
{
  var  VarImageID;
  var  VarIMG;
  var  VarDIV;


  VarImageID = ParamID + "_arrow";

  if (typeof(document.all) != "undefined")
  {
    // Reference image
    //
    VarIMG = document.all[VarImageID];
    if ((typeof(VarIMG) != "undefined") &&
        (VarIMG != null))
    {
      // Nothing to do
    }
    else
    {
      VarIMG = null;
    }

    // Reference DIV tag
    //
    VarDIV = document.all[ParamID];
    if ((typeof(VarDIV) != "undefined") &&
        (VarDIV != null))
    {
      if (VarDIV.style.display == "block")
      {
        if (VarIMG != null)
        {
          VarIMG.src = ParamBaseURL + "images/collapse.gif";
        }

        VarDIV.style.visibility = "hidden";
        VarDIV.style.display = "none";
      }
      else
      {
        if (VarIMG != null)
        {
          VarIMG.src = ParamBaseURL + "images/expanded.gif";
        }

        VarDIV.style.visibility = "visible";
        VarDIV.style.display = "block";
      }
    }
  }
  else if (typeof(document.getElementById) != "undefined")
  {
    // Reference image
    //
    VarIMG = document[VarImageID];
    if ((typeof(VarIMG) != "undefined") &&
        (VarIMG != null))
    {
      // Nothing to do
    }
    else
    {
      VarIMG = null;
    }

    // Reference DIV tag
    //
    VarDIV = document.getElementById(ParamID);
    if ((typeof(VarDIV) != "undefined") &&
        (VarDIV != null))
    {
      if (VarDIV.style.display == "block")
      {
        if (VarIMG != null)
        {
          VarIMG.src = ParamBaseURL + "images/collapse.gif";
        }

        VarDIV.style.visibility = "hidden";
        VarDIV.style.display = "none";
      }
      else
      {
        if (VarIMG != null)
        {
          VarIMG.src = ParamBaseURL + "images/expanded.gif";
        }

        VarDIV.style.visibility = "visible";
        VarDIV.style.display = "block";
      }
    }
  }
}
