package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.order.LoadOrder;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.UUID;

public class CommandRequestLoad implements CommandRequest {

    private final int orderId;

    private final int conveyorId;

    public CommandRequestLoad(int orderId, int conveyorId) {
        this.orderId = orderId;
        this.conveyorId = conveyorId;
    }

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
        InetSocketAddress source) {

        PartType type;

        switch(conveyorId) {
            // TODO
            case 3: {
                type = PartType.PART_2;
                break;
            }

            default: {
                type = PartType.PART_1;
                break;
            }
        }

        LoadOrder order = new LoadOrder(orderId, type, conveyorId, LocalDateTime.now(), LoadOrder.LoadState.PENDING);
        databaseManager.insertLoadOrder(order);
    }

    /*

     */
}
