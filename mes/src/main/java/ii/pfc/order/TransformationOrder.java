package ii.pfc.order;

import ii.pfc.part.PartType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

public class TransformationOrder {

    private final int orderId;

    private final PartType sourceType;

    private final PartType targetType;

    private final Date date;

    private final int quantity;

    private final Date deadline;

    private final int penalty;

    public TransformationOrder(int orderId, PartType sourceType, PartType targetType, Date date, int quantity, Date deadline, int penalty) {
        this.orderId = orderId;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.date = date;
        this.quantity = quantity;
        this.deadline = deadline;
        this.penalty = penalty;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public PartType getSourceType() {
        return sourceType;
    }

    public PartType getTargetType() {
        return targetType;
    }

    public Date getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDeadline() {
        return deadline;
    }

    public int getDayPenalty() {
        return penalty;
    }

    public int computePenalty(Date currentDate) {
        if (currentDate.before(deadline)) {
            return 0;
        }

        // TODO calculate days
        int days = 1;

        return penalty * days;
    }

    @Override
    public String toString() {
        return "TransformationOrder{" +
                "orderId=" + orderId +
                ", sourceType=" + sourceType +
                ", targetType=" + targetType +
                ", date=" + date +
                ", quantity=" + quantity +
                ", deadline=" + deadline +
                ", penalty=" + penalty +
                '}';
    }
}
