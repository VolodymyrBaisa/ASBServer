package server.bios.asbserver.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by BIOS on 9/10/2016.
 */
public final class CharsetUtils {
    public volatile static CharsetUtils charsetUtils;

    private CharsetUtils() {
    }

    public static CharsetUtils getInstance(){
        if(charsetUtils == null){
            synchronized (CharsetUtils.class) {
                charsetUtils = new CharsetUtils();
                return charsetUtils;
            }
        }
        return charsetUtils;
    }

    public String charsetDecoder(ByteBuffer buffer, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        CharBuffer charBuffer = charset.decode(buffer);
        return charBuffer.toString();
    }

    public ByteBuffer charsetEncoder(String str, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return charset.encode(str);
    }
}
