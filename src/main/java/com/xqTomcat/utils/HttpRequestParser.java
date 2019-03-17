package com.xqTomcat.utils;

import com.xqTomcat.http.HttpMethod;
import com.xqTomcat.http.HttpRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaqi
 * @date 2019/3/17
 * http request parsing tool
 */
public class HttpRequestParser {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    /**
     * can't create the instance of this class
     */
    private HttpRequestParser(){}

    /**
     * parst the http request
     * @param socket current socket
     * @return the instance of HttpRequestEntity
     */
    public static HttpRequestEntity parse(Socket socket){
        /*
            GET /xxx/xxx HTTP/1.1
            xxx:xxx
            xxx:xxx

            xxx=xxx&yyy=yyy
         */

        // the result returned
        HttpRequestEntity entity = null;
        // retrieve the inputstream of current socket
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            logger.error("parse the http request fail!",e);
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (inputStream != null){
            entity = new HttpRequestEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                // read the request line
                String requestLine = br.readLine();
                logger.info("request line is "+requestLine);
                // split the request line
                String[] strs = requestLine.split(" ");
                HttpMethod httpMethod = null;
                if ("get".equalsIgnoreCase(strs[0])){
                    httpMethod = HttpMethod.GET;
                } else {
                    // otherwise,post method
                    httpMethod = HttpMethod.POST;
                }
                entity.setHttpMethod(httpMethod);
                entity.setRequestURL(strs[1]);
                String line = null;

                // read the headers
                Map<String, String> requestHeaders = entity.getRequestHeaders();
                while (!"".equals(line = br.readLine())){
                    logger.info("request headers is "+line);
                    String[] headers = line.split(":");
                    requestHeaders.put(headers[0],headers[1]);
                }

                // read the request parameter
                Map<String, String> requestEntity = entity.getRequestEntity();
                while ((line = br.readLine()) != null){
                    logger.info("request parameters are "+line);
                    // name=zhangsan&age=20
                    String[] requestParams = line.split("&");
                    for (String param : requestParams){
                        String[] parameters = param.split("=");
                        requestEntity.put(parameters[0],parameters[1]);
                    }
                }
            } catch (IOException e) {
                logger.error("parse the http request fail!",e);
            }
        }
        return entity;
    }
}
