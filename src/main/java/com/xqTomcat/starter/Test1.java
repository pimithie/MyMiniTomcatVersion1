package com.xqTomcat.starter;

import java.util.Arrays;

/**
 * @author xiaqi
 * @date 2019/3/18
 */
public class Test1 {

    public static void main(String[] args) {
        String s = "GET /XX/YY HTTP/1.1\r\nContext-Type: text/html\r\n\r\nxxx";
        System.out.println(Arrays.toString(s.split("\r\n")));
    }
}
