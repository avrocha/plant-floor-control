package ii.pfc.command;

import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import java.net.InetSocketAddress;

public interface CommandRequest {

    void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
        InetSocketAddress source);

}
