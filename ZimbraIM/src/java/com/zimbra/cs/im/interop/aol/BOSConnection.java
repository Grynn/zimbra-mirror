/*
 * ***** BEGIN LICENSE BLOCK ***** Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 ("License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc. Portions created
 * by Zimbra are Copyright (C) 2005 Zimbra, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.im.interop.aol;

import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.kano.joscar.ByteBlock;
import net.kano.joscar.flap.FlapCommand;
import net.kano.joscar.flap.FlapPacketEvent;
import net.kano.joscar.flapcmd.*;
import net.kano.joscar.snac.SnacPacketEvent;
import net.kano.joscar.snac.SnacResponseEvent;
import net.kano.joscar.snaccmd.*;
import net.kano.joscar.snaccmd.buddy.BuddyOfflineCmd;
import net.kano.joscar.snaccmd.buddy.BuddyStatusCmd;
import net.kano.joscar.snaccmd.conn.*;
import net.kano.joscar.snaccmd.icbm.*;
import net.kano.joscar.snaccmd.loc.*;
import net.kano.joscar.snaccmd.ssi.*;
import net.kano.joscar.ssiitem.DefaultSsiItemObjFactory;
import net.kano.joscar.ssiitem.SsiItemObj;
import net.kano.joscar.ssiitem.SsiItemObjectFactory;

/**
 * BOS - Basic OSCAR Service. This term refers to the services that form the
 * core of the AOL Instant Messenger service. These services include Login/Logoff,
 * Locate, Instant Message, and Buddy List.
 * 
 * This class is our basic connection object.  All connections (except the login connection)
 * are instances of this class.
 */
public class BOSConnection extends AolConnection {

    public BOSConnection(AolMgr mgr, String host, int port, ByteBlock cookie) throws UnknownHostException {
        super(mgr, host, port);
        mCookie = cookie;
    }

    @Override
    public String getInstanceInfo() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "BOSConnection(" + super.toString() + ")";
    }

    @Override
    public void handleFlapPacket(FlapPacketEvent e) {
        super.handleFlapPacket(e);
        FlapCommand cmd = e.getFlapCommand();
        debug("packet had FLAP command: " + cmd);
        if (cmd instanceof LoginFlapCmd) {
            getFlapProcessor().sendFlap(new LoginFlapCmd(mCookie));
        }
    }

    @Override
    public void handleSnacPacket(SnacPacketEvent e) {
        super.handleSnacPacket(e);
        SnacCommand cmd = e.getSnacCommand();
        debug("packet had SNAC command: " + cmd);
        if (cmd instanceof ServerReadyCmd) {
            ServerReadyCmd src = (ServerReadyCmd) cmd;
            int[] families = src.getSnacFamilies();
            setSnacFamilies(families);
            List<SnacFamilyInfo> familyInfos = SnacFamilyInfoFactory.getDefaultFamilyInfos(families);
            mSnacFamilyInfo = familyInfos;
            // tester.registerSnacFamilies(this);
            request(new ClientVersionsCmd(familyInfos));
            request(new RateInfoRequest());
            request(new ParamInfoRequest());
            request(new LocRightsRequest());
            request(new SsiRightsRequest());
            request(new SsiDataRequest());
        } else if (cmd instanceof RecvTypingNotification) {
            RecvTypingNotification r = (RecvTypingNotification) cmd;
            getEventListener().receivedTypingNotification(this, r.getScreenname(), r.getTypingState());
        } else if (cmd instanceof RecvImIcbm) {
            RecvImIcbm icbm = (RecvImIcbm) cmd;
            FullUserInfo info = icbm.getSenderInfo();
            InstantMessage message = icbm.getMessage();
            getEventListener().receivedIM(info, message);
        } else if (cmd instanceof WarningNotification) {
            WarningNotification wn = (WarningNotification) cmd;
            getEventListener().receivedWarning(wn);
        } else if (cmd instanceof BuddyStatusCmd) {
            BuddyStatusCmd bsc = (BuddyStatusCmd) cmd;
            FullUserInfo info = bsc.getUserInfo();
            getEventListener().receivedBuddyStatus(info);
        } else if (cmd instanceof BuddyOfflineCmd) {
            BuddyOfflineCmd boc = (BuddyOfflineCmd) cmd;
            getEventListener().receivedBuddyOffline(boc.getScreenname());

        } else if (cmd instanceof RateChange) {
            RateChange rc = (RateChange) cmd;
            debug("rate change: current avg is " + rc.getRateInfo().getCurrentAvg());
        } else if (cmd instanceof ServerVersionsCmd) {
            // ServerVersionsCmd svc = (ServerVersionsCmd) cmd;
            // List<SnacFamilyInfo> familyInfos = svc.getSnacFamilyInfos();
            debug("IGNORING ServerVersionsCmd");
        }

    }

    @Override
    public void handleResponse(SnacResponseEvent e) {
        super.handleResponse(e);
        
        SnacCommand cmd = e.getSnacCommand();
        debug("SNAC Response Command: %s", cmd);

        if (cmd instanceof RateInfoCmd) {
            RateInfoCmd ric = (RateInfoCmd) cmd;
            List<RateClassInfo> rateClasses = ric.getRateClassInfos();
            int[] classes = new int[rateClasses.size()];
            for (int i = 0; i < rateClasses.size(); i++) {
                RateClassInfo rateClass = rateClasses.get(i);
                classes[i] = rateClass.getRateClass();
                debug("- " + rateClass + ": " + rateClass.getCommands());
            }
            request(new RateAck(classes));
        } else if (cmd instanceof LocRightsCmd) {
            CertificateInfo certInfo = null;
            Certificate cert = null;
            if (cert != null) {
                try {
                    byte[] encoded = cert.getEncoded();
                    certInfo = new CertificateInfo(ByteBlock.wrap(encoded));
                } catch (CertificateEncodingException e1) {
                    e1.printStackTrace();
                }
            }
            request(new SetInfoCmd(new InfoData("yo", null, CAPABILITIES, certInfo)));
            request(new SetEncryptionInfoCmd(Arrays.asList(new ExtraInfoBlock(
                ExtraInfoBlock.TYPE_CERTINFO_HASHA, new ExtraInfoData(ExtraInfoData.FLAG_HASH_PRESENT,
                    CertificateInfo.HASHA_DEFAULT)), new ExtraInfoBlock(ExtraInfoBlock.TYPE_CERTINFO_HASHB,
                new ExtraInfoData(ExtraInfoData.FLAG_HASH_PRESENT, CertificateInfo.HASHB_DEFAULT)))));
            request(new MyInfoRequest());
        } else if (cmd instanceof ParamInfoCmd) {
            ParamInfoCmd pic = (ParamInfoCmd) cmd;
            ParamInfo info = pic.getParamInfo();
            request(new SetParamInfoCmd(new ParamInfo(0,
                info.getFlags() | ParamInfo.FLAG_TYPING_NOTIFICATION, 8000, info.getMaxSenderWarning(), info
                    .getMaxReceiverWarning(), 0)));
        } else if (cmd instanceof YourInfoCmd) {
            YourInfoCmd yic = (YourInfoCmd) cmd;
            FullUserInfo info = yic.getUserInfo();
            getEventListener().receivedYourUserInfo(info);
        } else if (cmd instanceof UserInfoCmd) {
            UserInfoCmd uic = (UserInfoCmd) cmd;
            getEventListener().receivedUserInfo(uic.getUserInfo(), uic.getInfoData());
            // CertificateInfo certInfo =
            // uic.getInfoData().getCertificateInfo();
            // storeCert(sn, certInfo);
        } else if (cmd instanceof ServiceRedirect) {
            ServiceRedirect sr = (ServiceRedirect) cmd;
            info("IGNORING ServiceRedirect to " + sr.getRedirectHost() + " for 0x"
                + Integer.toHexString(sr.getSnacFamily()));
        } else if (cmd instanceof SsiDataCmd) {
            SsiDataCmd sdc = (SsiDataCmd) cmd;
            List<SsiItem> items = sdc.getItems();
            List<SsiItemObj> objs = new ArrayList<SsiItemObj>(items.size());
            debug("SSI items: " + items.size());
            for (SsiItem item : items) {
                SsiItemObj obj = itemFactory.getItemObj(item);
                objs.add(obj);
                debug("SSI: " + (obj == null ? (Object) item : (Object) obj));
            }
            getEventListener().receivedSSI(objs);
            if (items.size() == 0 || sdc.getLastModDate() != 0) {
                debug("SSI completed");
                request(new ActivateSsiCmd());
                clientReady();
            }
        }
    }

    protected void clientReady() {
        if (!mSentClientReady) {
            request(new ClientReadyCmd(mSnacFamilyInfo));
            mSentClientReady = true;
        }
    }

    private static final List<CapabilityBlock> CAPABILITIES =
        Arrays.asList(CapabilityBlock.BLOCK_CHAT, CapabilityBlock.BLOCK_DIRECTIM, CapabilityBlock.BLOCK_ICON,
            CapabilityBlock.BLOCK_SHORTCAPS);

    protected void setSnacFamilies(int[] families) {
        mSnacFamilies = families.clone();
        Arrays.sort(mSnacFamilies);
    }

    private SsiItemObjectFactory itemFactory = new DefaultSsiItemObjFactory();
    private ByteBlock mCookie;
    private boolean mSentClientReady = false;
    private Collection<SnacFamilyInfo> mSnacFamilyInfo;
    protected int[] mSnacFamilies = null;

    @Override
    int[] getSnacFamilies() {
        return mSnacFamilies;
    }
}
