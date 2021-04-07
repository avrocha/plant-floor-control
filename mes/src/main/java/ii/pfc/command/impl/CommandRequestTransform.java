package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.part.PartType;
import ii.pfc.part.xml.PartTypeAdapter;
import java.net.InetSocketAddress;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRequestTransform implements CommandRequest {

    private static final Logger logger = LoggerFactory.getLogger(CommandRequestTransform.class);

    /*

     */

    protected int orderId;

    @XmlAttribute(name = "From")
    @XmlJavaTypeAdapter(PartTypeAdapter.class)
    private PartType sourceType;

    @XmlAttribute(name = "To")
    @XmlJavaTypeAdapter(PartTypeAdapter.class)
    private PartType targetType;

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
    public void onReceive(InetSocketAddress source) {
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
