package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.route.Route;
import java.net.InetSocketAddress;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;

public class CommsManager implements ICommsManager {

    private final InetSocketAddress plcAddress;

    private final PlcDriverManager plcDriverManager;

    public CommsManager(InetSocketAddress plcAddress) {
        this.plcAddress = plcAddress;
        this.plcDriverManager = new PlcDriverManager();
    }

    /*

     */

    @Override
    public PlcConnection getPlcConnection() throws PlcConnectionException {
        String opcConnection = String.format("opcua:tcp://%s:%d?discovery=false", plcAddress.getHostName(), plcAddress.getPort());
        System.out.println(String.format("OPCUA ADDRESS: %s", opcConnection));

        PlcConnection connection = this.plcDriverManager.getConnection(opcConnection);
        return connection;
    }

    @Override
    public void sendPlcRoute(Route route) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            StringBuilder serializedRoute = new StringBuilder();
            serializedRoute = serializedRoute.append(route.getPart().getId().toString());

            for(Conveyor conveyor : route.getConveyors()) {
                serializedRoute = serializedRoute.append(',').append(conveyor.getId());
            }

            builder.addItem(
                "RouteData",
                "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData",
                serializedRoute.toString()
            );

            PlcWriteRequest writeRequest = builder.build();

            // Async execution
            writeRequest.execute();
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
