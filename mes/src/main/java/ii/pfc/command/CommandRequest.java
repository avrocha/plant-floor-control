package ii.pfc.command;

import ii.pfc.manager.ICommandManager;
import java.net.InetSocketAddress;

public interface CommandRequest {

    void onReceive(ICommandManager commandManager, InetSocketAddress source);

}
