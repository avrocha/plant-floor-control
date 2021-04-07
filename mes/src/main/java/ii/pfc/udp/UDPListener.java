package ii.pfc.udp;

import java.net.InetSocketAddress;

public interface UDPListener {

    void onReceive(String data, InetSocketAddress source);

}