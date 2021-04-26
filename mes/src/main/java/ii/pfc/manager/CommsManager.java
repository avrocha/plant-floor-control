package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.route.Route;
import ii.pfc.udp.UdpListener;
import ii.pfc.udp.UdpServer;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.utils.connectionpool.PooledPlcDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommsManager implements ICommsManager {

    private static final Logger logger = LoggerFactory.getLogger(CommsManager.class);

    //

    private final UdpServer server;

    //

    private final InetSocketAddress plcAddress;

    private final String opcConnectionUrl;

    private final PlcDriverManager plcDriverManager;

    public CommsManager(int udpPort, InetSocketAddress plcAddress) {

        this.server = new UdpServer(udpPort);

        this.plcAddress = plcAddress;
        this.opcConnectionUrl = String.format("opcua:tcp://%s:%d?discovery=false", plcAddress.getHostName(), plcAddress.getPort());
        this.plcDriverManager = new PooledPlcDriverManager();
    }

    /*

     */

    @Override
    public void startUdpServer() {
        this.server.bind();
    }

    @Override
    public void stopUdpServer() {
        this.server.close();
    }

    @Override
    public void sendUdpData(InetSocketAddress target, String data) {
        this.server.send(target, data);
    }

    @Override
    public void addUdpListener(UdpListener listener) {
        this.server.addListener(listener);
    }

    /*

     */

    @Override
    public PlcConnection getPlcConnection() throws PlcConnectionException {
        PlcConnection connection = this.plcDriverManager.getConnection(opcConnectionUrl);
        return connection;
    }

    /*

     */

    @Override
    public void dispatchWarehouseInConveyorEntry(short conveyorId) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            builder.addItem("EWI",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWI%d.EntryWarehouseI", conveyorId), true);

            PlcWriteRequest writeRequest = builder.build();

            // Async execution
            writeRequest.execute();
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*

     */

    @Override
    public Part getWarehouseInConveyorPart(short conveyorId) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            String fieldName = "Sensor";
            builder.addItem(fieldName,
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWI%d.ReadyToEntry", conveyorId));
            builder.addItem("Type",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWI%d.CurrentPartType", conveyorId));
            builder.addItem("Id",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWI%d.CurrentPartId", conveyorId));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                if (response.getBoolean(fieldName)) {
                    return new Part(UUID.fromString(response.getString("Id")), 0, PartType.getType(response.getString("Type")));
                }
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*

     */

    @Override
    public void dispatchWarehouseOutConveyorExit(short conveyorId, PartType type) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            builder.addItem("EWOD",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWO%d.NewPartType", conveyorId), type.getInternalId());
            builder.addItem("EWOE",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWO%d.ExtractPart", conveyorId), true);

            PlcWriteRequest writeRequest = builder.build();

            // Async execution
            writeRequest.execute();
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getWarehouseOutConveyorStatus(short conveyorId) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            String fieldName = "Sensor";
            builder.addItem(fieldName,
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWO%d.ReadyToExtract", conveyorId));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                return response.getBoolean(fieldName);
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /*

     */

    @Override
    public boolean getLoadConveyorStatus(short conveyorId) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            String fieldName = "Sensor";
            builder.addItem(fieldName,
                String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CLD%d.ReadyToReceive", conveyorId));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                return response.getBoolean(fieldName);
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void sendPlcRoute(Route route) {
        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            int i = 1;
            for (Conveyor conveyor : route.getConveyors()) {
                builder.addItem(String.format("Conveyor[%d]", i), String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.Route[%d]", i), conveyor.getId());
                i++;
            }
            builder.addItem("Type", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.PartType", route.getPart().getType().getName());
            builder.addItem("ID", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.PartId", route.getPart().getId().toString());
            builder.addItem("CHECK", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.CheckPart", true);

            PlcWriteRequest writeRequest = builder.build();

            // Async execution
            writeRequest.execute();
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*

     */


}
