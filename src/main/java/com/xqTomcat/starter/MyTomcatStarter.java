package com.xqTomcat.starter;

import com.xqTomcat.handler.StaticResourceHandler;
import com.xqTomcat.http.HttpRequestEntity;
import com.xqTomcat.utils.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xiaqi
 * @date 2019/3/17
 * tomcat starter.
 */
public class MyTomcatStarter {

    private final static Logger logger = LoggerFactory.getLogger(MyTomcatStarter.class);

    /**
     * start the tomcat server
     */
    public static void main(String[] args) throws IOException {
        // record the start timestamp
        long start = System.currentTimeMillis();
        // create the serversocket to listening the specific port
        ServerSocket serverSocket = new ServerSocket(8080);
        logger.info("my tomcat started,listening "+8080+"port");
        while (true){
            // start listening
            Socket socket = serverSocket.accept();
            logger.info("receive http request,create the socket:"+socket);
            // parse the http request
            HttpRequestEntity httpRequestEntity = HttpRequestParser.parse(socket);
            logger.info("finish parse the http request,the parsing result is "+httpRequestEntity);
            // 此处默认全是静态资源
            StaticResourceHandler.handle(httpRequestEntity,socket);
        }
    }

}
