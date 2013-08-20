/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":42,"id":2680,"methods":[{"el":31,"sc":5,"sl":29},{"el":36,"sc":5,"sl":33},{"el":41,"sc":5,"sl":38}],"name":"CompareEqual","sl":25}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_1096":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"testCompareEqual","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_540":{"methods":[{"sl":29},{"sl":38}],"name":"cmpTo","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_561":{"methods":[{"sl":29},{"sl":38}],"name":"cmpTo","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_574":{"methods":[{"sl":29},{"sl":38}],"name":"testCompare","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_736":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"testCompareEqual","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_947":{"methods":[{"sl":29},{"sl":38}],"name":"testCompare","pass":true,"statements":[{"sl":30},{"sl":40}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [561, 540, 947, 1096, 736, 574], [561, 540, 947, 1096, 736, 574], [], [], [1096, 736], [], [1096, 736], [], [], [561, 540, 947, 1096, 736, 574], [], [561, 540, 947, 1096, 736, 574], [], []]
