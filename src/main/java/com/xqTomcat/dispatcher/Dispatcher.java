package com.xqTomcat.dispatcher;

import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;
import com.xqTomcat.utils.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaqi
 * @date 2019/3/20
 * process the http request and dispatch the request to servlet
 * 解析请求，查询handlerMapping找到对应的servlet并转发
 */
public class Dispatcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * url对应的servlet
     * url ----> servlet
     */
    private final Map<String,Class<?>> servletMapping = new HashMap<>();

    /**
     * all exists servlet instance
     * 所有已经实例化过的servlet
     */
    private final Map<String,Object> allAlreadyExistsServlet = new ConcurrentHashMap<>();

    public Map<String, Class<?>> getServletMapping() {
        return servletMapping;
    }

    public Map<String,Object> getAllAlreadyExistsServlet(){
        return allAlreadyExistsServlet;
    }

    /**
     * parse the request to set the headers，and dispatch the instance of request and response to the servlet
     * 解析请求设置对应的响应头部行，并转发http请求和响应对象给对应的servlet
     * @param socket client 客户端对象的socket对象
     * @throws IOException
     */
    public void dispatch(Socket socket) throws IOException {
        HttpRequest request = new HttpRequest(socket.getInputStream());
        HttpResponse response = new HttpResponse(socket.getOutputStream());
        logger.info("create request instance:"+request);
        logger.info("create response instance:"+response);
        response.setContentType("text/html;charset=UTF-8");
        // retrieve the request url
        // search for the servlet
        // invoke the service method of the servlet
        response.getOutputStream().write("你好啊！夏齐".getBytes("UTF-8"));
        response.send();
        // close the socket
        socket.close();
        logger.info("finishing request process.");
    }

}
