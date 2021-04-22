package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import java.net.InetSocketAddress;

public class CommandRequestLoad implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
        InetSocketAddress source) {
        System.out.println("Received request load");
    }

    /*

     */
}
