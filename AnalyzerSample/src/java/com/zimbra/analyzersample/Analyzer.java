/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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

import org.apache.lucene.analysis.TokenStream;

import com.zimbra.cs.index.LuceneFields;
import com.zimbra.cs.index.ZimbraAnalyzer;

/**
 * Our sample "customized" Analyzer: all we do is pass all requests on do the
 * default Zimbra analyzer.
 */
public class Analyzer extends ZimbraAnalyzer {

    /**
     * Return a {@link TokenStream} instance depending on the specified Field
     * and, potentially, the contents of the Reader itself.
     *
     * There are a number of different Lucene fields that get analyzed. In many
     * cases our custom analyzer will not want to change the default behavior
     * (e.g. L_MIMETYPE which is guaranteed to be ASCII) but for other fields
     * (e.g. L_CONTENT) we probably do want to plug in.
     */
    @Override
    public TokenStream tokenStream(String field, Reader reader) {
        if (field.equals(LuceneFields.L_ATTACHMENTS) ||
                field.equals(LuceneFields.L_MIMETYPE)) {
            // L_ATTACHMENTS: comma-separated list of attachment types
            //  e.g. "application/binary, application/pdf"
            // L_MIMETYPE: (possibly comma-separated list) mime type,
            //  e.g. "text/plain"
            return super.tokenStream(field, reader);
        } else if (field.equals(LuceneFields.L_SORT_SIZE)) {
            // L_SORT_SIZE: numeric size, tokenize via NumberTokenStream
            return super.tokenStream(field, reader);
        } else if (field.equals(LuceneFields.L_H_FROM)
                || field.equals(LuceneFields.L_H_TO)
                || field.equals(LuceneFields.L_H_CC)) {
            // L_H_FROM, L_H_TO, L_H_CC are the RFC822 headers.
            // Custom analyzers must pass these up to the inherited TokenStream.
            return super.tokenStream(field, reader);
        } else if (field.equals(LuceneFields.L_FILENAME)) {
            return super.tokenStream(field, reader);
        } else if (field.equals(LuceneFields.L_CONTENT)) {
            // L_CONTENT field means all the text in the document.
            // We probably want to plug our custom analyzer in here
            return super.tokenStream(field, reader);

        } else if (field.equals(LuceneFields.L_H_SUBJECT)) {
            // L_H_SUBJECT field means the subject of the document.
            // We probably want to plug our custom analyzer in here
            return super.tokenStream(field, reader);

        } else {
            // Catch-All: probably want to pass this up to the standard Zimbra Analyzer
            return super.tokenStream(field, reader);
        }
    }

}
