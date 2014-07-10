package com.westudio.java.client;

import com.westudio.java.util.*;
import org.apache.commons.codec.binary.Base64;

import javax.net.SocketFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocalTunnelClient {

    private static final int SEGMENT_SIZE = 32758;
    private static final String URL_STRING = "http://localhost/direct-tunnel/";
    private static final String AUTH = "USERNAME:PASSWORD";
    private static final String HEX_DIGITS = "0123456789ABCDEF";
    private static final byte[] CHUNK_END = "0\r\n\r\n".getBytes();
    private static final byte[] EMPTY_CHUNK = {'2', '\r', '\n', 0, 0, '\r', '\n'};

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
            "\r\n2\r\n\0\0\r\n";

    private static AtomicBoolean running = new AtomicBoolean(true);

    private static SocketFactory sf;

    private static void onRecvLocal(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[SEGMENT_SIZE + 10];
        buffer[4] = '\r';
        buffer[5] = '\n';

        long lastSent = System.currentTimeMillis();
        while (true) {
            long now = System.currentTimeMillis();
            int len = 0;

            try {
                len = is.read(buffer, 8, SEGMENT_SIZE);
            } catch (IOException e) {/**/}
            if (len == 0) {
                if (now > lastSent + 15000) {
                    lastSent = now;
                    os.write(EMPTY_CHUNK);
                }
                continue;
            }

            if (len < 0) {
                break;
            }

            int chunkSize = len + 2;
            buffer[0] = (byte)HEX_DIGITS.charAt(chunkSize >> 12);
            buffer[1] = (byte)HEX_DIGITS.charAt((chunkSize >> 8) & 0xF);
            buffer[2] = (byte)HEX_DIGITS.charAt((chunkSize >> 4) & 0xF);
            buffer[3] = (byte)HEX_DIGITS.charAt(chunkSize & 0xF);
            buffer[6] = (byte)(len >> 8);
            buffer[7] = (byte)(len & 0xFF);
            buffer[len + 8] = '\r';
            buffer[len + 9] = '\n';
            os.write(buffer, 0 , len + 10);
            lastSent = now;
        }

        os.write(CHUNK_END);
    }

    private static void connect(final Socket socket) {
        try (
          // Connect the Tunnel Server Servlet
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
            HashMap<String, String> headers = Headers.generateHeaders(is);
            Headers.checkHeaders(headers);

            Executors.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        onRecvLocal(socket.getInputStream(), os);
                    } catch (IOException e) {/**/}
                }
            });

            if ("chunked".equals(headers.get("Transfer-Encoding"))) {
                onRecvRemoteChunked(is, os);
            } else {
                onRecvRemote(is, os);
            }
        } catch (IOException e) {/**/}
    }

    private static void onRecvRemote(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[SEGMENT_SIZE];
        int b0;
        int b1;


        while ((b0 = is.read()) >= 0 && (b1 = is.read()) >= 0) {
            int len = b0 << 8 + b1;

            if (len == 0) {
                // Heart beat
                continue;
            }

            if (len > SEGMENT_SIZE) {
                break;
            }

            int offset = 0;
            while (len > 0) {
                int bytesRead = is.read(buffer, offset, len);

                if (bytesRead < 0) {
                    break;
                }

                offset += bytesRead;
                len -= bytesRead;
            }

            os.write(buffer, 0, offset);
        }
    }

    private static void onRecvRemoteChunked(InputStream is, OutputStream os) {
        byte[] buffer = new byte[SEGMENT_SIZE];
        int len = 0;


    }

    public static void main(String[] args) {

        if (Conf.handleShutdown(LocalTunnelClient.class, args, running)) {
            return;
        }

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
        port = url.getPort() < 0 ? url.getDefaultPort() : url.getPort(); //FIXME?
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
