package ii.pfc.manager;

import ii.pfc.command.CommandRequest;
import ii.pfc.command.CommandResponse;
import ii.pfc.command.impl.BatchRequestOrderWrapper;
import ii.pfc.command.impl.CommandRequestOrderList;
import ii.pfc.command.impl.CommandRequestPartList;
import ii.pfc.command.impl.RequestOrderWrapper;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;
import javax.xml.bind.JAXB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandManager implements ICommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommsManager.class);

    //

    private final ICommsManager commsManager;

    private final Queue<CommandRequestSource> requestQueue = new LinkedList<>();

    public CommandManager(ICommsManager commsManager) {
        this.commsManager = commsManager;
        this.commsManager.addUDPListener(this::enqueueRequest);
    }

    /*

     */

    private void enqueueRequest(CommandRequest request, InetSocketAddress address) {
        if(request != null) {
            logger.info("Received XML request: {}", request.toString());
            requestQueue.offer(new CommandRequestSource(request, address));
        }
    }

    @Override
    public void enqueueRequest(String data, InetSocketAddress address) {
        StringReader reader = new StringReader(data);

        BatchRequestOrderWrapper batchOrderWrapper = JAXB.unmarshal(reader, BatchRequestOrderWrapper.class);
        if (batchOrderWrapper != null) {
            for (RequestOrderWrapper order : batchOrderWrapper.getOrders()) {
                enqueueRequest(order.getRequest(), address);
            }

            return;
        }

        RequestOrderWrapper orderWrapper = JAXB.unmarshal(reader, RequestOrderWrapper.class);
        if (orderWrapper != null) {
            enqueueRequest(orderWrapper.getRequest(), address);
            return;
        }

        CommandRequestOrderList orderList = JAXB.unmarshal(reader, CommandRequestOrderList.class);
        if (orderList != null) {
            enqueueRequest(orderList, address);
            return;
        }

        CommandRequestPartList partList = JAXB.unmarshal(reader, CommandRequestPartList.class);
        if (partList != null) {
            enqueueRequest(partList, address);
            return;
        }
    }

    @Override
    public void pollRequests() {

    }

    @Override
    public void sendResponse(InetSocketAddress target, CommandResponse response) {
        StringWriter writer = new StringWriter();
        JAXB.marshal(response, writer);

        this.commsManager.sendUDPData(target, writer.toString());
    }

    /*

     */

    private class CommandRequestSource {

        private final CommandRequest request;

        private final InetSocketAddress source;

        private CommandRequestSource(CommandRequest request, InetSocketAddress source) {
            this.request = request;
            this.source = source;
        }
    }
}
