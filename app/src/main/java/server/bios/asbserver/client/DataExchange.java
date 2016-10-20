package server.bios.asbserver.client;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import server.bios.asbserver.bus.BusStation;
import server.bios.asbserver.bus.CloseEvent;
import server.bios.asbserver.bus.LinkEvent;
import server.bios.asbserver.server._interface._Socket;
import server.bios.asbserver.settings.Settings;
import server.bios.asbserver.utils.Regex;

/**
 * Created by BIOS on 9/11/2016.
 */
public class DataExchange implements Runnable {
    private static final String TAG = DataExchange.class.getName();
    private static final Settings SETTINGS = Settings.getInstance();
    private static final ChannelsStorage CHANNELS_STORAGE = ChannelsStorage.getInstance();
    private static final Regex REGEX = Regex.getInstance();
    private static final AceStreamAPI ACE_STREAM_API = AceStreamAPI.getInstance();
    private ClientSocket clientSocket;
    private String command;
    private String content;
    private _Socket socket;
    private volatile boolean isStop;

    public DataExchange(_Socket socket, String command, String content) {
        this.socket = socket;
        this.command = command;
        this.content = content;
        clientSocket = new ClientSocket(SETTINGS.getAceStreamIP(), SETTINGS.getAceStreamPort());
        BusStation.getBus().register(this);
    }

    public void connect() {
        try {
            clientSocket.connect();
        } catch (IOException e) {
            Log.d(TAG, "Error connecting to Ace Stream engine");
        }
    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }

    @Override
    public void run() {
        try {
            clientSocket.sendData(AceStreamAPI.HELLO.concat("\r\n"));

            String response = getResponse();
            clientSocket.sendData(AceStreamAPI.READY_KEY.concat(ACE_STREAM_API.key(REGEX.parser("key=(.*?)\\s", response, 1))).concat("\r\n"));

            clientSocket.sendData(ACE_STREAM_API.userdata(SETTINGS.getGender(), SETTINGS.getAge()).concat("\r\n"));
            getResponse();

            clientSocket.sendData(ACE_STREAM_API.loadasync(command, content).concat("\r\n"));

            String channel = getResponse();

            if (!CHANNELS_STORAGE.contains(channel)) {
                clientSocket.sendData(ACE_STREAM_API.start(command, content).concat("\r\n"));

                String link = getResponse();
                CHANNELS_STORAGE.put(channel, link);

                new Thread(() -> {
                    System.out.println("1");
                    BusStation.getBus().post(new LinkEvent(link, socket));
                    System.out.println("1");
                }).start();

                getResponse();

                CHANNELS_STORAGE.remove(channel);
            } else {
                String link = CHANNELS_STORAGE.get(channel);
                new Thread(() -> {
                    System.out.println("2");
                    BusStation.getBus().post(new LinkEvent(link, socket));
                    System.out.println("2");
                }).start();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error write buffer to socket channel", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "The Character Encoding is not supported or requested algorithm is not available in the environment", e);
        } finally {
            try {
                if (isStop) {
                    clientSocket.sendData(AceStreamAPI.STOP.concat("\r\n"));
                    getResponse();
                }
                clientSocket.sendData(AceStreamAPI.SHUTDOWN.concat("\r\n"));
                getResponse();
                clientSocket.close();
                BusStation.getBus().unregister(this);

                Log.d(TAG, "SHUTDOWN");
            } catch (IOException e) {
                Log.e(TAG, "Error close socket channel", e);
            }
        }
    }

    private String getResponse() {
        try {
            do {
                String[] lines = clientSocket.getData().split("\r\n");
                for (String response : lines) {
                    Log.d(TAG, response);
                    String responseAPI = REGEX.parser("\\w+(?=\\s|$)", response);
                    switch (responseAPI) {
                        case AceStreamAPI.HELLOTS:
                        case AceStreamAPI.AUTH:
                            return response;
                        case AceStreamAPI.LOADRESP:
                            return REGEX.parser("\\[\\[\"(.*)\".*\\]\\]", response, 1);
                        case AceStreamAPI.START:
                            String link = REGEX.parser("START\\shttp://[0-9.:]*(.*(m3u8|[0-9].[0-9]*$))", response, 1);
                            link = "http://".concat(SETTINGS.getAceStreamIP()).concat(":")
                                    .concat(String.valueOf(SETTINGS.getAceStreamOutVideoPort()))
                                    .concat(link);
                            return link;
                        case AceStreamAPI.EVENT:
                            break;
                        case AceStreamAPI.NOTREADY:
                            return "";
                        case AceStreamAPI.INFO:
                            if (response.contains("1")) return "";
                            break;
                        case AceStreamAPI.STATE:
                            if (response.contains("6")) return "";
                            if (response.contains("0")) return "";
                            break;
                        case AceStreamAPI.STATUS:
                            if (response.contains("err")) return "";
                            break;
                        case AceStreamAPI.STOP:
                            return "";
                        case AceStreamAPI.SHUTDOWN:
                            return "";
                    }
                }
            } while (!isStop);
        } catch (IOException e) {
            Log.e(TAG, "Error read buffer from socket channel", e);
        }
        return "";
    }

    @Subscribe
    public void onEvent(CloseEvent close) {
        isStop = close.isClose;
    }
}