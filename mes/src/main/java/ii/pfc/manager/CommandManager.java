package ii.pfc.manager;

import ii.pfc.command.CommandRequest;
import ii.pfc.command.CommandResponse;
import ii.pfc.command.impl.BatchRequestOrderWrapper;
import ii.pfc.command.impl.RequestOrderWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class CommandManager implements ICommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    //

    private final ICommsManager commsManager;

    private final IOrderManager orderManager;

    private final IDatabaseManager databaseManager;

    private final Queue<CommandRequestSource> requestQueue = new ArrayBlockingQueue<>(10);

    public CommandManager(ICommsManager commsManager, IOrderManager orderManager, IDatabaseManager databaseManager) {
        this.commsManager = commsManager;
        this.orderManager = orderManager;
        this.databaseManager = databaseManager;

        this.commsManager.addUdpListener(this::enqueueRequest);
    }

    /*

     */

    @Override
    public void enqueueRequest(CommandRequest request, InetSocketAddress address) {
        requestQueue.offer(new CommandRequestSource(request, address));
    }

    @Override
    public void enqueueRequests(CommandRequest[] requests, InetSocketAddress address) {
        for (CommandRequest request : requests) {
            enqueueRequest(request, address);
        }
    }

    @Override
    public void enqueueRequest(String data, InetSocketAddress address) {
        try {
            BatchRequestOrderWrapper batchOrderWrapper = JAXB.unmarshal(new StringReader(data), BatchRequestOrderWrapper.class);
            if (batchOrderWrapper.getOrders() != null) {
                for (RequestOrderWrapper order : batchOrderWrapper.getOrders()) {
                    enqueueRequest(order.getRequest(), address);
                }

                return;
            }

            if (batchOrderWrapper.getOrderList() != null) {
                enqueueRequests(batchOrderWrapper.getOrderList(), address);
            }

            if (batchOrderWrapper.getPartList() != null) {
                enqueueRequests(batchOrderWrapper.getPartList(), address);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void pollRequests() {
        while (!this.requestQueue.isEmpty()) {
            CommandRequestSource requestSource = this.requestQueue.poll();
            requestSource.request.onReceive(this, this.orderManager, this.databaseManager, requestSource.source);
        }
    }

    @Override
    public void sendResponse(InetSocketAddress target, CommandResponse response) {
        StringWriter writer = new StringWriter();
        JAXB.marshal(response, writer);

        this.commsManager.sendUdpData(target, writer.toString());
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
