package server.bios.asbserver.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by BIOS on 10/1/2016.
 */

public class PlayerController {
    private Map<String, Integer> storage;
    private volatile static PlayerController channelsStorage;

    private PlayerController() {
        storage = new ConcurrentHashMap<>();
    }

    public static PlayerController getInstance() {
        if (channelsStorage == null) {
            synchronized (PlayerController.class) {
                channelsStorage = new PlayerController();
                return channelsStorage;
            }
        }
        return channelsStorage;
    }

    public boolean contains(String channel) {
        return storage.containsKey(channel);
    }

    public synchronized void put(String channel, Integer count) {
        storage.put(channel, count);
    }

    public Integer get(String channel) {
        return storage.get(channel);
    }

    public void remove(String channel) {
        storage.remove(channel);
    }

    public void clear() {
        storage.clear();
    }
}
