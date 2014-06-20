package com.westudio.java.client;

import java.net.MalformedURLException;
import java.net.URL;

public class LocalTunnelClient {

    private static final String URL_STRING = "http://localhost/direct-tunnel/";
    private static final String AUTH = "USERNAME:PASSWORD";

    private static String host;
    private static String path;
    private static int port;

    public static void main(String[] args) {

        if (args == null || args.length < 3) {
            return;
        }

        URL url;
        try {
            url = new URL(URL_STRING);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        host = url.getHost();
        port = url.getPort() < 0 ? url.getDefaultPort() : url.getPort();
        path = url.getPath();

        while (true) {

        }
    }
}
