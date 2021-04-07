package ii.pfc;

import ii.pfc.manager.CommandManager;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.ICommsManager;
import java.net.InetSocketAddress;

public class Factory {

    private final ICommsManager commsManager;

    private final ICommandManager commandManager;

    public Factory() {
        this.commsManager = new CommsManager(54321, new InetSocketAddress("127.0.0.1", 4840));
        this.commandManager = new CommandManager(commsManager);
    }

    /*

     */

    public void start() {

    }

}
