package com.westudio.java.client;

import com.westudio.java.util.Executors;
import com.westudio.java.util.Factory;
import com.westudio.java.util.Numbers;
import org.apache.commons.codec.binary.Base64;

import javax.net.SocketFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalTunnelClient {

    private static final String URL_STRING = "http://localhost/direct-tunnel/";
    private static final String AUTH = "USERNAME:PASSWORD";

    private static String host;
    private static String path;
    private static int port;
    private static String destination;

    private static String NO_AUTH_HEADER =
            "POST %s HTTP/1.1\r\n" +
            "Connection: close\r\n" +
            "Transfer-Encoding: chunked\r\n" +
            "Host: %s:%s\r\n" +
            "Destination: %s\r\n" +
            "\r\n2\r\n\0\0\r\n";
    private static String AUTH_HEADER =
            "POST %s HTTP/1.1\r\n" +
            "Connection: close\r\n" +
            "Transfer-Encoding: chunked\r\n" +
            "Host: %s:%s\r\n" +
            "Destination: %s\r\n" +
            "Authorization: Basic %s\r\n" +
            "\r\n2\r\n\0\0\r\n";;

    private static AtomicBoolean running = new AtomicBoolean(true);

    private static SocketFactory sf;

    private static void connect(final Socket socket) {
        try (
          Socket socket_ = sf.createSocket(host, port);
          InputStream is = new BufferedInputStream(socket_.getInputStream());
          OutputStream os = socket_.getOutputStream();
        ) {
            String header = null;
            if (AUTH == null) {
                header = String.format(NO_AUTH_HEADER, path,
                        host, port, "");
            } else {
                header = String.format(path, host,
                        port, "", new String(Base64.encodeBase64(AUTH.getBytes())));
            }

            // write the header
            os.write(header.getBytes());
        } catch (IOException e) {

        }
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

        sf = Factory.getSocketFactory("HTTPS".equals(url.getProtocol()));

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
