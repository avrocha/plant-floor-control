package ii.pfc.manager;

import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;
import ii.pfc.route.Route;
import ii.pfc.udp.UdpListener;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

import java.net.InetSocketAddress;

public interface ICommsManager {

    void startUdpServer();

    void stopUdpServer();

    void sendUdpData(InetSocketAddress target, String data);

    void addUdpListener(UdpListener listener);

    /*

     */

    PlcConnection getPlcConnection() throws PlcConnectionException;

    /*

     */

    void dispatchWarehouseInConveyorEntry(short conveyorId);

    Part getWarehouseInConveyorPart(short conveyorId);

    /*

     */

    void dispatchWarehouseOutConveyorExit(short conveyorId, PartType type);

    boolean getWarehouseOutConveyorStatus(short conveyorId);

    /*

     */

    boolean getLoadConveyorStatus(short conveyorId);

    /*

     */

    int getSliderConveyorOccupation(short conveyorId);

    /*

     */

    short getAssemblyConveyorTool(short conveyorId);

    boolean getAssemblyConveyorOccupation(short conveyorId);

    /*

     */
    void sendPlcRoute(Route route);

    void sendPlcRoute(Route route, Process process);
}
