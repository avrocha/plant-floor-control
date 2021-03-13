package ii.pfc.manager;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

public class CommsManager implements ICommsManager {

    private final InetSocketAddress plcAddress;

    private final PlcDriverManager plcDriverManager;

    public CommsManager(InetSocketAddress plcAddress) {
        this.plcAddress = plcAddress;
        this.plcDriverManager = new PlcDriverManager();
    }

    /*

     */

    public void getPlcConnection(Consumer<PlcConnection> consumer) {
        String opcConnection = String.format("opcua://%s:%d?discovery=true", plcAddress.getHostName(), plcAddress.getPort());
        try (PlcConnection connection = this.plcDriverManager.getConnection(opcConnection)) {
            consumer.accept(connection);
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
