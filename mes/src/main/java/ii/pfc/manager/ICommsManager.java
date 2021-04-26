package ii.pfc.manager;

import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.part.PartType;
import ii.pfc.route.Route;
import ii.pfc.udp.UdpListener;
import java.net.InetSocketAddress;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

public interface ICommsManager {

    void startUdpServer();

    void stopUdpServer();

    void sendUdpData(InetSocketAddress target, String data);

    void addUdpListener(UdpListener listener);

    /*

     */

    PlcConnection getPlcConnection() throws PlcConnectionException;

    void dispatchWarehouseInConveyorEntry(short conveyorId);

    PartType getWarehouseInConveyorPart(short conveyorId);

    boolean getLoadConveyorStatus(short conveyorId);

    void sendPlcRoute(Route route);

}
