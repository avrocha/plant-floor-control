package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;

public class CommandRequestPartList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request part list");
    }
}
