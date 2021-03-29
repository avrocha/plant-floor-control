package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;

public class CommandRequestLoad implements CommandRequest {

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request load");
    }

    /*

     */
}
