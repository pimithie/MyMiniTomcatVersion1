package com.xqTomcat.handler;

import com.xqTomcat.http.HttpRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * @author xiaqi
 * @date 2019/3/17
 * static resource handler
 */
public class StaticResourceHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceHandler.class);

    private static final String WEB_ROOT = "C:/test";

    public static void handle (HttpRequestEntity httpRequestEntity,Socket socket) throws IOException {
        // resource path
        String resourcePath = WEB_ROOT+httpRequestEntity.getRequestURL();
        // create the http response
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append("HTTP/1.1 200 ok\r\n");
        stringBuilder.append("Content-Type: text/html;charset=UTF-8\r\n");
        stringBuilder.append("\r\n");
        //output the http response line
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(stringBuilder.toString());
        bw.flush();
        // read the resource stream
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(resourcePath));
        // output the resource
        BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());

        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1){
            outputStream.write(bytes,0 ,len);
            outputStream.flush();
        }
        outputStream.close();
        socket.shutdownOutput();
        socket.close();
    }


}
