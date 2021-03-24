package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import javax.xml.bind.annotation.XmlAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRequestUnload implements CommandRequest {

    private static final Logger logger = LoggerFactory.getLogger(CommandRequestTransform.class);

    /*

     */

    protected int orderId;

    @XmlAttribute(name = "Type")
    private String partType;

    @XmlAttribute(name = "Destination")
    private int conveyorId;

    @XmlAttribute(name = "Quantity")
    private int quantity;

    /*

     */

    @Override
    public void onReceive() {
        logger.info("Received request: {}", this.toString());
    }

    /*

     */

    @Override
    public String toString() {
        return "CommandRequestUnload{" +
            "orderId=" + orderId +
            ", partType='" + partType + '\'' +
            ", conveyorId=" + conveyorId +
            ", quantity=" + quantity +
            '}';
    }
}
