package ii.pfc.order;


import ii.pfc.part.PartType;

import java.time.LocalDateTime;

public class UnloadOrder {

    private final int orderId;

    private final PartType partType;

    private final short conveyorId;

    private final LocalDateTime date;

    private final int quantity;

    private final UnloadState state;

    public UnloadOrder(int orderId, PartType partType, short conveyorId, LocalDateTime date, int quantity, UnloadState state) {
        this.orderId = orderId;
        this.partType = partType;
        this.conveyorId = conveyorId;
        this.date = date;
        this.quantity = quantity;
        this.state = state;
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

    public UnloadState getState() {
        return state;
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
                '}';
    }

    /*

     */

    public static enum UnloadState {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
