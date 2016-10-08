package server.bios.asbserver.bus;

/**
 * Created by BIOS on 10/1/2016.
 */

public class CloseEvent {
    public final boolean isClose;

    public CloseEvent(Boolean isClose) {
        this.isClose = isClose;
    }
}
