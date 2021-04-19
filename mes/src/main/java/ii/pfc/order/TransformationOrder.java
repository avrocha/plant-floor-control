package ii.pfc.order;

import ii.pfc.part.PartType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

public class TransformationOrder {

    private final int orderId;

    private final PartType sourceType;

    private final PartType targetType;

    private final LocalDateTime date;

    private final int quantity;

    private final LocalDateTime deadline;

    private final int penalty;

    private final TransformationState state;

    public TransformationOrder(int orderId, PartType sourceType, PartType targetType, LocalDateTime date, int quantity, LocalDateTime deadline, int penalty, TransformationState state) {
        this.orderId = orderId;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.date = date;
        this.quantity = quantity;
        this.deadline = deadline;
        this.penalty = penalty;
        this.state = state;
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

    public LocalDateTime getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public int getDayPenalty() {
        return penalty;
    }

    public int computePenalty(LocalDateTime currentDate) {
        if (currentDate.isBefore(deadline)) {
            return 0;
        }

        Duration duration = Duration.between(deadline, currentDate);
        return (int) (penalty * duration.toDays());
    }

    public TransformationState getState() {
        return state;
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
    /*

     */

    public static enum TransformationState {
        PENDING, IN_PROGRESS, COMPLETED
    }
}


