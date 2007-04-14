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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.yahoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.zimbra.common.util.Pair;

/**
 * 
 */
public class YMSGDecoder implements MessageDecoder {
    
    YMSGHeader mHeader = null;
    
    /* (non-Javadoc)
     * @see org.apache.mina.filter.codec.demux.MessageDecoder#decodable(org.apache.mina.common.IoSession, org.apache.mina.common.ByteBuffer)
     */
    public MessageDecoderResult decodable(IoSession session, ByteBuffer in) {
        // Return NEED_DATA if the whole header is not read yet.
        if (in.remaining() < YMSGHeader.HEADER_LENGTH) {
            return MessageDecoderResult.NEED_DATA;
        }
        
        if (mHeader == null) {
            byte[] hdrBuf = new byte[YMSGHeader.HEADER_LENGTH];
            in.get(hdrBuf);
            try {
                mHeader = YMSGHeader.parse(hdrBuf);
            } catch(IOException e) {
                return MessageDecoderResult.NOT_OK;
            }
        }
        
        if (in.remaining() >= mHeader.length)
            return MessageDecoderResult.OK;
        else
            return MessageDecoderResult.NEED_DATA;
    }
    

    /* (non-Javadoc)
     * @see org.apache.mina.filter.codec.demux.MessageDecoder#decode(org.apache.mina.common.IoSession, org.apache.mina.common.ByteBuffer, org.apache.mina.filter.codec.ProtocolDecoderOutput)
     */
    public MessageDecoderResult decode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
        in.skip(YMSGHeader.HEADER_LENGTH);
        byte[] buf = new byte[mHeader.length];
        in.get(buf);
        
//        StringBuilder sb = new StringBuilder();
//        for (byte b : buf) {
//            sb.append(YMSGBufUtils.toHex(b)).append(' ');
//        }
//        System.err.println("decoding:\n\t"+sb.toString());
        
//        HashMap<Integer, List<String>> strings = new HashMap<Integer, List<String>>();
        List<Pair<Integer, String>> strings = new ArrayList<Pair<Integer, String>>();
        
        String key = null;
        int startIdx = 0;
        
        for (int i = 0; i < mHeader.length; i++) {
            if (buf[i] == (byte)0xc0 && buf[i+1] == (byte)0x80) {
                String s = new String(buf, startIdx, i-startIdx, "UTF-8");
                if (key == null) {
                    key = s;
                } else {
                    Integer keyInt = Integer.parseInt(key);
//                    List<String> l = strings.get(keyInt);
//                    if (l == null) {
//                        l = new ArrayList<String>(1);
//                        strings.put(keyInt, l);
//                    }
//                    l.add(s);
                    strings.add(new Pair<Integer, String>(keyInt, s));
                    key = null;
                }
                i++; // skip to the 0x80 
                startIdx = i+1; // the NEXT char is the start
            }
        }
        
        out.write(new YMSGPacket(mHeader, strings));
        mHeader = null;
        return MessageDecoderResult.OK;
    }

    /* (non-Javadoc)
     * @see org.apache.mina.filter.codec.demux.MessageDecoder#finishDecode(org.apache.mina.common.IoSession, org.apache.mina.filter.codec.ProtocolDecoderOutput)
     */
    public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
    }

}
