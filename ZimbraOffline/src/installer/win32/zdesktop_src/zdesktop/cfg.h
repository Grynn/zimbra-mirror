/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

#ifndef CFG_H
#define CFG_H

#include <string>
#include <map>

using namespace std;

class Config {
public:
    Config() {};
    ~Config() {};

    bool Load(string &cfgfile);
    string &Get(const char *key);
    string &Get(string &key) { return Get(key.c_str()); }

protected:
    typedef map<string, string> CfgMap;

    CfgMap cfg;
    CfgMap vars;

    void Expand(string &val);

#ifdef _DEBUG
public:
    void Dump(ostream &s);
#endif

};

#endif