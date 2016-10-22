package server.bios.asbserver.server;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import server.bios.asbserver.bus.BusStation;
import server.bios.asbserver.bus.CloseEvent;
import server.bios.asbserver.bus.LinkEvent;
import server.bios.asbserver.bus.NotifMsgEvent;
import server.bios.asbserver.client.DataExchange;
import server.bios.asbserver.server._interface._Response;
import server.bios.asbserver.server._interface._Socket;
import server.bios.asbserver.settings.Settings;
import server.bios.asbserver.utils.CharsetUtils;
import server.bios.asbserver.utils.Regex;

/**
 * Created by BIOS on 9/14/2016.
 */
public class Server {
    private static final String TAG = Server.class.getName();
    private static Settings settings = Settings.getInstance();
    private static CharsetUtils charsetUtils = CharsetUtils.getInstance();
    private static Regex regex = Regex.getInstance();
    private static final int CONNECTION_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 120;
    private HttpResponse httpResponse;
    private ServerSocket serverSocket;
    private boolean isStop;

    public Server() {
        try {
            serverSocket = new ServerSocket(settings.getASBServerPort());
            httpResponse = new HttpResponse(CONNECTION_TIMEOUT, READ_TIMEOUT);
            BusStation.getBus().register(this);
        } catch (IOException e) {
            Log.d(TAG, "Error bind to ASBServer", e);
        }
    }

    public void start() {

        try {
            while (!isStop) {
                _Socket socket = serverSocket.accept();

                String header = charsetUtils.charsetDecoder(socket.getData(), "UTF-8");

                if (!header.isEmpty()) {
                    String command = header.matches("(?s).*(acelive|torrent).*") ? "torrent" : "pid";
                    String content = null;
                    try {
                        content = URLDecoder.decode(regex.parser("/(.*?)\\sH.*", header, 1), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "The Character Encoding is not supported", e);
                    }

                    if (!command.isEmpty() && (content != null && !content.isEmpty())) {
                        DataExchange dataExchange = new DataExchange(socket, command, content);

                        dataExchange.connect();
                        if (dataExchange.isConnected()) {

                            new Thread(dataExchange).start();
                        } else {
                            Log.d(TAG, "Tray connection...");
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error connecting or read to socket channel", e);
            BusStation.getBus().post(new CloseEvent(true));
        } finally {
            BusStation.getBus().unregister(this);
        }
    }

    @Subscribe
    public void onEvent(CloseEvent close) {
        isStop = close.isClose;
    }

    @Subscribe
    public void onEvent(LinkEvent event) {
        _Socket socket = event.socket;
        _Response response = null;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(0xfff);
        ReadableByteChannel readableByteChannel = null;
        try {
            String link = event.link;
            if (!link.isEmpty()) {
                URL url = new URL(link);
                //Get the link from Ace Stream Server
                response = httpResponse.getResponse(url);

                String headers = response.getHeaderResponce().toString();
                socket.sendData(charsetUtils.charsetEncoder(headers, "UTF-8"));
                BufferedInputStream bufferedInputStream = new BufferedInputStream(response.byteStream());
                readableByteChannel = Channels.newChannel(bufferedInputStream);
                while (readableByteChannel.read(byteBuffer) > -1) {
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()) {
                        socket.sendData(byteBuffer);
                    }
                    byteBuffer.clear();
                }
                socket.getData();
            }

        } catch (IOException e) {}
        finally {
            try {
                if (readableByteChannel != null) readableByteChannel.close();
                if (socket != null) socket.close();
                if (response != null) response.close();
            } catch (IOException e) {
                Log.e(TAG, "Error close socket channel", e);
            }
        }
    }
}
