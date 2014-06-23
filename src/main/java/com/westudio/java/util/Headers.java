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

            int index = sb.indexOf(":");
            if (index < 0) {
                // HTTP/1.1 200 OK
                headers.put("", sb.toString());
            } else {
                headers.put(sb.toString().substring(0, index), sb.toString().substring(index + 2));
            }

            sb.setLength(0);
        }
    }
}
