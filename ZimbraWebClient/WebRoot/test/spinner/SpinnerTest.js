function Test() {
};

Test.run = function() {
	var shell = new DwtShell("MainShell", false, null, null, true);

	var id1, id2, id3, id4, id5, id6, id7, id8;

	var html = [ "<table cellspacing=5>",
		     "<tr><td align=right>Default props</td><td id='", id1 = Dwt.getNextId(), "'></td></tr>",
		     "<tr><td align=right>0..10, 2 decimals, step 0.1</td><td id='", id2 = Dwt.getNextId(), "'></td></tr>",
		     "<tr><td align=right>No decimals, step 0.5</td><td id='", id3 = Dwt.getNextId(), "'></td></tr>",
		     "</table>"
		];

	var cont = new DwtComposite(shell, null, DwtControl.ABSOLUTE_STYLE);
	cont.getHtmlElement().innerHTML = html.join("");
	cont.zShow(true);

	var props1 = {
	    parent   : cont
	};
	var spin1 = new DwtSpinner(props1);
	spin1.reparentHtmlElement(id1);

	var props2 = {
	    parent   : cont,
	    value    : 5,
	    min      : 0,
	    max      : 10,
	    decimals : 2,
	    step     : 0.1
	};
	var spin2 = new DwtSpinner(props2);
	spin2.reparentHtmlElement(id2);

	var props3 = {
	    parent   : cont,
	    value    : 2,
	    min      : -10,
	    max      : 10,
	    decimals : null,
	    step     : 0.5
	};
	var spin3 = new DwtSpinner(props3);
	spin3.reparentHtmlElement(id3);
};
