package com.xqTomcat.utils;

import com.xqTomcat.http.HttpMethod;
import com.xqTomcat.http.HttpRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            try {

                // read the request
                byte[] bytes = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(bytes);
                String httpRequestDatagram = new String(bytes,0,bytes.length);
                logger.info("httpRequestDatagram:\r\n"+httpRequestDatagram);

                // parse the request
                String[] strings = httpRequestDatagram.split("\r\n");

                // parse request line
                String requestLine  = strings[0];
                logger.info("requestLine:"+requestLine);
                String[] requestLineInfo = requestLine.split(" ");
                if ("GET".equalsIgnoreCase(requestLineInfo[0])){
                    entity.setHttpMethod(HttpMethod.GET);
                } else {
                    entity.setHttpMethod(HttpMethod.POST);
                }
                entity.setRequestURL(requestLineInfo[1]);

                //parse the http request headers and the parameter
                Map<String, String> requestHeaders = entity.getRequestHeaders();
                Map<String, String> requestEntity = entity.getRequestEntity();
                for (int i = 1;i<strings.length;i++){
                    if (!" ".equals(strings[i])){
                        // headers
                        String[] headers = strings[i].split(":");
                        requestHeaders.put(headers[0],headers[1].trim());
                    } else {
                        // request body
                        String[] parameterPairs = strings[i].split("&");
                        for (String str : parameterPairs){
                            String[] parameter = str.split("=");
                            requestEntity.put(parameter[0],parameter[1]);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("parse the http request fail!",e);
            }
        }
        return entity;
    }
}
