package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;

import java.net.InetSocketAddress;
import java.util.Map;

public class CommandRequestPartList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
                          InetSocketAddress source) {
        CommandResponsePartList response = new CommandResponsePartList();

        Map<PartType, Integer> count = databaseManager.countPartsTypes(Part.PartState.STORED);
        for (PartType type : PartType.getTypes()) {
            response.addPartList(type, count.getOrDefault(type, 0));
        }

        commandManager.sendResponse(source, response);
    }

    /*

     */

}
