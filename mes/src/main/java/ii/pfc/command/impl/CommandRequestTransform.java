package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import ii.pfc.order.TransformationOrder;

public class CommandRequestTransform implements CommandRequest {

    private final TransformationOrder order;

    public CommandRequestTransform(TransformationOrder order) {
        this.order = order;
    }

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request load");
    }
}
