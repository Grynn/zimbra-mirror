/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.wildfire.net;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.mina.common.ByteBuffer;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.jivesoftware.util.Log;

import com.zimbra.cs.im.xp.parse.Application;
import com.zimbra.cs.im.xp.parse.ApplicationException;
import com.zimbra.cs.im.xp.parse.CharacterDataEvent;
import com.zimbra.cs.im.xp.parse.CommentEvent;
import com.zimbra.cs.im.xp.parse.EndCdataSectionEvent;
import com.zimbra.cs.im.xp.parse.EndDocumentTypeDeclarationEvent;
import com.zimbra.cs.im.xp.parse.EndElementEvent;
import com.zimbra.cs.im.xp.parse.EndEntityReferenceEvent;
import com.zimbra.cs.im.xp.parse.EndPrologEvent;
import com.zimbra.cs.im.xp.parse.EntityParser;
import com.zimbra.cs.im.xp.parse.MarkupDeclarationEvent;
import com.zimbra.cs.im.xp.parse.OpenEntity;
import com.zimbra.cs.im.xp.parse.ProcessingInstructionEvent;
import com.zimbra.cs.im.xp.parse.StartCdataSectionEvent;
import com.zimbra.cs.im.xp.parse.StartDocumentTypeDeclarationEvent;
import com.zimbra.cs.im.xp.parse.StartElementEvent;
import com.zimbra.cs.im.xp.parse.StartEntityReferenceEvent;

public class NioParser implements Application {
    
    DocumentFactory mDf = DocumentFactory.getInstance();
    
    List<Element> mCompletedElements = new ArrayList<Element>();
    Element mCurElt = null;
    Element mStreamElt = null;
    int mCurDepth = 0;
    int mIndent = 0;
    final static boolean SPEW = false;
    
    private static String trim(String s) {
        s = s.trim();
        if (s.endsWith("\n"))
            s = s.substring(0, s.length()-1);
        return s;
    }
    
    private String indent() { 
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mIndent; i++)
            sb.append(' ');
        return sb.toString();
    }
    
    void indentOut() { mIndent+=3; }
    void indentIn() { mIndent-=3; }

    public void characterData(CharacterDataEvent event) throws Exception {
        StringWriter sw = new StringWriter();
        event.writeChars(sw);
        String s = sw.toString();
        
        if (mCurElt != null) { 
            mCurElt.addText(s);
        }
        
        if (SPEW) {
            if (s.endsWith("\n")) 
                s = s.substring(0, s.length()-1);
            System.out.print(s);
        }
    }
    public void comment(CommentEvent event) throws Exception {
        if (SPEW) { System.out.println("comment "+event.getComment()); }
    }
    public void endCdataSection(EndCdataSectionEvent event) throws Exception {
        if (SPEW) { System.out.println("endCdataSection "+event); }
    }
    public void endDocument() throws Exception {
        if (SPEW) { System.out.println("endDocument "); }
    }
    public void endDocumentTypeDeclaration(EndDocumentTypeDeclarationEvent event) throws Exception {
        if (SPEW) { System.out.println("endDocumentTypeDeclaration "+event); }
    }
    public void endEntityReference(EndEntityReferenceEvent event) throws Exception {
        if (SPEW) { System.out.println("endEntityReference "+event); }
    }
    public void endProlog(EndPrologEvent event) throws Exception {
        if (SPEW) { System.out.println("endProlog "+event); }
    }
    public void markupDeclaration(MarkupDeclarationEvent event) throws Exception {
        if (SPEW) { System.out.println("markupDeclaration "+event.getName()+" attrib="+event.getAttributeName()); } 
    }
    public void processingInstruction(ProcessingInstructionEvent event) throws Exception {
        if (SPEW) { System.out.println("processingInstruction "+event.getName()+" instruct="+event.getInstruction()); }
    }
    public void startCdataSection(StartCdataSectionEvent event) throws Exception {
        if (SPEW) { System.out.println("startCdataSection "+event); }
    }
    public void startDocument() throws Exception {
        if (SPEW) { System.out.println("startDocument"); }
    }
    public void startDocumentTypeDeclaration(StartDocumentTypeDeclarationEvent event) throws Exception {
        if (SPEW) { System.out.println("startDocumentTypeDeclaration "+ event); }
    }
    public void startElement(StartElementEvent event) throws Exception {
        if (SPEW) {
            System.out.println();
            System.out.print(indent()+"<"+trim(event.getName()) );
            indentOut();
            if (event.getAttributeCount() > 0) {
                System.out.print(" ");
                for (int i = event.getAttributeCount()-1; i>=0; i--) {
                    System.out.print(trim(event.getAttributeName(i))+"=\""+trim(event.getAttributeValue(i))+"\"");
                    if (i > 0)
                        System.out.print(" ");
                }
            }
            System.out.print(">");
        }
        
        ////////////////////////////////////
        String name = event.getName();
        
        // special case the stream element -- we want each XMPP *stanza* returned as a single
        // Dom4j Document -- but the stanzas are enclosed in the outer <stream:stream>...
        // so basically we hack things so that the <stream:stream> is returned immediately
        // on the StartElement, and then from there on we returns stanzas as individual docs.
        if (name.equals("stream:stream")) {
            Element newElement = createStartElement(event, true);
            mStreamElt = newElement;
            mCompletedElements.add(newElement);
        } else {
            Element newElement = createStartElement(event, false);
            if (mCurElt != null) {
                mCurElt.add(newElement);
            }
            mCurDepth++;
            mCurElt = newElement;
        }
    }
    
    /**
     * foo:bar --> { foo, bar }
     * foo --> { null, foo }
     * 
     * @param s
     * @return
     */
    private String[] splitPrefix(String s) {
        String[] toRet = new String[2];
        
        int prefixIdx = s.indexOf(':');
        if (prefixIdx > 0) {
            toRet[0] = s.substring(0, prefixIdx);
            toRet[1] = s.substring(prefixIdx+1);
        } else {
            toRet[1] = s;
        }
        return toRet;
    }
    
    private Element createStartElement(StartElementEvent event, boolean includeJabberNS) 
    {
        Element newElement = null;
//        if (event.getName().equals("stream:features"))
//            System.out.println("at features...");
        
        String fullname = event.getName();
        String[] split = splitPrefix(fullname);
        String prefix = split[0];
        String name = split[1];
        String myNsName = "xmlns";
        String ns;
        if (prefix != null) {
            myNsName = "xmlns:"+prefix;
        }
        ns = event.getAttributeValue(myNsName);
        if (prefix != null && ns == null && this.mStreamElt != null) {
            // can't resolve this namespace in the current element: check the <stream> element
            // to see if it defined this namespace prefix...
            Namespace n= this.mStreamElt.getNamespaceForPrefix(prefix);
            if (n != null)
                ns = n.getURI();
        }
        if (!includeJabberNS && (
                        ns == null || 
                        ns.equals("jabber:client") || 
                        ns.equals("jabber:server") ||
                        ns.equals("jabber.connectionmanager") || 
                        ns.equals("jabber:component:accept")) ||
                        ns.equals("jabber:cloudrouting")
        ) {
            newElement = mDf.createElement(fullname);
        } else {
            QName qname;
            if (prefix == null)
                qname = mDf.createQName(name, ns); 
            else
                qname = mDf.createQName(name, prefix, ns);
            newElement = mDf.createElement(qname);
        }
        
        for (int i = 0; i < event.getAttributeCount(); i++) {
            String attrName = event.getAttributeName(i);
            if (attrName.startsWith("xmlns")) {
                String attrValue = event.getAttributeValue(i);
                if (attrValue != null && attrValue.length() > 0) {
                    if (!attrName.equals(myNsName)) {
                        split = splitPrefix(attrName);
                        if (split[0] != null) { 
                            newElement.addNamespace(split[1], attrValue);
                        } else {
                            newElement.addNamespace("",attrValue);
                        }
                    }
                }
            } else {
                newElement.addAttribute(attrName, event.getAttributeValue(i));
            }
        }
        
//        if (SPEW) {
//            System.out.print("NioParser: ");
//            String asXML = newElement.asXML();
//            System.out.println(asXML);
//        }

        return newElement;
    }
    
    public void endElement(EndElementEvent event) throws Exception {
        if (SPEW) { 
            System.out.println();
            indentIn();
            System.out.print(indent()+"</"+trim(event.getName())+">");
        }
        
        ////////////////////////////////////
        String name = event.getName();
        if (!name.equals("stream:stream")) {
            assert(mCurElt != null);
            assert(mCurDepth >= 1);
            mCurDepth--;
            
            if (mCurDepth < 1) {
                mCompletedElements.add(mCurElt);
                mCurDepth = 0;
                mCurElt = null;
            }
            if (mCurElt != null)
                mCurElt = mCurElt.getParent();
        }
    }
    public void startEntityReference(StartEntityReferenceEvent event) throws Exception {
        if (SPEW) { System.out.println("startEntityReference "+event.getName()); }
    }
    
    Locale mLocale;
    EntityParser mEp = null;
    boolean mPastProlog = false;
    private byte[] mInitialBuf = null;
    private static byte[] sXMLPrologBytes = new byte[] { '<', '?', 'x', 'm', 'l' };
    private static byte[] sXMLPrologEndBytes = new byte[] { '?', '>' };
    private static byte[] sStreamStartBytes = new byte[] {
        '<', 's', 't', 'r', 'e', 'a', 'm', ':', 's', 't', 'r', 'e', 'a', 'm'
    };
    
    
    
    public NioParser(Locale locale) {
        mLocale = locale;
    }
    
    public void setEof() {
        if (mEp != null)
            mEp.setEof();
    }
    
    public List<Element> getCompletedElements() { return mCompletedElements; }
    public void clearCompletedElements() { mCompletedElements.clear(); }
    
    /**
     * 
     * just like lhs.indexOf(rhs) for Strings
     * 
     * @param lhs
     * @param rhs
     * @return
     */
    private int byteArrayIndexOf(byte[] lhs, byte[] rhs) {
        if (lhs.length < rhs.length)
            throw new IllegalArgumentException("byteArrayIndexOf: lhs must be larger or same length as rhs (parameters in wrong order?)");
        
        for (int start = 0; start < lhs.length; start++) {
            if (lhs.length - start < rhs.length)
                return -1;
            
            if (lhs[start] == rhs[0]) {
                boolean eq = true;
                for (int i = 1; i < rhs.length; i++) {
                    if (lhs[start+i] != rhs[i]) {
                        eq = false;
                        break;
                    }
                }
                if (eq)
                    return start;
            }
        }
        return -1; 
    }
    
    public static class NioParserException extends IOException {
        public NioParserException(String s) {
            mWhy = s;
        }
        private String mWhy;
        public String toString() { return mWhy; }
    }
    
    public void parseBytes(ByteBuffer bb) throws IOException, ApplicationException  
    {
        if (!bb.hasRemaining())
            return;
        
        
        // TODO, eliminate double-buffering here (make parser ByteBuffer-aware)
        // be careful: parser assumes it can take ownership of byte[], need to modify code
        // to remove this assumption if we convert things to ByteBuffers
        byte[] buf= new byte[bb.remaining()];
        bb.get(buf);
        
        if (SPEW) {
            StringBuilder sb = new StringBuilder();
            for (byte b : buf) 
                sb.append((char)b);
            Log.debug("Parsing: "+sb.toString());
        }
        
        
        if (mEp == null) {
            // append new data into initial buf (create it if necessary) 
            if (mInitialBuf != null) {
                byte[] newInitial = new byte[mInitialBuf.length + buf.length];
                System.arraycopy(mInitialBuf, 0, newInitial, 0, mInitialBuf.length);
                System.arraycopy(buf, 0, newInitial, mInitialBuf.length, buf.length);
                mInitialBuf = newInitial;
            } else {
                mInitialBuf = buf;
            }

            // see if we can find the <? xml ... ?> (+ 4 bytes of text)...
            if (mInitialBuf != null && mInitialBuf.length >= sXMLPrologBytes.length) {
                int index = byteArrayIndexOf(mInitialBuf, sXMLPrologBytes);
                if (index >= 0) {
                    // found <?xml> part                    
                    int endIdx = byteArrayIndexOf(mInitialBuf, sXMLPrologEndBytes);
                    if (endIdx > 0) {
                        if (endIdx <= index) {
                            StringBuilder sb = new StringBuilder();
                            for (byte b : mInitialBuf) 
                                sb.append((char)b);
                            throw new NioParserException("Garbage at beginning of stream: \""+sb.toString()+"\"");
                        } else {
                            int endPrologIdx = endIdx + sXMLPrologEndBytes.length;
                            int leftoverLen = mInitialBuf.length-endPrologIdx;
                            if (leftoverLen >= 4) { // parser needs 4 initial bytes to start parsing
                                Log.info("Handshaking complete for client");
                                byte[] leftover = new byte[mInitialBuf.length - endPrologIdx];
                                System.arraycopy(mInitialBuf, endPrologIdx, leftover, 0, leftoverLen);
                                mInitialBuf = null;
                                OpenEntity oe = new OpenEntity(null, null, null, null);
                                mEp = new EntityParser(leftover, oe, null, this, mLocale, null);
                                mEp.parseContent(false, true);
                            }
                        }
                    }
                } else if ((index = byteArrayIndexOf(mInitialBuf, sStreamStartBytes)) >= 0) {
                    // found a <stream:stream part 
                    int leftoverLen = mInitialBuf.length-index;
                    Log.info("Handshaking complete for client");
                    byte[] leftover = new byte[mInitialBuf.length - index];
                    System.arraycopy(mInitialBuf, index, leftover, 0, leftoverLen);
                    mInitialBuf = null;
                    OpenEntity oe = new OpenEntity(null, null, null, null);
                    mEp = new EntityParser(leftover, oe, null, this, mLocale, null);
                    mEp.parseContent(false, true);
                }
            }
                
            // sanity check
            if (mEp == null && mInitialBuf != null && mInitialBuf.length > 2048) {
                StringBuilder sb = new StringBuilder();
                for (byte b : mInitialBuf) 
                    sb.append((char)b);
                mInitialBuf = null;
                throw new NioParserException("Invalid handshake at beginning of stream: \""+sb.toString()+"\"");
            }
        } else {
            mEp.addBytes(buf, buf.length);
            mEp.parseContent(false, true);
        }
    }
}
