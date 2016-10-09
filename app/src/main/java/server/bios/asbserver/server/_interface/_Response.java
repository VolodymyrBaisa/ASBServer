package server.bios.asbserver.server._interface;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by BIOS on 10/9/2016.
 */

public interface _Response {
    StringBuilder getHeaderResponce();
    InputStream byteStream();
    String getString() throws IOException;
    void close();
}
