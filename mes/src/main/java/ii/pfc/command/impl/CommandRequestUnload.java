package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.part.PartType;
import ii.pfc.part.xml.PartTypeAdapter;
import java.net.InetSocketAddress;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRequestUnload implements CommandRequest {

    private static final Logger logger = LoggerFactory.getLogger(CommandRequestTransform.class);

    /*

     */

    protected int orderId;

    @XmlAttribute(name = "Type")
    @XmlJavaTypeAdapter(PartTypeAdapter.class)
    private PartType partType;

    @XmlAttribute(name = "Destination")
    private int conveyorId;

    @XmlAttribute(name = "Quantity")
    private int quantity;

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, InetSocketAddress source) {
        logger.info("Received request: {}", this.toString());
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
