package com.westudio.java.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class TunnelServlet extends HttpServlet {

    private String auth;

    protected abstract void doTunnel(HttpServletRequest req, HttpServletResponse res);

    @Override
    public void init() throws ServletException {
        auth = getInitParameter("auth");
        if (auth != null) {

        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authorization = req.getHeader("Authorization");

        if (auth == null || (authorization != null)) {
            doTunnel(req, resp);
        }  else {

        }
    }
}
