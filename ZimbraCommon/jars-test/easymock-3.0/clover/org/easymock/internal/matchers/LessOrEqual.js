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
clover.pageData = {"classes":[{"el":42,"id":2761,"methods":[{"el":31,"sc":5,"sl":29},{"el":36,"sc":5,"sl":33},{"el":41,"sc":5,"sl":38}],"name":"LessOrEqual","sl":25}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_120":{"methods":[{"sl":29},{"sl":38}],"name":"lessOrEqual","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_357":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"testLessOrEqual","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_388":{"methods":[{"sl":29},{"sl":38}],"name":"greaterThan","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_476":{"methods":[{"sl":29},{"sl":38}],"name":"lessOrEqual","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_485":{"methods":[{"sl":29},{"sl":38}],"name":"greaterThan","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_523":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"constraints","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_585":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"constraints","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]},"test_586":{"methods":[{"sl":29},{"sl":38}],"name":"lessOrEqualOverloaded","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_739":{"methods":[{"sl":29},{"sl":38}],"name":"lessOrEqualOverloaded","pass":true,"statements":[{"sl":30},{"sl":40}]},"test_817":{"methods":[{"sl":29},{"sl":33},{"sl":38}],"name":"testLessOrEqual","pass":true,"statements":[{"sl":30},{"sl":35},{"sl":40}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [523, 817, 485, 739, 585, 586, 388, 357, 120, 476], [523, 817, 485, 739, 585, 586, 388, 357, 120, 476], [], [], [523, 817, 585, 357], [], [523, 817, 585, 357], [], [], [523, 817, 485, 739, 585, 586, 388, 357, 120, 476], [], [523, 817, 485, 739, 585, 586, 388, 357, 120, 476], [], []]
