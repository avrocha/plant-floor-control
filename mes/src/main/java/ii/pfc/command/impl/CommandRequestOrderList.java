package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.order.TransformationOrder;
import java.net.InetSocketAddress;
import java.util.Collection;

public class CommandRequestOrderList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
        InetSocketAddress source) {
        CommandResponseOrderList response = new CommandResponseOrderList();

        Collection<TransformationOrder> orders = databaseManager.fetchAllTransformOrders();
        for(TransformationOrder order : orders) {
        }

        commandManager.sendResponse(source, response);
    }
}
