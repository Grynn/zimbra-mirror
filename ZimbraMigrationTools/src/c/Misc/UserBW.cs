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
namespace Misc
{
using System.Windows.Data;
using System.ComponentModel;
using System;

public class UserBW : BackgroundWorker
{
    public int usernum {
        get;
        set;
    }
    public int threadnum {
        get;
        set;
    }
    public UserBW(int tnum)
    {
        usernum = -1;
        threadnum = tnum;
    }
}
}
