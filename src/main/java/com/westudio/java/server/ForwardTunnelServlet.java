package com.westudio.java.server;

import com.westudio.java.util.Numbers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ForwardTunnelServlet extends TunnelServlet {

    @Override
    protected void doTunnel(HttpServletRequest req, HttpServletResponse res) {
        final int port = Numbers.parseInt(req.getHeader("Listen"));

        if (port < 0) {
            try {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {/**/}
            return;
        }
    }
}
