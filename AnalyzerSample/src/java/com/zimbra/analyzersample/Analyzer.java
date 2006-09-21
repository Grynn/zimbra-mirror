package com.zimbra.analyzersample;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;

import com.zimbra.cs.index.ZimbraAnalyzer;

public class Analyzer extends ZimbraAnalyzer {
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return super.tokenStream(fieldName, reader);
    }
}
