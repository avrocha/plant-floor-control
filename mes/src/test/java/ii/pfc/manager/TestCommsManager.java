package ii.pfc.manager;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import junit.framework.TestCase;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.junit.Test;

public class TestCommsManager {

    private ICommsManager commsManager = new CommsManager(new InetSocketAddress("127.0.0.1", 4840));

    @Test
    public void testRead() {
        this.commsManager.getPlcConnection((plcConnection) -> {
            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canRead()) {
                System.err.println("This connection doesn't support reading.");
                return;
            }

            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
            builder.addItem("pressure", "ns=2;i=3");
            builder.addItem("temperature", "ns=2;i=2");
            builder.addItem("pumpsetting", "ns=2;i=4");
            PlcReadRequest readRequest = builder.build();

            try {
                PlcReadResponse response = readRequest.execute().get(5000, TimeUnit.MILLISECONDS);

                for (String fieldName : response.getFieldNames()) {
                    if (response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                        int numValues = response.getNumberOfValues(fieldName);
                        // If it's just one element, output just one single line.
                        if (numValues == 1) {
                            System.out.println("Value[" + fieldName + "]: " + response.getObject(fieldName));
                        }
                        // If it's more than one element, output each in a single row.
                        else {
                            System.out.println("Value[" + fieldName + "]:");
                            for (int i = 0; i < numValues; i++) {
                                System.out.println(" - " + response.getObject(fieldName, i));
                            }
                        }
                    }
                    // Something went wrong, to output an error message instead.
                    else {
                        System.err.println("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

}
