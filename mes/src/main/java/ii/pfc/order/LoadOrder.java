package ii.pfc.order;

import java.util.Date;

public class LoadOrder {

    private final int orderId;

    private final int conveyorId;

    private final Date date;

    public LoadOrder(int orderId, int conveyorId, Date date){
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

    public Date getDate() {
        return date;
    }
}
