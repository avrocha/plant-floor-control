package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.order.TransformationOrder;

import java.net.InetSocketAddress;
import java.time.ZoneId;
import java.util.Collection;

public class CommandRequestOrderList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
                          InetSocketAddress source) {
        CommandResponseOrderList response = new CommandResponseOrderList();

        Collection<TransformationOrder> orders = databaseManager.fetchAllTransformOrders();
        for (TransformationOrder order : orders) {
            response.addOrder(order.getOrderId(), order.getSourceType(), order.getTargetType(), order.getQuantity(), order.getCompleted(), order.getQuantity() - order.getCompleted() - order.getHolding() - order.getRemaining(), order.getHolding(), (int) order.getDate().atZone(ZoneId.systemDefault()).toEpochSecond(), 0, (int) order.getDeadline().atZone(ZoneId.systemDefault()).toEpochSecond(), order.getDayPenalty(), order.getStartDate() == null ? 0 : (int) order.getStartDate().atZone(ZoneId.systemDefault()).toEpochSecond(), order.getFinishDate() == null ? 0 : (int) order.getFinishDate().atZone(ZoneId.systemDefault()).toEpochSecond(), 0);
        }

        commandManager.sendResponse(source, response);
    }
}
