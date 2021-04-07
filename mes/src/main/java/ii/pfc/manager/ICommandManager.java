package ii.pfc.manager;

import ii.pfc.command.CommandResponse;
import java.net.InetSocketAddress;

public interface ICommandManager {

    void enqueueRequest(String data, InetSocketAddress source);

    void pollRequests();

    /*

     */

    void sendResponse(InetSocketAddress target, CommandResponse response);

}
