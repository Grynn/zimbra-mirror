/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
// Copyright (c) 2000-2011 Quadralay Corporation.  All rights reserved.
//

function  WWHHandler_Object()
{
  this.mbInitialized = false;

  this.fInit              = WWHHandler_Init;
  this.fFinalize          = WWHHandler_Finalize;
  this.fGetFrameReference = WWHHandler_GetFrameReference;
  this.fGetFrameName      = WWHHandler_GetFrameName;
  this.fIsReady           = WWHHandler_IsReady;
  this.fUpdate            = WWHHandler_Update;
  this.fSyncTOC           = WWHHandler_SyncTOC;
  this.fFavoritesCurrent  = WWHHandler_FavoritesCurrent;
  this.fProcessAccessKey  = WWHHandler_ProcessAccessKey;
  this.fGetCurrentTab     = WWHHandler_GetCurrentTab;
  this.fSetCurrentTab     = WWHHandler_SetCurrentTab;
}

function  WWHHandler_Init()
{
  this.mbInitialized = true;
  WWHFrame.WWHHelp.fHandlerInitialized();
}

function  WWHHandler_Finalize()
{
}

function  WWHHandler_GetFrameReference(ParamFrameName)
{
  var  VarFrameReference;


  // Nothing to do
  //

  return VarFrameReference;
}

function  WWHHandler_GetFrameName(ParamFrameName)
{
  var  VarName = null;


  // Nothing to do
  //

  return VarName;
}

function  WWHHandler_IsReady()
{
  var  bVarIsReady = true;


  return bVarIsReady;
}

function  WWHHandler_Update(ParamBookIndex,
                            ParamFileIndex)
{
}

function  WWHHandler_SyncTOC(ParamBookIndex,
                             ParamFileIndex,
                             ParamAnchor,
                             bParamReportError)
{
  setTimeout("WWHFrame.WWHControls.fSwitchToNavigation();", 1);
}

function  WWHHandler_FavoritesCurrent(ParamBookIndex,
                                      ParamFileIndex)
{
}

function  WWHHandler_ProcessAccessKey(ParamAccessKey)
{
  switch (ParamAccessKey)
  {
    case 1:
      WWHFrame.WWHControls.fSwitchToNavigation("contents");
      break;

    case 2:
      WWHFrame.WWHControls.fSwitchToNavigation("index");
      break;

    case 3:
      WWHFrame.WWHControls.fSwitchToNavigation("search");
      break;
  }
}

function  WWHHandler_GetCurrentTab()
{
  var  VarCurrentTab;


  // Initialize return value
  //
  VarCurrentTab = "";

  return VarCurrentTab;
}

function  WWHHandler_SetCurrentTab(ParamTabName)
{
}
