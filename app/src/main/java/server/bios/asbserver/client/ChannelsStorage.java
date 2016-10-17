package server.bios.asbserver.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by BIOS on 10/1/2016.
 */

public class ChannelsStorage {
    private Map<String, Pair> storage;
    private volatile static ChannelsStorage channelsStorage;

    private ChannelsStorage() {
        storage = new HashMap<>();
    }

    public static ChannelsStorage getInstance() {
        if (channelsStorage == null) {
            synchronized (ChannelsStorage.class) {
                channelsStorage = new ChannelsStorage();
                return channelsStorage;
            }
        }
        return channelsStorage;
    }

    public boolean contains(String channel) {
        return storage.containsKey(channel);
    }

    public void put(String channel, Pair<String, Boolean> pair) {
        storage.put(channel, pair);
    }

    public Pair<String, Boolean> get(String channel) {
        return storage.get(channel);
    }

    public Set<Map.Entry<String, Pair>> iterator() {
        return storage.entrySet();
    }

    public void remove(String channel) {
        storage.remove(channel);
    }

    public void clear() {
        storage.clear();
    }
}
