package server.bios.asbserver.bus;

import server.bios.asbserver.server._interface._Socket;
import server.bios.asbserver.utils.Timer;

/**
 * Created by BIOS on 10/1/2016.
 */

public class LinkEvent {
    public final String link;
    public final _Socket socket;
    public final Timer timer;

    public LinkEvent(String link, _Socket socket, Timer timer) {
        this.link = link;
        this.socket = socket;
        this.timer = timer;
    }
}
