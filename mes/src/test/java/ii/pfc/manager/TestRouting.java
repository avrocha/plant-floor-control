package ii.pfc.manager;

import ii.pfc.Factory;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;
import ii.pfc.route.Route;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import junit.framework.TestCase;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.junit.Test;

public class TestRouting extends TestCase {

    private Factory factory;

    @Override
    protected void setUp() throws Exception {
        this.factory = new Factory();
    }

    @Test
    public void testRoutes() {
        Route route = this.factory.routingManager.traceRoute(null, this.factory.LIN13, this.factory.LIN10);

        try (PlcConnection plcConnection = this.factory.commsManager.getPlcConnection()) {
            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canWrite()) {
                System.err.println("This connection doesn't support writing.");
                return;
            }

            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
            builder.addItem("ID", "ns=4;s=|var|GVL.RouteData.Id", 2);

            int i = 0;
            for (Conveyor conveyor : route.getConveyors()) {
                builder.addItem(String.format("Conveyor[%d]", i), String.format("ns=4;s=|var|GVL.RouteData.ConveyorParts[%d]", i), conveyor.getId());
                i++;
            }

            PlcWriteRequest writeRequest = builder.build();

            try {
                PlcWriteResponse response = writeRequest.execute().get();

                for (String fieldName : response.getFieldNames()) {
                    if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                        System.out.println("Value[" + fieldName + "]: updated");
                    }
                    // Something went wrong, to output an error message instead.
                    else {
                        System.out.println("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } catch (PlcConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
