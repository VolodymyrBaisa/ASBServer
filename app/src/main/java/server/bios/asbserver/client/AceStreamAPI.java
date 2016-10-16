package server.bios.asbserver.client;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import server.bios.asbserver.utils.Algorithm;

/**
 * Created by BIOS on 9/11/2016.
 */
public final class AceStreamAPI {
    private volatile static AceStreamAPI aceStreamAPI;
    private static final Algorithm ALGORITHM = Algorithm.getInstance();

    public static final String PRODUCT_KEY = "n51LvQoTlJzNGaFxseRK-uvnvX-sD4Vm5Axwmc4UcoD-jruxmKsuJaH0eVgE";
    public static final String HELLO = "HELLOBG version=3";
    public static final String READY_KEY = "READY key=";
    public static final String HELLOTS = "HELLOTS";
    public static final String AUTH = "AUTH";
    public static final String LOADRESP = "LOADRESP";
    public static final String NOTREADY = "NOTREADY";
    public static final String STATUS = "STATUS";
    public static final String STATE = "STATE";
    public static final String EVENT = "EVENT";
    public static final String PAUSE = "PAUSE";
    public static final String RESUME = "RESUME";
    public static final String START = "START";
    public static final String PLAY = "PLAY";
    public static final String INFO = "INFO";
    public static final String STOP = "STOP";
    public static final String SHUTDOWN = "SHUTDOWN";

    private AceStreamAPI() {
    }

    public static AceStreamAPI getInstance() {
        if (aceStreamAPI == null) {
            synchronized (AceStreamAPI.class) {
                aceStreamAPI = new AceStreamAPI();
                return aceStreamAPI;
            }
        }
        return aceStreamAPI;
    }

    public String key(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String signature = ALGORITHM.SHA1(key.concat(PRODUCT_KEY));
        return PRODUCT_KEY.split("-")[0].concat("-").concat(signature);
    }

    public String userdata(String gender, String age) {
        return "USERDATA [{".concat(gender).concat("},{".concat(age).concat("}]"));
    }

    public String loadasync(String command, String content) {
        switch (command.toLowerCase()) {
            case "pid":
                return "LOADASYNC ".concat(String.valueOf(random())).concat(" PID ").concat(content);
            case "torrent":
                return "LOADASYNC ".concat(String.valueOf(random())).concat(" TORRENT ").concat(content).concat(" 0 0 0");
            default:
                return "";
        }
    }

    public String start(String command, String content) {
        switch (command.toLowerCase()) {
            case "pid":
                return "START PID ".concat(content).concat(" 0");
            case "torrent":
                return "START TORRENT ".concat(content).concat(" 0 0 0 0 0");
            default:
                return "";
        }
    }

    private int random() {
        int min = 500;
        int max = 1000;
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
