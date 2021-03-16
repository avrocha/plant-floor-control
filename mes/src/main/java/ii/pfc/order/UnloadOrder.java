package ii.pfc.order;

import java.util.Date;

public class UnloadOrder {
    private final int orderId;

    private final int conveyorId;

    private final Date date;

    private final int quantity;

    public UnloadOrder(int orderId, int conveyorId, Date date, int quantity){
        this.orderId = orderId;
        this.conveyorId = conveyorId;
        this.date = date;
        this.quantity = quantity;
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

    public int getQuantity(){ return quantity; }
}
