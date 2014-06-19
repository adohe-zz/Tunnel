package com.westudio.java.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DirectTunnelServlet extends TunnelServlet {

    @Override
    protected void doTunnel(HttpServletRequest req, HttpServletResponse res) {
    }

}
