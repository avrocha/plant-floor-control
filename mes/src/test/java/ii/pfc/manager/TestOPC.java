package ii.pfc.manager;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.junit.Test;

public class TestOPC {

    private ICommsManager commsManager = new CommsManager(new InetSocketAddress("94.60.138.247", 4840));

    @Test
    public void testRead() {
        this.commsManager.getPlcConnection((plcConnection) -> {
            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canRead()) {
                System.err.println("This connection doesn't support reading.");
                return;
            }

            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
            builder.addItem("ALT5_MOTOR_NEG", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.IoConfig_Globals_Mapping.ALT5_MOTOR_NEG");
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

    @Test
    public void testWrite() {
        this.commsManager.getPlcConnection((plcConnection) -> {
            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canWrite()) {
                System.err.println("This connection doesn't support writing.");
                return;
            }

            PlcWriteRequest.Builder builder = plcConnection.writeRequestBuilder();
            builder.addItem("ALT5_MOTOR_NEG", "ns=4;s=|var|CODESYS Control Win V3 x64.Application.IoConfig_Globals_Mapping.ALT5_MOTOR_NEG",
                true);
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
        });
    }

}
