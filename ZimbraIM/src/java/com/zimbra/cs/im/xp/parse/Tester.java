package com.zimbra.cs.im.xp.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.mina.core.buffer.IoBuffer;
import org.jivesoftware.wildfire.net.NioParser;

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

        @Override
        public void characterData(CharacterDataEvent event) throws Exception {
            StringWriter sw = new StringWriter();
            event.writeChars(sw);
            String s = sw.toString();
            if (s.endsWith("\n"))
                s = s.substring(0, s.length()-1);
            System.out.print(s);
        }

        @Override
        public void comment(CommentEvent event) throws Exception {
            System.out.println("comment "+event.getComment());
        }

        @Override
        public void endCdataSection(EndCdataSectionEvent event) throws Exception {
            System.out.println("endCdataSection "+event);
        }

        @Override
        public void endDocument() throws Exception {
            System.out.println("endDocument ");
        }

        @Override
        public void endDocumentTypeDeclaration(EndDocumentTypeDeclarationEvent event) throws Exception {
            System.out.println("*************!!!!!!!!!!!!endDocumentTypeDeclaration "+event);
        }

        @Override
        public void endElement(EndElementEvent event) throws Exception {
            System.out.println();
            indentIn();
            System.out.print(indent()+"</"+trim(event.getName())+">");
        }

        @Override
        public void endEntityReference(EndEntityReferenceEvent event) throws Exception {
            System.out.println("endEntityReference "+event);
        }

        @Override
        public void endProlog(EndPrologEvent event) throws Exception {
            System.out.println("endProlog "+event);
        }

        @Override
        public void markupDeclaration(MarkupDeclarationEvent event) throws Exception {
            System.out.println("markupDeclaration "+event.getName()+" attrib="+event.getAttributeName());
        }

        @Override
        public void processingInstruction(ProcessingInstructionEvent event) throws Exception {
            System.out.println("processingInstruction "+event.getName()+" instruct="+event.getInstruction());
        }

        @Override
        public void startCdataSection(StartCdataSectionEvent event) throws Exception {
            System.out.println("startCdataSection "+event);
        }

        @Override
        public void startDocument() throws Exception {
            System.out.println("startDocument");
        }

        @Override
        public void startDocumentTypeDeclaration(StartDocumentTypeDeclarationEvent event) throws Exception {
            System.out.println("startDocumentTypeDeclaration "+ event);
        }

        @Override
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

        @Override
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
            NioParser nbp = new NioParser(Locale.getDefault());

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
                    nbp.parseBytes(IoBuffer.wrap(b, 0, read));
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
