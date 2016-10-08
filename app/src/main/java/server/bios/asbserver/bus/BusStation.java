package server.bios.asbserver.bus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by BIOS on 10/1/2016.
 */

public class BusStation {
    private static EventBus bus = new EventBus();

    public static EventBus getBus(){
        return bus;
    }
}
