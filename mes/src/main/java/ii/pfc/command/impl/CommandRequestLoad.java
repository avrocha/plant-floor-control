package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import java.net.InetSocketAddress;

public class CommandRequestLoad implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, InetSocketAddress source) {
        System.out.println("Received request load");
    }

    /*

     */
}
