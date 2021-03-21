package ii.pfc.manager;

import java.util.function.Consumer;
import org.apache.plc4x.java.api.PlcConnection;

public interface ICommsManager {

    void getPlcConnection(Consumer<PlcConnection> consumer);

}
