package ii.pfc.manager;

import ii.pfc.Factory;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.route.Route;

import java.util.UUID;
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
        Part part = new Part(UUID.randomUUID(), 0, PartType.PART_1);
        Route route = this.factory.routingManager.traceRoute(null, this.factory.LIN8, this.factory.LIN7);

        try (PlcConnection plcConnection = this.factory.commsManager.getPlcConnection()) {
            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canWrite()) {
                System.err.println("This connection doesn't support writing.");
                return;
            }

            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();

            int i = 1;
            for (Conveyor conveyor : route.getConveyors()) {
                builder.addItem(String.format("Conveyor[%d]", i), String.format("ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RoutePart.route[%d]", i), conveyor.getId());
                i++;
            }
            builder.addItem("ID", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.GVL.RoutePart.id", part.getId().toString());

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
