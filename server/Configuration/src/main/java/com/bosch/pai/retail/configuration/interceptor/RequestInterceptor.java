package com.bosch.pai.retail.configuration.interceptor;

import com.bosch.pai.retail.common.serviceutil.ContextHolder;
import com.bosch.pai.retail.common.serviceutil.RequestContext;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor implements HandlerInterceptor {
    private static final String USER_ID = "userid";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        final String userId = request.getHeader(USER_ID);
        if (null != userId && !userId.isEmpty()) {
            final RequestContext requestContext = new RequestContext();
            requestContext.setUserId(userId);
            ContextHolder.setContext(requestContext);
        }else {
            final RequestContext requestContext = new RequestContext();
            requestContext.setUserId("");
            ContextHolder.setContext(requestContext);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object
            handler, ModelAndView modelAndView) throws Exception {
        ContextHolder.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse
            response, Object handler, Exception ex) throws Exception {
    }
}
