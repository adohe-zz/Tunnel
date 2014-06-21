package com.westudio.java.util;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Factory {

    private static SocketFactory plainFactory;
    private static SocketFactory secureFactory;

    static {
        plainFactory = SocketFactory.getDefault();
        try {
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(new KeyManager[0], new X509TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, null);
            secureFactory = ssl.getSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static SocketFactory getSocketFactory(final boolean secure) {
        return secure ? secureFactory : plainFactory;
    }
}
