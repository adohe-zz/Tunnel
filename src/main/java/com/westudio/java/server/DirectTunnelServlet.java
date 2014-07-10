package com.westudio.java.server;

import com.westudio.java.util.Numbers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DirectTunnelServlet extends TunnelServlet {

    private static final int DEFAULT_SEGMENT_SIZE = 32758;
    private static final byte[] EMPTY_CHUNK = {0, 0};

    private static void closeSocket(final Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {/**/}
    }

    private static void onRecvRemote(InputStream is, OutputStream os) throws IOException {
        byte[] buffer  = new byte[DEFAULT_SEGMENT_SIZE + 2];
        long lastSent = 0;
        while (true) {
            long now = System.currentTimeMillis();
            int len = 0;
            try {
                len = is.read(buffer, 2, DEFAULT_SEGMENT_SIZE);
            } catch (IOException e) {/**/}
            if (len == 0) {
                if (now > lastSent + 15000) {
                    os.write(EMPTY_CHUNK);
                    os.flush();
                    lastSent = now;
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                }
                continue;
            }

            if (len < 0) {
                break;
            }

            os.write(buffer, 0, len + 2);
            os.flush();
            lastSent = now;
        }
    }

    private static void onRecvClient(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[DEFAULT_SEGMENT_SIZE];
        int b0;
        int b1;

        while ((b0 = is.read()) > 0 && (b1 = is.read()) > 0) {
            int len = (b0 << 8) + b1;
            if (len == 0) {
                // Heartbeat
                continue;
            }

            if (len > DEFAULT_SEGMENT_SIZE) {
                break;
            }

            int off = 0;
            while (len > 0) {
                int bytesRead = is.read(buffer, off, len);
                if (bytesRead < 0) {
                    break;
                }
                off += bytesRead;
                len -= bytesRead;
            }
            os.write(buffer, 0, off);
        }
    }

    @Override
    protected void doTunnel(HttpServletRequest req, final HttpServletResponse res) {
        String destination = res.getHeader("Destination");
        int c = 0;

        if (destination == null || (c = destination.indexOf(":")) < 0) {
            try {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {/**/}
            return;
        }

        int port = Numbers.parseInt(destination.substring(c + 1));
        String host = destination.substring(0, c);
        Socket socket = null;

        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(DEFAULT_TIMEOUT);
        } catch (IOException e) {
            try {
                res.sendError(HttpServletResponse.SC_BAD_GATEWAY);
            } catch (IOException e1) {
                return;
            }
        }

        final Socket socket_ = socket;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    onRecvRemote(socket_.getInputStream(), res.getOutputStream());
                } catch (IOException e) {/**/}
                closeSocket(socket_);
            }
        });

        InputStream in = null;
        try {
            in = req.getInputStream();
            ins.add(in);
            onRecvClient(in, socket.getOutputStream());
        } catch (IOException e) {
        } finally {
            if (in != null) {
                ins.remove(in);
            }
        }

        // Evade resource leak warning
        closeSocket(socket);
    }

}
