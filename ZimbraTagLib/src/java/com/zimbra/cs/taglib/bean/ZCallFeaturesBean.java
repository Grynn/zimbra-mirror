package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.cs.zclient.ZCallFeature;
import com.zimbra.cs.zclient.ZCallFeatures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZCallFeaturesBean {

    private ZCallFeatures mFeatures;
    private boolean mModify;

    public ZCallFeaturesBean(ZCallFeatures features, boolean modify) {
        mFeatures = features;
        mModify = modify;
    }

    public ZCallFeatures getCallFeatures() {
        return mFeatures;
    }

    public ZEmailNotificationBean getEmailNotification() throws ServiceException {
        return new ZEmailNotificationBean(getFeature(VoiceConstants.A_vmPrefEmailNotifAddress, true));
    }

    public ZCallForwardingBean getCallForwardingAll() throws ServiceException {
        return new ZCallForwardingBean(getFeature(VoiceConstants.E_CALL_FORWARD, false));
    }

    public boolean isEmpty() { return mFeatures.isEmpty(); }

    public List<ZCallFeatureBean> getFeatureList() {
        Collection<ZCallFeature> collection = mFeatures.getFeatureList();
        ArrayList<ZCallFeatureBean> result = new ArrayList<ZCallFeatureBean>(collection.size());
        for (ZCallFeature feature : collection) {
            result.add(new ZCallFeatureBean(feature));
        }
        return result;
    }

    public ZCallFeatureBean findCallFeature(String name) throws ServiceException {
        return new ZCallFeatureBean(mFeatures.findCallFeature(name));
    }

    public void removeCallFeature(String name) throws ServiceException {
        mFeatures.removeCallFeature(name);
    }
    
    private ZCallFeature getFeature(String name, boolean isVoiceMailPref) throws ServiceException {
        if (mModify) {
            ZCallFeature result = mFeatures.findCallFeature(name);
            if (result == null) {
                result = mFeatures.addCallFeature(name, isVoiceMailPref);
            }
            return result;
        } else {
            return mFeatures.getCallFeature(name); 
        }
    }
}
