package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.part.PartType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRequestTransform implements CommandRequest {

    private static final Logger logger = LoggerFactory.getLogger(CommandRequestTransform.class);

    /*

     */

    protected int orderId;

    @XmlAttribute(name = "From")
    private String sourceType;

    @XmlAttribute(name = "To")
    private String targetType;

    @XmlAttribute(name = "Time")
    private int time;

    @XmlAttribute(name = "Quantity")
    private int quantity;

    @XmlAttribute(name = "MaxDelay")
    private int deadline;

    @XmlAttribute(name = "Penalty")
    private int penalty;


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
        return "CommandRequestTransform{" +
            "orderId=" + orderId +
            ", sourceType=" + sourceType +
            ", targetType=" + targetType +
            ", time=" + time +
            ", quantity=" + quantity +
            ", deadline=" + deadline +
            ", penalty=" + penalty +
            '}';
    }
}
