package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import java.util.logging.Logger;

public class CommandRequestLoad implements CommandRequest {

    private final int orderId;

    public CommandRequestLoad(int orderId) {
        this.orderId = orderId;
    }

    /*

     */

    public int getOrderId() {
        return orderId;
    }

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request load");
    }
}
