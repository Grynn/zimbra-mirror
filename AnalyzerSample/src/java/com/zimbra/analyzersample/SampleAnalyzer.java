/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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
package com.zimbra.analyzersample;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import com.zimbra.cs.index.LuceneFields;
import com.zimbra.cs.index.ZimbraAnalyzer;

/**
 * Our sample "customized" Analyzer: all we do is pass all requests on do the default Zimbra analyzer.
 *
 * The Analyzer-API in Lucene is based on the decorator pattern. Therefore all non-abstract subclasses must be final or
 * their tokenStream() and reusableTokenStream() implementations must be final! This is checked when Java assertions are
 * enabled.
 */
public final class SampleAnalyzer extends Analyzer {

    /**
     * Return a {@link TokenStream} instance depending on the specified Field and, potentially, the contents of the
     * Reader itself.
     *
     * There are a number of different Lucene fields that get analyzed. In many cases our custom analyzer will not want
     * to change the default behavior but for other fields (e.g. L_CONTENT) we probably do want to plug in.
     */
    @Override
    public TokenStream tokenStream(String field, Reader reader) {
        if (field.equals(LuceneFields.L_FILENAME)) {
            return ZimbraAnalyzer.getTokenStream(field, reader);
        } else if (field.equals(LuceneFields.L_CONTENT)) {
            // L_CONTENT field means all the text in the document.
            // We probably want to plug our custom analyzer in here
            return ZimbraAnalyzer.getTokenStream(field, reader);
        } else if (field.equals(LuceneFields.L_H_SUBJECT)) {
            // L_H_SUBJECT field means the subject of the document.
            // We probably want to plug our custom analyzer in here
            return ZimbraAnalyzer.getTokenStream(field, reader);
        } else {
            // Catch-All: probably want to pass this up to the standard Zimbra Analyzer
            return ZimbraAnalyzer.getTokenStream(field, reader);
        }
    }

}
