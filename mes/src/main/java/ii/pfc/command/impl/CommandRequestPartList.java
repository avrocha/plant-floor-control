package ii.pfc.command.impl;

import ii.pfc.command.CommandRequest;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Request_Stores")
public class CommandRequestPartList implements CommandRequest {

    /*

     */

    @Override
    public void onReceive() {
        System.out.println("Received request part list");
    }
}
