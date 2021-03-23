package ii.pfc.manager;

import ii.pfc.route.Route;
import java.util.function.Consumer;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

public interface ICommsManager {

    PlcConnection getPlcConnection() throws PlcConnectionException;

    void sendPlcRoute(Route route);

}
