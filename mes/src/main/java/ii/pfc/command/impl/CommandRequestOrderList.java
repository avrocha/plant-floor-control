package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;

public class CommandRequestOrderList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request order list");
    }
}
