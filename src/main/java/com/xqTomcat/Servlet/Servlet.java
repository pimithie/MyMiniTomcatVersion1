package com.xqTomcat.Servlet;

import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;

/**
 * @author xiaqi
 * @date 2019/3/20
 */
public interface Servlet {

    public void init();

    public void service(HttpRequest request, HttpResponse response) throws Exception;

}
