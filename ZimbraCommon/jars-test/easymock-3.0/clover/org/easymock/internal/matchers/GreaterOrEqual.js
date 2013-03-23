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
clover.pageData = {"classes":[{"el":42,"id":2743,"methods":[{"el":31,"sc":5,"sl":29},{"el":36,"sc":5,"sl":33},{"el":41,"sc":5,"sl":38}],"name":"GreaterOrEqual","sl":25}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_1040":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"testGreateOrEqual","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_1060":{"methods":[{"sl":29},{"sl":38}],"name":"lessThan","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_148":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"testGreateOrEqual","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_422":{"methods":[{"sl":29},{"sl":38}],"name":"greaterOrEqualOverloaded","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_467":{"methods":[{"sl":29},{"sl":38}],"name":"greaterOrEqual","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_494":{"methods":[{"sl":29},{"sl":38}],"name":"greaterOrEqualOverloaded","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_523":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"constraints","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_585":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"constraints","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_674":{"methods":[{"sl":29},{"sl":38}],"name":"lessThan","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_694":{"methods":[{"sl":29},{"sl":38}],"name":"greaterOrEqual","pass":true,"statements":[{"sl":30},{"sl":40}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [422, 523, 1040, 674, 585, 494, 467, 1060, 148, 694], [422, 523, 1040, 674, 585, 494, 467, 1060, 148, 694], [], [], [523, 1040, 585, 148], [], [523, 1040, 585, 148], [], [], [422, 523, 1040, 674, 585, 494, 467, 1060, 148, 694], [], [422, 523, 1040, 674, 585, 494, 467, 1060, 148, 694], [], []]
