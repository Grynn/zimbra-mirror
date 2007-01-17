package com.zimbra.cs.im.xp.parse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;

public class NonblockingCallbackParser implements Application {
    
    DocumentFactory mDf = DocumentFactory.getInstance();
    
    List<Element> mCompletedElements = new ArrayList<Element>();
//    Element initialStreamElt = null;
    Element curElt = null;
    int mCurDepth = 0;
    
    public static String trim(String s) {
        s = s.trim();
        if (s.endsWith("\n"))
            s = s.substring(0, s.length()-1);
        return s;
    }
    
    int mIndent = 0;
    
    String indent() { 
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
        
        if (curElt != null) 
            curElt.addText(s);
        
        if (s.endsWith("\n")) 
            s = s.substring(0, s.length()-1);
        System.out.print(s);
    }
    public void comment(CommentEvent event) throws Exception {
        System.out.println("comment "+event.getComment());
    }
    public void endCdataSection(EndCdataSectionEvent event) throws Exception {
        System.out.println("endCdataSection "+event);
    }
    public void endDocument() throws Exception {
        System.out.println("endDocument ");
    }
    public void endDocumentTypeDeclaration(EndDocumentTypeDeclarationEvent event) throws Exception {
        System.out.println("*************!!!!!!!!!!!!endDocumentTypeDeclaration "+event);
    }
    public void endEntityReference(EndEntityReferenceEvent event) throws Exception {
        System.out.println("endEntityReference "+event);
    }
    public void endProlog(EndPrologEvent event) throws Exception {
        System.out.println("endProlog "+event);
    }
    public void markupDeclaration(MarkupDeclarationEvent event) throws Exception {
        System.out.println("markupDeclaration "+event.getName()+" attrib="+event.getAttributeName());
    }
    public void processingInstruction(ProcessingInstructionEvent event) throws Exception {
        System.out.println("processingInstruction "+event.getName()+" instruct="+event.getInstruction());
    }
    public void startCdataSection(StartCdataSectionEvent event) throws Exception {
        System.out.println("startCdataSection "+event);
    }
    public void startDocument() throws Exception {
        System.out.println("startDocument");
    }
    public void startDocumentTypeDeclaration(StartDocumentTypeDeclarationEvent event) throws Exception {
        System.out.println("startDocumentTypeDeclaration "+ event);
    }
    public void startElement(StartElementEvent event) throws Exception {
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
        
        ////////////////////////////////////
        String name = event.getName();
        if (name.equals("stream:stream")) {
            Element newElement = createStartElement(event, true);
            mCompletedElements.add(newElement);
        } else {
            Element newElement = createStartElement(event, false);
            if (curElt != null) {
                curElt.add(newElement);
            }
            mCurDepth++;
            curElt = newElement;
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
        String[] split = splitPrefix(event.getName());
        String prefix = split[0];
        String name = split[1];
        String myNsName = "xmlns";
        String ns;
        if (prefix != null) {
            myNsName = "xmlns:"+prefix;
        }
        ns = event.getAttributeValue(myNsName);
        if (!includeJabberNS && (ns == null || ns.equals("jabber:client") || ns.equals("jabber:server") ||
                    ns.equals("jabber.connectionmanager") || ns.equals("jabber:component:accept"))) {
            newElement = mDf.createElement(name);
        } else {
            QName qname;
            if (prefix == null)
                qname = mDf.createQName(name, ns); 
            else
                qname = mDf.createQName(name, prefix, ns);
            newElement = mDf.createElement(qname);
        }
        
        String asXML = newElement.asXML();
        System.out.println(asXML);
        
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
        
        asXML = newElement.asXML();
        System.out.println(asXML);

        return newElement;
    }
    
    public void endElement(EndElementEvent event) throws Exception {
        System.out.println();
        indentIn();
        System.out.print(indent()+"</"+trim(event.getName())+">");
        
        ////////////////////////////////////
        String name = event.getName();
        if (!name.equals("stream:stream")) {
            assert(curElt != null);
            assert(mCurDepth >= 1);
            mCurDepth--;
            
            if (mCurDepth < 1) {
                mCompletedElements.add(curElt);
                mCurDepth = 0;
                curElt = null;
            }
            if (curElt != null)
                curElt = curElt.getParent();
        }
    }
    public void startEntityReference(StartEntityReferenceEvent event) throws Exception {
        System.out.println("startEntityReference "+event.getName());
    }
    
    Locale mLocale;
    EntityParser mEp = null;
    byte[] mInitialBuf = null; // our initial startup must have at least 4 bytes,
      // this is used to get there
    
    
    
    public NonblockingCallbackParser(Locale locale) {
        mLocale = locale;
    }
    
    public void setEof() {
        if (mEp != null)
            mEp.setEof();
    }
    
//    public Element getInitialStreamElement()  { return initialStreamElt; }
//    public void clearInitialStreamElement() { initialStreamElt = null; }
    
    public List<Element> getCompletedElements() { return mCompletedElements; }
    public void clearCompletedElements() { mCompletedElements.clear(); }
    
    public void parseBytes(byte[] b, int len) throws IOException, ApplicationException  {
        
        if (len == 0)
            return;
        
        
        if (mEp == null) {
            if (mInitialBuf != null) {
                byte[] newInitial = new byte[mInitialBuf.length + len];
                System.arraycopy(mInitialBuf, 0, newInitial, 0, mInitialBuf.length);
                System.arraycopy(b, 0, newInitial, mInitialBuf.length, len);
                mInitialBuf = newInitial;
            } else {
                if (len > b.length) {
                    mInitialBuf = new byte[len];
                    System.arraycopy(b, 0, mInitialBuf, 0, len);
                } else {
                    mInitialBuf = b;
                }
            }
            
            if (mInitialBuf.length >= 4) {
                OpenEntity oe = new OpenEntity(null, null, null, null);
                mEp = new EntityParser(mInitialBuf, oe, null, this, mLocale, null);
                mEp.parseContent(false, true);
                mInitialBuf = null;
            } 
        } else {
            mEp.addBytes(b, len);
            mEp.parseContent(false, true);
        }
    }
}
