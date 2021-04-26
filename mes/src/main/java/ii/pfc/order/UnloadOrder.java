package ii.pfc.order;


import ii.pfc.part.PartType;

import java.time.LocalDateTime;

public class UnloadOrder {

    private final int orderId;

    private final PartType partType;

    private final short conveyorId;

    private final LocalDateTime date;

    private final int quantity;

    private final int remaining;

    private final int completed;

    public UnloadOrder(int orderId, PartType partType, short conveyorId, LocalDateTime date, int quantity, int remaining, int completed) {
        this.orderId = orderId;
        this.partType = partType;
        this.conveyorId = conveyorId;
        this.date = date;
        this.quantity = quantity;
        this.remaining = remaining;
        this.completed = completed;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public PartType getPartType() {
        return partType;
    }

    public short getConveyorId() {
        return conveyorId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getRemaining() {
        return remaining;
    }

    public int getCompleted() {
        return completed;
    }

    /*

     */

    @Override
    public String toString() {
        return "UnloadOrder{" +
                "orderId=" + orderId +
                ", partType=" + partType +
                ", conveyorId=" + conveyorId +
                ", date=" + date +
                ", quantity=" + quantity +
                ", remaining=" + remaining +
                ", completed=" + completed +
                '}';
    }
}
