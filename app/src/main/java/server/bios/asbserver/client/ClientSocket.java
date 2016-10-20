package server.bios.asbserver.client;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import server.bios.asbserver.utils.CharsetUtils;

/**
 * Created by BIOS on 9/4/2016.
 */
public class ClientSocket {
    private static final String TAG = ClientSocket.class.getName();
    private static CharsetUtils charsetUtils = CharsetUtils.getInstance();
    private SocketChannel socketChannel;
    private static final int BUFFER_SIZE = 0x800;
    private ByteBuffer getBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private ByteBuffer sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private String host;
    private int port;

    public ClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        Socket socket = socketChannel.socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        socket.connect(inetSocketAddress, 1000);

        Log.d(TAG,"Ace Stream engine starting on port ".concat(String.valueOf(port)));
    }

    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    public void sendData(String msg) throws IOException {
        if (socketChannel == null) throw new NullPointerException("socketChannel == null");
        sendBuffer.put(msg.getBytes());
        sendBuffer.flip();
        socketChannel.write(sendBuffer);
        sendBuffer.clear();
    }

    public String getData() throws IOException {
        if (socketChannel == null) throw new NullPointerException("socketChannel == null");
        socketChannel.read(getBuffer);
        getBuffer.flip();
        String chars = charsetUtils.charsetDecoder(getBuffer, "UTF-8");
        getBuffer.clear();
        return chars;
    }

    public void close() throws IOException {
        if (socketChannel != null) {
            socketChannel.close();
        }
    }
}
