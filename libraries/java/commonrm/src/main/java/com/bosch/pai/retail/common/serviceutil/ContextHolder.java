package com.bosch.pai.retail.common.serviceutil;

public class ContextHolder {
    private static final ThreadLocal<RequestContext>
            REQUEST_CONTEXT_THREAD_LOCAL = new ThreadLocal<RequestContext>();

    private ContextHolder(){

    }

    public static void setContext(RequestContext contextData)
    {
        ContextHolder.REQUEST_CONTEXT_THREAD_LOCAL.set(contextData);
    }

    public static RequestContext getContext()
    {
        return ContextHolder.REQUEST_CONTEXT_THREAD_LOCAL.get();
    }

    public static void clear()
    {
        ContextHolder.REQUEST_CONTEXT_THREAD_LOCAL.remove();
    }
}
