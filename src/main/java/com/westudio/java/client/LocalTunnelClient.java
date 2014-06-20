package com.westudio.java.client;

import com.westudio.java.util.Executors;
import com.westudio.java.util.Numbers;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalTunnelClient {

    private static final String URL_STRING = "http://localhost/direct-tunnel/";
    private static final String AUTH = "USERNAME:PASSWORD";

    private static String host;
    private static String path;
    private static int port;

    private static AtomicBoolean running = new AtomicBoolean(true);

    private static void connect(Socket socket) {

    }

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
            try (ServerSocket server = new ServerSocket(Numbers.parseInt(args[0]))) {
                server.setSoTimeout(16);
                while (running.get()) {
                    Socket socket = null;
                    try {
                        socket = server.accept();
                    } catch (SocketTimeoutException e) {/**/}
                    if (socket == null) {
                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException e) {/**/}
                        continue;
                    }
                    socket.setSoTimeout(16);
                    final Socket socket_ = socket;
                    Executors.execute(new Runnable() {
                        @Override
                        public void run() {
                            connect(socket_);
                        }
                    });
                }
            } catch (IOException e) {

            }
        }
    }
}
