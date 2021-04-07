package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import java.net.InetSocketAddress;
import javax.xml.bind.annotation.XmlRootElement;

public class CommandRequestOrderList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, InetSocketAddress source) {
        CommandResponseOrderList response = new CommandResponseOrderList();
        commandManager.sendResponse(source, response);
    }
}
