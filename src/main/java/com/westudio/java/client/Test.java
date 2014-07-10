package com.westudio.java.client;

import java.net.MalformedURLException;
import java.net.URL;

public class Test {

    public static void main(String[] args) {
        try {
            URL url = new URL("https://localhost/direct-tunnel/");
            System.out.println(url.getPort() + url.getDefaultPort());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
