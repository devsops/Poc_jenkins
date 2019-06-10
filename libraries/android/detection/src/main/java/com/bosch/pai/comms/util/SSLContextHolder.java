package com.bosch.pai.comms.util;

import javax.net.ssl.SSLContext;

/**
 * Created by NewUser on 05/07/17.
 */

public class SSLContextHolder {
    private static final ThreadLocal<SSLContext>
            BEARING_USER_CONTEXT_THREAD_LOCAL = new ThreadLocal<SSLContext>();

    private SSLContextHolder(){

    }

    public static void set(SSLContext contextData)
    {
        SSLContextHolder.BEARING_USER_CONTEXT_THREAD_LOCAL.set(contextData);
    }

    public static SSLContext get()
    {
        return SSLContextHolder.BEARING_USER_CONTEXT_THREAD_LOCAL.get();
    }

    public static void clear()
    {
        SSLContextHolder.BEARING_USER_CONTEXT_THREAD_LOCAL.remove();
    }
}