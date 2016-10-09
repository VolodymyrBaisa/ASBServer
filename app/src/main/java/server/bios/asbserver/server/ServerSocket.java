package server.bios.asbserver.server;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import server.bios.asbserver.server._interface._Socket;

/**
 * Created by BIOS on 9/14/2016.
 */
public class ServerSocket {
    private static final String TAG = ServerSocket.class.getName();
    private static final int BUFFER_SIZE = 0x800;
    private ByteBuffer getBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private ServerSocketChannel serverSocketChannel;

    public ServerSocket(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        Log.i(TAG, "ASBServer starting on port ".concat(String.valueOf(port)));
    }

    public Socket accept() throws IOException {
        return new Socket();
    }

    public void close() throws IOException {
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
        }
    }

    private class Socket implements _Socket {
        private volatile SocketChannel socketChannel;

        public Socket() throws IOException {
            socketChannel = serverSocketChannel.accept();
        }

        public boolean isConnected() {
            return socketChannel.isConnected();
        }

        public ByteBuffer getData() throws IOException {
            if (socketChannel == null) throw new NullPointerException("socketChannel == null");
            socketChannel.read(getBuffer);
            getBuffer.flip();
            ByteBuffer buffer = getBuffer.duplicate();
            getBuffer.clear();
            return buffer;
        }

        public void sendData(ByteBuffer byteBuffer) throws IOException {
            if (socketChannel == null) throw new NullPointerException("socketChannel == null");
            socketChannel.write(byteBuffer);
        }

        public void close() throws IOException {
            if (socketChannel != null) {
                socketChannel.close();
            }
        }
    }
}
