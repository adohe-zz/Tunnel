package com.westudio.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Headers {

    public static HashMap<String, String> generateHeaders(InputStream is) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        while (true) {
            int b = is.read();

            if (b < 0) {
                throw new IOException("socket connection lost");
            }

            if (b == '\r') {
                continue;
            }

            if (b != '\n') {
                sb.append((char)b);
                continue;
            }

            if (sb.length() == 0) {
                return headers;
            }

            String str = sb.toString();
            int index = str.indexOf(": ");
            if (index < 0) {
                // HTTP/1.1 200 OK
                headers.put("", str);
            } else {
                headers.put(str.substring(0, index), str.substring(index + 2));
            }

            sb.setLength(0);
        }
    }

    public static void checkHeaders(HashMap<String, String> headers) throws IOException {
        String statusLine = headers.get("");

        if (statusLine == null) {
            throw new IOException("response status error");
        }

        String[] ss = statusLine.split(" ", 3);
        if (ss.length != 3) {
            throw new IOException("response status error");
        }

        if (Numbers.parseInt(ss[1]) != 200) {
            throw new IOException("response status " + ss[2]);
        }
    }
}
