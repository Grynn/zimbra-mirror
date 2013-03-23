/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":43,"id":2825,"methods":[{"el":34,"sc":5,"sl":32},{"el":38,"sc":5,"sl":36},{"el":42,"sc":5,"sl":40}],"name":"StartsWith","sl":26}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_123":{"methods":[{"sl":32},{"sl":36}],"name":"testStartsWith","pass":true,"statements":[{"sl":33},{"sl":37}]},"test_402":{"methods":[{"sl":32},{"sl":40}],"name":"startsWithToString","pass":true,"statements":[{"sl":33},{"sl":41}]},"test_446":{"methods":[{"sl":32},{"sl":36}],"name":"testStartsWith","pass":true,"statements":[{"sl":33},{"sl":37}]},"test_516":{"methods":[{"sl":32},{"sl":40}],"name":"startsWithToString","pass":true,"statements":[{"sl":33},{"sl":41}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [402, 446, 123, 516], [402, 446, 123, 516], [], [], [446, 123], [446, 123], [], [], [402, 516], [402, 516], [], []]
