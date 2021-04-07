package ii.pfc.udp;

import java.net.InetSocketAddress;

public interface UdpListener {

    void onReceive(String data, InetSocketAddress source);

}