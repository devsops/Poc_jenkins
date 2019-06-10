package com.bosch.pai.ipswrapper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bosch.pai.IeroIPSPlatformListener;
import com.bosch.pai.IeroIPSPlatform;
import com.bosch.pai.detection.Util;

/**
 * The type Ips platform factory.
 */
public final class IPSPlatformFactory {

    private static final String TAG = IPSPlatformFactory.class.getName();

    /**
     * The enum Platform type.
     */
    public enum PlatformType {
        /**
         * Iero ips platform platform type.
         */
        IERO_IPS_PLATFORM
    }

    private static IeroIPSPlatform ieroIPSPlatform;

    /**
     * Gets instance.
     *
     * @param context                 the context
     * @param platformType            the platform type
     * @param ieroIPSPlatformListener the ieroIPSPlatformListener
     * @return the instance
     */
    public static synchronized IeroIPSPlatform getInstance(Context context, PlatformType platformType, IeroIPSPlatformListener ieroIPSPlatformListener) {
        if (context == null)
            throw new NullPointerException("Context can not be null");
        if (ieroIPSPlatformListener == null)
            throw new NullPointerException("Listener can not be null");
        if(platformType == null)
            throw new NullPointerException("PlatformType can not be null");
        switch (platformType) {
            case IERO_IPS_PLATFORM:
                getIEROIPSPlatformInstance(context, ieroIPSPlatformListener);
                break;
            default:
                Util.addLogs(Util.LOG_STATUS.ERROR, TAG, "Not a valid platform type", null);
        }
        return ieroIPSPlatform;
    }

    private static void getIEROIPSPlatformInstance(Context context, IeroIPSPlatformListener ieroIPSPlatformListener) {
        final IeroIPSPlatformImpl ieroIPSPlatformImpl;
        if (ieroIPSPlatform == null) {
            ieroIPSPlatformImpl = new IeroIPSPlatformImpl(context, ieroIPSPlatformListener);
            IPSPlatformFactory.ieroIPSPlatform = ieroIPSPlatformImpl;
        } else {
            ieroIPSPlatformImpl = (IeroIPSPlatformImpl) ieroIPSPlatform;
            ieroIPSPlatformImpl.setListener(ieroIPSPlatformListener);
            ieroIPSPlatformImpl.setContext(context);
        }
    }
}
