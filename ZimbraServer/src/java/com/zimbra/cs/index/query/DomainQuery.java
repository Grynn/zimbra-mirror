/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.index.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import com.zimbra.cs.index.QueryOperation;
import com.zimbra.cs.index.TextQueryOperation;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query by email domain.
 *
 * @author tim
 * @author ysasaki
 */
public final class DomainQuery extends Query {
    private final String field;
    private final String term;
    private final Mailbox mailbox;

    public DomainQuery(Mailbox mbox, String field, String term) {
        this.field = field;
        this.term = term;
        this.mailbox = mbox;
    }

    @Override
    public QueryOperation getQueryOperation(boolean bool) {
        TextQueryOperation op = mailbox.getMailboxIndex().createTextQueryOperation();
        op.addClause(toQueryString(field, term),
                new TermQuery(new Term(field, term)), evalBool(bool));
        return op;
    }

    @Override
    public void dump(StringBuilder out) {
        out.append("DOMAIN,");
        out.append(term);
    }
}
