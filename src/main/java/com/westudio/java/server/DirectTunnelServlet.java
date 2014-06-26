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

    private void onRecvRemote(InputStream is, OutputStream os) throws IOException {
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

    @Override
    protected void doTunnel(HttpServletRequest req, HttpServletResponse res) {
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
        final Socket socket;

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

        executor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

}
