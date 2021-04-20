package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import java.net.InetSocketAddress;
import javax.xml.bind.annotation.XmlRootElement;

public class CommandRequestPartList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, InetSocketAddress source) {
        System.out.println("received request");

        CommandResponsePartList response = new CommandResponsePartList();
        commandManager.sendResponse(source, response);
    }

    /*

     */

}
