package com.xqTomcat.Servlet;

import com.xqTomcat.annotation.MyServlet;
import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;

import java.io.UnsupportedEncodingException;

/**
 * @author xiaqi
 * @date 2019/3/20
 * my test servlet
 * 测试servlet
 */
@MyServlet(value = "/test",load_on_startup = true)
public class MyTestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.getOutputStream().write("你好！夏齐".getBytes("UTF-8"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        doGet(request,response);
    }
}
