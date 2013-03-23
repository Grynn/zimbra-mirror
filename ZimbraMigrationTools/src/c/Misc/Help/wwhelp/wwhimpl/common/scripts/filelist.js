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

function  WWHFile_Object(ParamTitle,
                         ParamHREF)
{
  this.mTitle = ParamTitle;
  this.mHREF  = ParamHREF;
}

function  WWHFileList_Object()
{
  this.mFileList = new Array();
  this.mFileHash = new WWHFileHash_Object();

  this.fEntries          = WWHFileList_Entries;
  this.fAddFile          = WWHFileList_AddFile;
  this.fA                = WWHFileList_AddFile;
  this.fHREFToIndex      = WWHFileList_HREFToIndex;
  this.fHREFToTitle      = WWHFileList_HREFToTitle;
  this.fFileIndexToHREF  = WWHFileList_FileIndexToHREF;
  this.fFileIndexToTitle = WWHFileList_FileIndexToTitle;
}

function  WWHFileList_Entries()
{
  return this.mFileList.length;
}

function  WWHFileList_AddFile(ParamTitle,
                              ParamHREF)
{
  // Store unescaped to avoid browser specific auto-unescape behaviors
  //
  this.mFileHash[unescape(ParamHREF) + "~"] = this.mFileList.length;
  this.mFileList[this.mFileList.length] = new WWHFile_Object(ParamTitle, ParamHREF);
}

function  WWHFileList_HREFToIndex(ParamHREF)
{
  var  MatchIndex = -1;
  var  Match;


  // Query unescaped to avoid browser specific auto-unescape behaviors
  //
  Match = this.mFileHash[unescape(ParamHREF) + "~"];
  if (typeof(Match) != "undefined")
  {
    MatchIndex = Match;
  }

  return MatchIndex;
}

function  WWHFileList_HREFToTitle(ParamHREF)
{
  var  Title = "";
  var  MatchIndex;


  MatchIndex = this.fHREFToIndex(ParamHREF);
  if (MatchIndex != -1)
  {
    Title = this.mFileList[MatchIndex].mTitle;
  }
  else
  {
    Title = WWHStringUtilities_EscapeHTML(ParamHREF);
  }

  return Title;
}

function  WWHFileList_FileIndexToHREF(ParamIndex)
{
  return this.mFileList[ParamIndex].mHREF;
}

function  WWHFileList_FileIndexToTitle(ParamIndex)
{
  return this.mFileList[ParamIndex].mTitle;
}

function  WWHFileHash_Object()
{
}
