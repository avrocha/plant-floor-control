package ii.pfc.command;

import java.net.InetSocketAddress;

public interface CommandRequest {

    void onReceive(InetSocketAddress source);

}
