package com.westudio.java.server;

import com.westudio.java.util.Numbers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.Socket;

public class DirectTunnelServlet extends TunnelServlet {

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
