package ii.pfc;

import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommsManager;
import java.net.InetSocketAddress;

public class Factory {

    private final ICommsManager commsManager;

    public Factory() {
        this.commsManager = new CommsManager(new InetSocketAddress("127.0.0.1", 4840));
    }

    /*

     */

    public void start() {

    }

}
