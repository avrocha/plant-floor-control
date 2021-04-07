package ii.pfc.manager;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.route.Route;
import ii.pfc.udp.UDPListener;
import ii.pfc.udp.UDPServer;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommsManager implements ICommsManager {

    private static final Logger logger = LoggerFactory.getLogger(CommsManager.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //

    private final UDPServer server;

    private final List<UDPListener> udpListeners = new ArrayList<>();

    //

    private final InetSocketAddress plcAddress;

    private final PlcDriverManager plcDriverManager;

    public CommsManager(int udpPort, InetSocketAddress plcAddress) {

        this.server = new UDPServer(udpPort) {
            @Override
            public void onReceive(String data, InetSocketAddress address) {
                for (UDPListener udpListener : udpListeners) {
                    udpListener.onReceive(data, address);
                }
            }
        };

        this.plcAddress = plcAddress;
        this.plcDriverManager = new PlcDriverManager();
    }

    /*

     */

    @Override
    public void startServer() {
        executor.submit(this.server::bind);
    }

    @Override
    public void stopServer() {
        this.server.close();

        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }

    }

    @Override
    public void sendUDPData(InetSocketAddress target, String data) {
        this.server.send(target, data);
    }

    @Override
    public void addUDPListener(UDPListener listener) {
        this.udpListeners.add(listener);
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

            for (Conveyor conveyor : route.getConveyors()) {
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

    /*

     */


}
