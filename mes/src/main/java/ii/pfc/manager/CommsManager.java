package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.EnumTool;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.part.Process;
import ii.pfc.route.Route;
import ii.pfc.udp.UdpListener;
import ii.pfc.udp.UdpServer;
import org.apache.commons.lang3.tuple.Pair;
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

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    private boolean isConnected() {
        return true;
    }

    /*

     */

    @Override
    public void dispatchWarehouseInConveyorEntry(short conveyorId) {
        if (!isConnected()) {
            return;
        }

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
        if (!isConnected()) {
            return null;
        }

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

            if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                if (response.getBoolean(fieldName)) {
                    return new Part(UUID.fromString(response.getString("Id")), 0, PartType.getType(response.getString("Type")), Part.PartState.STORED);
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
        if (!isConnected()) {
            return;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            builder.addItem("EWOD",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWO%d.NewPartType", conveyorId), type.getInternalId());
            builder.addItem("EWOE",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWO%d.ExtractPart", conveyorId), true);

            PlcWriteRequest writeRequest = builder.build();

            // Async execution
            writeRequest.execute().get(1000, TimeUnit.SECONDS);
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getWarehouseOutConveyorStatus(short conveyorId) {
        if (!isConnected()) {
            return false;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            String fieldName = "Sensor";
            builder.addItem(fieldName,
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CWO%d.ReadyToExtract", conveyorId));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
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
    public boolean getLoadConveyorStatus(short conveyorId) {
        if (!isConnected()) {
            return false;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            String fieldName = "Sensor";
            builder.addItem(fieldName,
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CLD%d.ReadyToReceive", conveyorId));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
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
    public int getSliderConveyorOccupation(short conveyorId) {
        if (!isConnected()) {
            return -1;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            String fieldName = "SliderStatus";
            builder.addItem(fieldName,
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.SlidersStatus[%d]", (short) (conveyorId - 60)));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                return response.getInteger(fieldName);
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public void prepareAssemblyTool(short conveyorId, EnumTool tool) {
        if (!isConnected()) {
            return;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            builder.addItem("TOOL",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.DesiredTool", conveyorId), tool.getId());
            builder.addItem("PTOOL",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.PrepareTool", conveyorId), true);

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
    public EnumTool getAssemblyConveyorTool(short conveyorId) {
        if (!isConnected()) {
            return null;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            builder.addItem("ConveyorActualTool",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.ActualTool", (short) (conveyorId)));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);

            if (response.getResponseCode("ConveyorActualTool") == PlcResponseCode.OK) {
                return EnumTool.getTool(response.getShort("ConveyorActualTool"));
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean getAssemblyConveyorOccupation(short conveyorId) {
        if (!isConnected()) {
            return false;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            builder.addItem("ConveyorHasPart",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.Si", (short) (conveyorId)));
            builder.addItem("ConveyorIsReserved",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.IsReserved", (short) (conveyorId)));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);
            if (response.getResponseCode("ConveyorHasPart") == PlcResponseCode.OK && response.getResponseCode("ConveyorIsReserved") == PlcResponseCode.OK) {
                return response.getBoolean("ConveyorHasPart") || response.getBoolean("ConveyorIsReserved");
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Pair<UUID, PartType> getAssemblyConveyorCompletedStatus(short conveyorId) {
        if (!isConnected()) {
            return null;
        }

        try (PlcConnection plcConnection = getPlcConnection()) {
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

            builder.addItem("AssembleDone",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.AssembleDone", (short) (conveyorId)));
            builder.addItem("ID",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.CurrentPartId", (short) (conveyorId)));
            builder.addItem("TYPE",
                    String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.CurrentTargetType", (short) (conveyorId)));

            PlcReadRequest readRequest = builder.build();
            PlcReadResponse response = readRequest.execute().get(1000, TimeUnit.MILLISECONDS);
            if (response.getResponseCode("AssembleDone") == PlcResponseCode.OK && response.getResponseCode("ID") == PlcResponseCode.OK && response.getResponseCode("TYPE") == PlcResponseCode.OK) {
                if (response.getBoolean("AssembleDone")) {
                    return Pair.of(UUID.fromString(response.getString("ID")), PartType.getType(response.getString("TYPE")));
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
    public void sendPlcRoute(Route route) {
        sendPlcRoute(route, null);
    }

    @Override
    public void sendPlcRoute(Route route, Process process) {
        if (!isConnected()) {
            return;
        }

        System.out.println("Send route " + route.toString());

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

            if (process != null) {
                builder.addItem("TOOL", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.Tool", (short) process.getTool().getId());
                builder.addItem("TARGET", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.TargetType", process.getResult().getName());
                System.out.println("Adding target " + process.getResult().getName());
                builder.addItem("ASSEMBLETIME", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.AssembleTime", (short) process.getDuration().toSeconds());
                builder.addItem("RESERVE", String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.Reserve", route.getTarget().getId()), true);
                builder.addItem("ASSTOOL", String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.PlantFloor.CA%d.DesiredTool", route.getTarget().getId()), (short) process.getTool().getId());
            } else {
                builder.addItem("TOOL", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.Tool", (short) 0);
                builder.addItem("ASSEMBLETIME", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RouteData.AssembleTime", (short) 0);
            }

            PlcWriteRequest writeRequest = builder.build();

            // Async execution
            PlcWriteResponse response = writeRequest.execute().get(1000, TimeUnit.SECONDS);

            for (String fieldName : writeRequest.getFieldNames()) {
                //System.out.println(fieldName + " - " + response.getResponseCode(fieldName));
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*

     */


}
