package com.cloud.personal.zuulserver.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        log.info(ctx.getRequest().getRequestURI() + " get request path info");
        String url = ctx.getRequest().getRequestURI().toLowerCase();
        // 这里判断url逻辑
        if (url.startsWith("/login")) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();

        // 通过其它业务生成token
        String access_token = "user_name_token";

        // 使用1 ：向request的header中增加参数access_token
        ctx.addZuulRequestHeader("access_token", access_token);

        // 使用2：向request的url里增加参数,示例增加 access_token
        Map<String, List<String>> requestQueryParams = ctx.getRequestQueryParams();
        if (requestQueryParams == null) {
            requestQueryParams = new HashMap<>();
        }

        requestQueryParams.put("access_token", Arrays.asList(access_token));

        ctx.setRequestQueryParams(requestQueryParams);

        //使用3 ：:判断是否登录,如果未登录直接返回404
        if (access_token == null || access_token.trim().length() == 0) {
            //过滤该请求，不对其进行路由(直接输入返回）
            ctx.setSendZuulResponse(false);
            //返回错误码
            ctx.setResponseStatusCode(401);
            // 返回错误内容
            ctx.setResponseBody("{\"result\":\"access_token is not correct!\"}");

            //让下一个Filter看到上一个Filter的状态用于过滤器间的协调
            ctx.set("my_filter_is_success", false);
            return null;
        }

        // 对该请求进行路由(默认就是true)
        //ctx.setSendZuulResponse(true);
        //ctx.setResponseStatusCode(200);
        //让下一个Filter看到上一个Filter的状态用于过滤器间的协调
        ctx.set("my_filter_is_success", false);

        return null; //直接返回null即可
    }
}
