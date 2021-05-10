package ii.pfc.order;

import ii.pfc.part.PartType;

import java.time.Duration;
import java.time.LocalDateTime;

public class TransformationOrder implements Comparable<TransformationOrder> {

    private final int orderId;

    private final PartType sourceType;

    private final PartType targetType;

    private final LocalDateTime date;

    private final LocalDateTime receivedDate;

    private final LocalDateTime startDate;

    private final LocalDateTime finishDate;

    private final int quantity;

    private final int remaining;

    private final int holding;

    private final int completed;

    private final LocalDateTime deadline;

    private final int penalty;

    public TransformationOrder(int orderId, PartType sourceType, PartType targetType, LocalDateTime date, LocalDateTime receivedDate, LocalDateTime startDate, LocalDateTime finishDate, int quantity,
                               int remaining, int holding, int completed, LocalDateTime deadline, int penalty) {
        this.orderId = orderId;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.date = date;
        this.receivedDate = receivedDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.quantity = quantity;
        this.remaining = remaining;
        this.holding = holding;
        this.completed = completed;
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

    public LocalDateTime getDate() {
        return date;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRemaining() {
        return remaining;
    }

    public int getHolding() {
        return holding;
    }

    public int getCompleted() {
        return completed;
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
        return (int) (penalty * Math.ceil((duration.toSeconds() / 50)));
    }

    /*
     */

    @Override
    public int compareTo(TransformationOrder o) {
        LocalDateTime now = LocalDateTime.now();

        return Integer.compare(computePenalty(now), o.computePenalty(now));
    }

    @Override
    public String toString() {
        return "TransformationOrder{" +
                "orderId=" + orderId +
                ", sourceType=" + sourceType +
                ", targetType=" + targetType +
                ", date=" + date +
                ", receivedDate=" + receivedDate +
                ", quantity=" + quantity +
                ", remaining=" + remaining +
                ", holding=" + holding +
                ", completed=" + completed +
                ", deadline=" + deadline +
                ", penalty=" + penalty +
                '}';
    }
}


