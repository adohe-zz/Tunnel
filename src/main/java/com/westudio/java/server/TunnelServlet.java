package com.westudio.java.server;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public abstract class TunnelServlet extends HttpServlet {

    private String auth;

    protected ExecutorService executor;
    protected abstract void doTunnel(HttpServletRequest req, HttpServletResponse res);

    protected static final int DEFAULT_TIMEOUT = 1000;

    @Override
    public void init() throws ServletException {
        auth = getInitParameter("auth");
        if (auth != null) {
            auth = new String(Base64.encodeBase64(auth.getBytes()));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String authorization = req.getHeader("Authorization");

        if (auth == null || (authorization != null &&
            authorization.toUpperCase().startsWith("BASIC")) &&
            authorization.substring(6).equals(auth)) {
            doTunnel(req, res);
        }  else {
            res.setHeader("WWW-Authenticate",
                    "Basic realm=\"Pegasus Tunnel\"");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
