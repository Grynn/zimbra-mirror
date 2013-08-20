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
clover.pageData = {"classes":[{"el":73,"id":5305,"methods":[{"el":54,"sc":5,"sl":46},{"el":62,"sc":5,"sl":59},{"el":72,"sc":5,"sl":69}],"name":"ConstructorTest","sl":27},{"el":34,"id":5305,"methods":[{"el":33,"sc":9,"sl":30}],"name":"ConstructorTest.FooClass","sl":29},{"el":37,"id":5307,"methods":[],"name":"ConstructorTest.EmptyConstructorClass","sl":36},{"el":44,"id":5307,"methods":[{"el":43,"sc":9,"sl":41}],"name":"ConstructorTest.ConstructorCallingPublicMethodClass","sl":39}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_306":{"methods":[{"sl":46},{"sl":69}],"name":"constructorCallingPublicMethod","pass":true,"statements":[{"sl":47},{"sl":48},{"sl":49},{"sl":50},{"sl":51},{"sl":52},{"sl":53},{"sl":71}]},"test_440":{"methods":[{"sl":46},{"sl":59}],"name":"emptyConstructor","pass":true,"statements":[{"sl":47},{"sl":48},{"sl":49},{"sl":50},{"sl":51},{"sl":52},{"sl":53},{"sl":61}]},"test_450":{"methods":[{"sl":46},{"sl":59}],"name":"emptyConstructor","pass":true,"statements":[{"sl":47},{"sl":48},{"sl":49},{"sl":50},{"sl":51},{"sl":52},{"sl":53},{"sl":61}]},"test_606":{"methods":[{"sl":46},{"sl":69}],"name":"constructorCallingPublicMethod","pass":true,"statements":[{"sl":47},{"sl":48},{"sl":49},{"sl":50},{"sl":51},{"sl":52},{"sl":53},{"sl":71}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [450, 440, 306, 606], [450, 440, 306, 606], [450, 440, 306, 606], [450, 440, 306, 606], [450, 440, 306, 606], [450, 440, 306, 606], [450, 440, 306, 606], [450, 440, 306, 606], [], [], [], [], [], [450, 440], [], [450, 440], [], [], [], [], [], [], [], [306, 606], [], [306, 606], [], []]
