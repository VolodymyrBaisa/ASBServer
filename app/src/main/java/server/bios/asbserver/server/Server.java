package server.bios.asbserver.server;

import android.os.SystemClock;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import server.bios.asbserver.bus.BusStation;
import server.bios.asbserver.bus.CloseEvent;
import server.bios.asbserver.bus.LinkEvent;
import server.bios.asbserver.bus.NotifMsgEvent;
import server.bios.asbserver.client.DataExchange;
import server.bios.asbserver.client.autostart.StartAceServer;
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
    private static final Settings SETTINGS = Settings.getInstance();
    private static final CharsetUtils CHARSET_UTILS = CharsetUtils.getInstance();
    private static final Regex REGEX = Regex.getInstance();
    private static final int CONNECTION_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 120;
    private HttpResponse httpResponse;
    private ServerSocket serverSocket;
    private boolean isStop;

    public Server() {
        try {
            serverSocket = new ServerSocket(SETTINGS.getASBServerPort());
            httpResponse = new HttpResponse(CONNECTION_TIMEOUT, READ_TIMEOUT);
            BusStation.getBus().register(this);
        } catch (IOException e) {
            Log.d(TAG, "Error bind to ASBServer", e);
        }
    }

    public void start() {
        BusStation.getBus().post(new NotifMsgEvent("Start"));
        try {
            while (true) {
                _Socket socket = serverSocket.accept();

                if (isStop) break;
                String header = CHARSET_UTILS.charsetDecoder(socket.getData(), "UTF-8");

                if (!header.isEmpty()) {
                    String command = header.matches(".*http://.*.acelive.*") ? "torrent" : "pid";
                    String content = null;
                    try {
                        content = URLDecoder.decode(REGEX.parser("/(.*?)\\sH.*", header, 1), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "The Character Encoding is not supported", e);
                    }

                    if (!command.isEmpty() && (content != null && !content.isEmpty())) {
                        DataExchange dataExchange = new DataExchange(socket, command, content);

                        boolean isConnected;

                        do {
                            dataExchange.connect();
                            isConnected = dataExchange.isConnected();
                            if (isConnected) {
                                BusStation.getBus().post(new NotifMsgEvent("Running"));
                                new Thread(dataExchange).start();
                            } else {
                                StartAceServer startAceServer = StartAceServer.getInstance();
                                startAceServer.start();

                                SystemClock.sleep(5000);

                                Log.d(TAG, "Tray connection...");
                            }
                        } while (!isStop && !isConnected);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error connecting or read to socket channel", e);
            BusStation.getBus().post(new CloseEvent(true));
        } finally {
            BusStation.getBus().post(new NotifMsgEvent("Destroy"));
            BusStation.getBus().unregister(this);
        }
    }

    @Subscribe
    public void onEvent(CloseEvent close) {
        isStop = close.isClose;
    }

    @Subscribe
    public void onEvent(LinkEvent event) {
        new Thread(() -> {
            _Socket socket = event.socket;
            _Response response = null;
            try {
                URL url = new URL(event.link);
                //Get the link from Ace Stream Server
                response = httpResponse.getResponse(url);

                String headers = response.getHeaderResponce().toString();
                socket.sendData(CHARSET_UTILS.charsetEncoder(headers, "UTF-8"));

                socket.sendData(CHARSET_UTILS.charsetEncoder(response.getString(), "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) socket.close();
                    if (response != null) response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
