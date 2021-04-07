package ii.pfc.manager;

import ii.pfc.route.Route;
import ii.pfc.udp.UDPListener;
import java.net.InetSocketAddress;
import java.util.function.Consumer;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

public interface ICommsManager {

    void startServer();

    void stopServer();

    void sendUDPData(InetSocketAddress target, String data);

    void addUDPListener(UDPListener listener);

    /*

     */

    PlcConnection getPlcConnection() throws PlcConnectionException;

    void sendPlcRoute(Route route);

}
