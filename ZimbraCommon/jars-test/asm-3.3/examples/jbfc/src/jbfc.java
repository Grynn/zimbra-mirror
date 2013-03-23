/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/***
 * ASM examples: examples showing how ASM can be used
 * Copyright (c) 2000-2007 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.jbfc.BFCompiler;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * A naive implementation of compiler for Brain**** language.
 * http://www.muppetlabs.com/~breadbox/bf/ *
 * 
 * @author Eugene Kuleshov
 */
public class jbfc {

    public static void main(final String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: jbfc [-v] <bf program file> <java class name>");
            return;
        }

        boolean verbose = false;
        String fileName = null;
        String className = null;
        for (int i = 0; i < args.length; i++) {
            if ("-v".equals(args[i])) {
                verbose = true;
            } else {
                fileName = args[i];
                className = args[i + 1];
                break;
            }
        }

        FileReader r = new FileReader(fileName);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        BFCompiler c = new BFCompiler();
        if (verbose) {
            c.compile(r, className, fileName, new TraceClassVisitor(cw,
                    new PrintWriter(System.out)));
        } else {
            c.compile(r, className, fileName, cw);
        }

        r.close();

        FileOutputStream os = new FileOutputStream(className + ".class");
        os.write(cw.toByteArray());
        os.flush();
        os.close();
    }

}
