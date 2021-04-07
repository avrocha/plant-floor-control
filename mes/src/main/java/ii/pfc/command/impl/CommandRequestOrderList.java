package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import java.net.InetSocketAddress;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request_Orders")
public class CommandRequestOrderList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive(InetSocketAddress source) {
        System.out.println("Received request order list");
    }
}
