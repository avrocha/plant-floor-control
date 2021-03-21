package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;

public class CommandRequestUnload implements CommandRequest {

    private final int orderId;

    private final int quantity;

    public CommandRequestUnload(int orderId, int quantity) {
        this.orderId = orderId;
        this.quantity = quantity;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request unload");
    }
}
