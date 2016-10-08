package server.bios.asbserver.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BIOS on 9/11/2016.
 */
public class Algorithm {
    private volatile static Algorithm algorithm;

    private Algorithm() {
    }

    public static Algorithm getInstance() {
        if (algorithm == null) {
            synchronized (Algorithm.class) {
                algorithm = new Algorithm();
                return algorithm;
            }
        }
        return algorithm;
    }

    public String SHA1(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");

        byte[] result = mDigest.digest(input.getBytes("UTF-8"));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
