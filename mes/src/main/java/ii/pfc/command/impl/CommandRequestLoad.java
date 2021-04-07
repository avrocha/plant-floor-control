package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import java.net.InetSocketAddress;

public class CommandRequestLoad implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(InetSocketAddress source) {
        System.out.println("Received request load");
    }

    /*

     */
}
