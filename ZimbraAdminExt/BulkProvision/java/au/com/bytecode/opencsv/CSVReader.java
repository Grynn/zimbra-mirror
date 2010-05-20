/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package au.com.bytecode.opencsv;

/**
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A very simple CSV reader released under a commercial-friendly license.
 * 
 * @author Glen Smith
 * @Modified Charles Cao
 * 
 */
public class CSVReader {

    private BufferedReader br;

    private boolean hasNext = true;

    private char separator;

    private char quotechar;
    
    private int skipLines;

    private boolean linesSkiped;

    /** The default separator to use if none is supplied to the constructor. */
    public static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    
    /**
     * The default line to start reading.
     */
    public static final int DEFAULT_SKIP_LINES = 0;

    /**
     * Constructs CSVReader using a comma for the separator.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     */
    public CSVReader(Reader reader) {
        this(reader, DEFAULT_SEPARATOR);
    }

    /**
     * Constructs CSVReader with supplied separator.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries.
     */
    public CSVReader(Reader reader, char separator) {
        this(reader, separator, DEFAULT_QUOTE_CHARACTER);
    }
    
    

    /**
     * Constructs CSVReader with supplied separator and quote char.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     */
    public CSVReader(Reader reader, char separator, char quotechar) {
        this(reader, separator, quotechar, DEFAULT_SKIP_LINES);
    }
    
    /**
     * Constructs CSVReader with supplied separator and quote char.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     * @param line
     *            the line number to skip for start reading 
     */
    public CSVReader(Reader reader, char separator, char quotechar, int line) {
        this.br = new BufferedReader(reader);
        this.separator = separator;
        this.quotechar = quotechar;
        this.skipLines = line;
    }

    /**
     * Reads the entire file into a List with each element being a String[] of
     * tokens.
     * 
     * @return a List of String[], with each String[] representing a line of the
     *         file.
     * 
     * @throws IOException
     *             if bad things happen during the read
     */
    public List<String[]> readAll() throws IOException {

        List<String[]> allElements = new ArrayList<String[]>();
        while (hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null)
                allElements.add(nextLineAsTokens);
        }
        return allElements;

    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     * 
     * @return a string array with each comma-separated element as a separate
     *         entry.
     * 
     * @throws IOException
     *             if bad things happen during the read
     */
    public String[] readNext() throws IOException {

        String nextLine = getNextLine();
        return hasNext ? parseLine(nextLine) : null;
    }

    /**
     * Reads the next line from the file.
     * 
     * @return the next line from the file without trailing newline
     * @throws IOException
     *             if bad things happen during the read
     */
    private String getNextLine() throws IOException {
    	if (!this.linesSkiped) {
            for (int i = 0; i < skipLines; i++) {
                br.readLine();
            }
            this.linesSkiped = true;
        }
        String nextLine = br.readLine();
        if (nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    /**
     * Parses an incoming String and returns an array of elements.
     * 1) fields that contain commas, double-quotes, or line-breaks must be quoted,
    *  2) a quote within a field must be escaped with an additional quote immediately preceding the literal quote,
    *  3) space before and after delimiter commas may be trimmed (which is prohibited by RFC 4180), and
    *  4) a line break within an element must be preserved.  

     * 
     * @param nextLine
     *            the string to parse
     * @return the comma-tokenized list of elements, or null if nextLine is null
     * @throws IOException if bad things happen during the read
     */

    private String[] parseLine(String nextLine) throws IOException {
        if (nextLine == null) {
            return null;
        }

        List tokensOnThisLine = new ArrayList();
        StringBuffer sb = new StringBuffer();
        Stack quoteStack = new Stack () ;    //keep the position of the quotes
        int quoteIndex ;

        for (int i=0; i < nextLine.length();i++) {
            char c = nextLine.charAt(i) ;
            if (c == quotechar) {
               if (quoteStack.empty()) {
                   //it is the first quote
                   quoteStack.push(i) ;
               }else{
                   quoteIndex = ((Integer) quoteStack.peek()).intValue() ;
                   if (quoteIndex == i - 1) { //it is an escaped quote, eg.  abcd""efg  - quote at index(5)
                       sb.append(c) ;
                       quoteStack.pop(); //pop the previous quote
                   } else if (i + 1 >= nextLine.length()) {
                        //quote is the last character
                        quoteStack.pop();
                   } else {
                       char nextChar = nextLine.charAt(i+1) ;
                       if (nextChar == quotechar) {
                           //it is the start of an escaped quote, "abcd""efg" - quote at index(5)
                           quoteStack.push(i) ; //push this quote into the stack
                       } else {
                           // or it is the closing quote, eg: "abcd" , efg - quote at index(5)
                           // try to find the separate or the end of the line
                           int j = i ;
                           for (; j < nextLine.length() - 1; j++) {
                               nextChar = nextLine.charAt (j+1) ;
                                if (nextChar == separator) {
                                    break ;
                                } else if (nextChar != ' ') { //only whitesapces can exist before the separate or the end of the quote
                                    throw new IOException ("Parsing Error: invalid line at position (" + j
                                             + ") character(" + nextChar + "):  " + nextLine) ;
                                }
                           }
                           i = j ;  //let the main loop handles the separator
                           quoteStack.pop(); //pop the previous quote
//                           tokensOnThisLine.add(sb.toString()) ;
//                           sb = new StringBuffer () ;
//                           continue ;
                       }

                   }
               }
            } else if (c == separator ) {
                if (quoteStack.empty()) {
                    //end of the field
                    tokensOnThisLine.add(sb.toString()) ;
                    sb = new StringBuffer () ;
                }else{
                    //inside an quoted entry
                    sb.append(c) ;
                }
            } else {
                sb.append(c) ;
            }

            if (i+1 >= nextLine.length()) {
                if (quoteStack.empty()) {
                    //end of the field
                    tokensOnThisLine.add(sb.toString()) ;
                    sb = new StringBuffer () ;
                    continue ;
                }else{
                    //inside an quoted entry with the new line
                    sb.append("\n") ;
                    String theFollowingLine =  getNextLine();
                    if (theFollowingLine == null) {
                        break ;
                    }else {
                        nextLine = nextLine + theFollowingLine;
                    }
                }
            }
        }
        

        if (!quoteStack.empty()) { //make sure all the quotes are closed
              throw new IOException ("Parsing Error: invalid line: " + nextLine) ;
        }
        
        return (String []) tokensOnThisLine.toArray(new String[0]);
    }

    /*
    private String[] parseLine(String nextLine) throws IOException {

        if (nextLine == null) {
            return null;
        }

        List tokensOnThisLine = new ArrayList();
        StringBuffer sb = new StringBuffer();
        boolean inQuotes = false;
        do {
        	if (inQuotes) {
                // continuing a quoted section, reappend newline
                sb.append("\n");
                nextLine = getNextLine();
                if (nextLine == null)
                    break;
            }
            for (int i = 0; i < nextLine.length(); i++) {

                char c = nextLine.charAt(i);
                if (c == quotechar) {
                	// this gets complex... the quote may end a quoted block, or escape another quote.
                	// do a 1-char lookahead:
                	if( inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
                	    && nextLine.length() > (i+1)  // there is indeed another character to check.
                	    && nextLine.charAt(i+1) == quotechar ){ // ..and that char. is a quote also.
                		// we have two quote chars in a row == one quote char, so consume them both and
                		// put one on the token. we do *not* exit the quoted text.
                		sb.append(nextLine.charAt(i+1));
                		i++;
                	}else{
                		inQuotes = !inQuotes;
                		// the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                		if(i>2 //not on the begining of the line
                				&& nextLine.charAt(i-1) != this.separator //not at the begining of an escape sequence 
                				&& nextLine.length()>(i+1) &&
                				nextLine.charAt(i+1) != this.separator //not at the	end of an escape sequence
                		){
                			sb.append(c);
                		}
                	}
                } else if (c == separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString());
                    sb = new StringBuffer(); // start work on next token
                } else {
                    sb.append(c);
                }
            }
        } while (inQuotes);
        tokensOnThisLine.add(sb.toString());
        return (String[]) tokensOnThisLine.toArray(new String[0]);

    }   */

    /**
     * Closes the underlying reader.
     * 
     * @throws IOException if the close fails
     */
    public void close() throws IOException{
    	br.close();
    }
    
}
