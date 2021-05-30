package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.order.TransformationOrder;
import ii.pfc.part.PartType;
import ii.pfc.part.xml.PartTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    public CommandRequestTransform() {

    }

    public CommandRequestTransform(int orderId, PartType sourceType, PartType targetType, int time, int quantity, int deadline, int penalty) {
        this.orderId = orderId;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.time = time;
        this.quantity = quantity;
        this.deadline = deadline;
        this.penalty = penalty;
    }

    /*

     */

    @Override
    public void onReceive(ICommandManager commandManager, IOrderManager orderManager, IDatabaseManager databaseManager,
                          InetSocketAddress source) {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.time), ZoneId.systemDefault());

        TransformationOrder order = new TransformationOrder(
                orderId,
                sourceType,
                targetType,
                time,
                LocalDateTime.now(),
                null,
                null,
                quantity,
                quantity,
                0,
                0,
                Duration.ofSeconds(deadline),
                penalty
        );

        orderManager.enqueueTransformationOrder(order);
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
