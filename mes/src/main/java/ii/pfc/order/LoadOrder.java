package ii.pfc.order;

import java.time.LocalDateTime;
import java.util.Date;

public class LoadOrder {

    private final int orderId;

    private final int conveyorId;

    private final LocalDateTime date;

    public LoadOrder(int orderId, int conveyorId, LocalDateTime date){
        this.orderId = orderId;
        this.conveyorId = conveyorId;
        this.date = date;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public int getConveyorId() {
        return conveyorId;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
