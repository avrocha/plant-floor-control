package ii.pfc;

import ii.pfc.manager.CommandManager;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.ICommsManager;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBException;

public class Factory {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //

    private final ICommsManager commsManager;

    private final ICommandManager commandManager;

    private boolean running = false;

    public Factory() {
        this.commsManager = new CommsManager(54321, new InetSocketAddress("127.0.0.1", 4840));
        this.commandManager = new CommandManager(commsManager);
    }

    /*

     */

    public void start() {
        this.executor.submit(this.commsManager::startUdpServer);

        this.running = true;
        while(running) {
            commandManager.pollRequests();
        }
    }

    public void stop() {
        this.running = false;
        this.commsManager.stopUdpServer();

        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }
    }

}
