package server.bios.asbserver.server._interface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by BIOS on 10/2/2016.
 */

public interface _Socket {
    boolean isConnected();
    ByteBuffer getData() throws IOException;
    void sendData(ByteBuffer byteBuffer) throws IOException;
    void close() throws IOException;

}
