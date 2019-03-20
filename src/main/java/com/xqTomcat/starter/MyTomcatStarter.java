package com.xqTomcat.starter;

import com.xqTomcat.Servlet.Servlet;
import com.xqTomcat.annotation.MyServlet;
import com.xqTomcat.dispatcher.Dispatcher;
import com.xqTomcat.http.HttpRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author xiaqi
 * @date 2019/3/17
 * tomcat starter.
 */
public class MyTomcatStarter {

    private final static Logger logger = LoggerFactory.getLogger(MyTomcatStarter.class);

    private static final Dispatcher DISPATCHER = new Dispatcher();

    private static String basePackage = "com.xqTomcat";

    /**
     * start the tomcat server
     */
    public static void main(String[] args) throws Exception {

        // initialize servlet mapping
        // 初始化servlet mapping
        initServletMapping();

        // instantiation all servlet whose @MyServlet Annotation attribute load-on-startup is true
        // 实例化所有在容器启动的加载的servlet
        instantiationEarlyServlet();

//        // record the start timestamp
//        long start = System.currentTimeMillis();
//        // create the serversocket to listening the specific port
//        ServerSocket serverSocket = new ServerSocket(8080);
//        logger.info("my tomcat started,listening "+8080+" port");
//        while (true){
//            // start listening
//            Socket socket = serverSocket.accept();
//            logger.info("receive http request,create the socket:"+socket);
//            DISPATCHER.dispatch(socket);
//        }
    }

    private static void instantiationEarlyServlet() throws Exception {
        // traverse the servlet mapping to search for all early servlet
        // 寻找需要提前实例化的servlet
        for (Map.Entry<String,Class<?>> entry : DISPATCHER.getServletMapping().entrySet()){
            Class<?> clazz = entry.getValue();
            MyServlet annotation = clazz.getAnnotation(MyServlet.class);
            if (annotation.load_on_startup()){
                Servlet instance = (Servlet) clazz.newInstance();
                // invoke servlet init method 调用servlet的init方法
                instance.init();
                logger.info("instantiation early servlet:"+clazz.getName());
                DISPATCHER.getAllAlreadyExistsServlet().put(entry.getKey(),instance);
            }
        }
    }

    /**
     * Scanning component,and initialize the ServletMapping
     * 扫描servlet组件，并初始化ServletMapping
     */
    private static void initServletMapping() throws Exception {
        logger.info("starting scanning package.");
        scanningPackage(basePackage);
    }

    private static void scanningPackage(String basePackage) throws Exception{
        // replace all "." with "/"  将所有的"."替换为"/"
        String basePackageStr = basePackage.replaceAll("\\.","/");

        // retrieve the root directory's url 获取根目录的url
        URL rootDirectoryUrl = MyTomcatStarter.class.getClassLoader().getResource(basePackageStr);
        String rootDirectoryStr = rootDirectoryUrl.getFile();

        File rootDirectory = new File(rootDirectoryStr);
        // retrieve all directory and file of rootDirectory
        // 获取根目录下所有的目录和文件
        String[] fileStrs = rootDirectory.list();
        logger.info("root directory has "+ Arrays.toString(fileStrs));
        for (String fileStr : fileStrs){
            File file = new File(rootDirectoryStr+"/"+fileStr);
            if (file.isDirectory()){
                scanningPackage(basePackage+"."+fileStr);
            } else {
                // com.xqTomcat.annotation.MyServlet.class
                String className = basePackage+"."+fileStr;
                logger.info("scanned component:"+className);
                // com.xqTomcat.annotation.MyServlet
                className = className.replace(".class","");
                // put into the servlet mapping 放入servlet mapping的map中
                Class<?> clazz = Class.forName(className);
                // exist @MyServlet annotation? 判断是否存在@MyServlet注解
                if (clazz.isAnnotationPresent(MyServlet.class)){
                    MyServlet annotation = clazz.getAnnotation(MyServlet.class);
                    String path = annotation.value();
                    DISPATCHER.getServletMapping().put(path,clazz);
                }
            }
        }
    }

}
