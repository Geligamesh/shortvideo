package com.gxb;

import com.gxb.utils.JsonResult;
import com.gxb.utils.JsonUtils;
import com.gxb.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;
    public static final String USER_REDIS_SESSION = "user-redis-session";

    /**
     * 拦截请求。在controller调用之前
     * @param request
     * @param response
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");

        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
            if(StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
                System.out.println("请登录");
                returnErrorResponse(response, new JsonResult().errorTokenMsg("请登录..."));
                return false;
            }else {
                if(!uniqueToken.equals(userToken)) {
                    System.out.println("账号被挤出...");
                    returnErrorResponse(response, new JsonResult().errorTokenMsg("账号被挤出..."));
                    return false;
                }
            }
        }else {
            System.out.println("请登录");
            returnErrorResponse(response, new JsonResult().errorTokenMsg("请登录..."));
            return false;
        }
        /**
         * 返回false：请求被拦截，返回
         *  请求ok，可以继续执行，放行
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, JsonResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

            /**
             * 请求controller之后，渲染视图之前
             * @param httpServletRequest
             * @param httpServletResponse
             * @param o
             * @param modelAndView
             * @throws Exception
             */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求controller之后，视图渲染之后
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
