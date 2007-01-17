/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
 
package com.zimbra.cs.im.xp.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import com.zimbra.cs.im.xp.util.NonblockingInputStream;

public class Tester {
    
    public static String trim(String s) {
        s = s.trim();
        if (s.endsWith("\n"))
            s = s.substring(0, s.length()-1);
        return s;
    }
    
    static public class MyApp implements Application {

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
        public void endElement(EndElementEvent event) throws Exception {
            System.out.println();
            indentIn();
            System.out.print(indent()+"</"+trim(event.getName())+">");
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
        }
        public void startEntityReference(StartEntityReferenceEvent event) throws Exception {
            System.out.println("startEntityReference "+event.getName());
        }
    }
    
    public static void main(String[] args) {
//        EntityManager em = new MyEntityManager();
        try {
            File file = new File("/tmp/foo.txt");
//            MyInputStream mys = new MyInputStream(new FileInputStream(file));
            NonblockingInputStream nis  = new NonblockingInputStream();
            FileInputStream fis = new FileInputStream(file);
            OpenEntity oe = new OpenEntity(nis, file.getName(), EntityManagerImpl.fileToURL(file), null);
                
            EntityManagerImpl.openFile("/tmp/foo.txt");
            EntityManager em = new EntityManagerImpl();
            Application app = new MyApp();
            Locale loc = Locale.getDefault();

//          EntityParser ep = new EntityParser(oe, em, app, loc, null);
//          ep.parseDocumentEntity();
          
//            EntityParser ep = null;
//            
//            {
//                int readSize = 4;
//                byte b[] = new byte[readSize];
//                fis.read(b);
////                nis.addBytes(b);
//                ep = new EntityParser(b, oe, em, app, loc, null);
//            }
            NonblockingCallbackParser nbp = new NonblockingCallbackParser(Locale.getDefault());

            while(!nis.eof()) {
                System.out.print(".");
//                ep.parseContent(true, true);
                
                int readSize = (int)(Math.round((Math.random()*3))); 
                byte b[] = new byte[readSize];
                int read = fis.read(b);
                if (read <0) {
//                    nis.setEof();
//                    ep.setEof();
                    nbp.setEof();
                    break;
                } else { 
//                    nis.addBytes(b);
//                    ep.addBytes(b, read);
                    nbp.parseBytes(b, read);
                }
            }
            
        } catch(IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
            
        } catch(ApplicationException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
                
        
    }        

}
