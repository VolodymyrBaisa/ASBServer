package server.bios.asbserver.bus;

import server.bios.asbserver.server._interface._Socket;

/**
 * Created by BIOS on 10/1/2016.
 */

public class LinkEvent {
    public final String link;
    public final _Socket socket;

    public LinkEvent(String link, _Socket socket) {
        this.link = link;
        this.socket = socket;
    }
}
