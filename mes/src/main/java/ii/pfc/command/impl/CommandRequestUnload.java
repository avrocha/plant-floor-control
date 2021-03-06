package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.order.UnloadOrder;
import ii.pfc.part.PartType;
import ii.pfc.part.xml.PartTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class CommandRequestUnload implements CommandRequest {

    private static final Logger logger = LoggerFactory.getLogger(CommandRequestTransform.class);

    /*

     */

    protected int orderId;

    @XmlAttribute(name = "Type")
    @XmlJavaTypeAdapter(PartTypeAdapter.class)
    private PartType partType;

    @XmlAttribute(name = "Destination")
    private short conveyorId;

    @XmlAttribute(name = "Quantity")
    private int quantity;

    /*

     */

    public CommandRequestUnload() {

    }

    public CommandRequestUnload(int orderId, PartType partType, short conveyorId, int quantity) {
        this.orderId = orderId;
        this.partType = partType;
        this.conveyorId = conveyorId;
        this.quantity = quantity;
    }

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
                          InetSocketAddress source) {
        UnloadOrder order = new UnloadOrder(
                orderId,
                partType,
                conveyorId,
                LocalDateTime.now(),
                quantity,
                quantity,
                0
        );

        orderManager.enqueueUnloadOrder(order);
    }

    /*

     */

    @Override
    public String toString() {
        return "CommandRequestUnload{" +
                "orderId=" + orderId +
                ", partType=" + partType +
                ", conveyorId=" + conveyorId +
                ", quantity=" + quantity +
                '}';
    }
}
