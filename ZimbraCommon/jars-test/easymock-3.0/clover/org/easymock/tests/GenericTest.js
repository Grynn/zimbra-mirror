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
clover.pageData = {"classes":[{"el":82,"id":5402,"methods":[{"el":51,"sc":5,"sl":44},{"el":81,"sc":5,"sl":74}],"name":"GenericTest","sl":32},{"el":36,"id":5402,"methods":[],"name":"GenericTest.C","sl":34},{"el":42,"id":5402,"methods":[{"el":41,"sc":9,"sl":39}],"name":"GenericTest.B","sl":38},{"el":59,"id":5410,"methods":[{"el":56,"sc":9,"sl":54}],"name":"GenericTest.AbstractFoo","sl":53},{"el":66,"id":5412,"methods":[{"el":65,"sc":9,"sl":62}],"name":"GenericTest.ConcreteFoo","sl":61}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_223":{"methods":[{"sl":74}],"name":"testPackageScope","pass":true,"statements":[{"sl":76},{"sl":77},{"sl":78},{"sl":79},{"sl":80}]},"test_453":{"methods":[{"sl":44}],"name":"testBridgeUnmocked","pass":true,"statements":[{"sl":46},{"sl":47},{"sl":48},{"sl":49},{"sl":50}]},"test_703":{"methods":[{"sl":74}],"name":"testPackageScope","pass":true,"statements":[{"sl":76},{"sl":77},{"sl":78},{"sl":79},{"sl":80}]},"test_970":{"methods":[{"sl":44}],"name":"testBridgeUnmocked","pass":true,"statements":[{"sl":46},{"sl":47},{"sl":48},{"sl":49},{"sl":50}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [453, 970], [], [453, 970], [453, 970], [453, 970], [453, 970], [453, 970], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [223, 703], [], [223, 703], [223, 703], [223, 703], [223, 703], [223, 703], [], []]
