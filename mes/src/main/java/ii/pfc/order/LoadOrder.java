package ii.pfc.order;

import ii.pfc.part.PartType;

import java.time.LocalDateTime;

public class LoadOrder {

    private final int orderId;

    private final PartType type;

    private final short conveyorId;

    private final LocalDateTime date;

    private final LoadState state;

    public LoadOrder(int orderId, PartType type, short conveyorId, LocalDateTime date, LoadState state){
        this.orderId = orderId;
        this.type = type;
        this.conveyorId = conveyorId;
        this.date = date;
        this.state = state;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public PartType getType() {
        return type;
    }

    public short getConveyorId() {
        return conveyorId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public LoadState getState() {
        return state;
    }

    /*

     */

    public static enum LoadState {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
