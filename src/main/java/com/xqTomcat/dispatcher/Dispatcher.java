package com.xqTomcat.dispatcher;

import com.xqTomcat.Servlet.Servlet;
import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaqi
 * process the http request and dispatch the request to servlet
 * 处理请求，查询handlerMapping找到对应的servlet并转发
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
     * @throws Exception
     */
    public void dispatch(Socket socket) throws Exception {
        HttpRequest request = new HttpRequest(socket.getInputStream());
        HttpResponse response = new HttpResponse(socket.getOutputStream());
        logger.info("create request instance:"+request);
        logger.info("create response instance:"+response);
        // retrieve the request url 获取http请求的url
        String requestURL = request.getRequestURL();
        if (null == requestURL){
            return;
        }
        String path = null;
        //    /test/login
        int firstSlashIndex = requestURL.indexOf("/");
        if (-1 != firstSlashIndex){
            int secondSlashIndex = requestURL.indexOf("/", firstSlashIndex + 1);
            path = requestURL.substring(secondSlashIndex);
        }
        // search for the servlet 搜寻对应路径的servlet
        Servlet servlet = (Servlet) allAlreadyExistsServlet.get(path);
        if (null == servlet){
            Class<?> clazz = servletMapping.get(path);
            logger.info("create the servlet instance:"+clazz.getName());
            servlet = (Servlet) clazz.newInstance();
            servlet.init();
            logger.info("new servlet instance of "+clazz.getName()+" is put into the allAlreadyExistsServlet.");
            allAlreadyExistsServlet.put(path,servlet);
        }
        // invoke the service method of the servlet 调用servlet的service方法
        servlet.service(request,response);
        response.send();
        // close the socket 关闭socket连接
        socket.close();
        logger.info("finishing request process.");
    }

}
