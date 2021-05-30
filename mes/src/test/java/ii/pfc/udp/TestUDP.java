package ii.pfc.udp;

import ii.pfc.manager.CommandManager;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.ICommsManager;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.InetSocketAddress;

public class TestUDP extends TestCase {

    private ICommsManager commsManager;

    private ICommandManager commandManager;

    @Override
    protected void setUp() throws Exception {
        this.commsManager = new CommsManager(54321, new InetSocketAddress("127.0.0.1", 4840));
        this.commandManager = new CommandManager(commsManager, null, null);
    }

    @Test
    public void testUDP() {
        this.commsManager.startUdpServer();

        while (true) {
        }
    }

}
