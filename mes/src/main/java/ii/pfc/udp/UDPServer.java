package ii.pfc.udp;

import com.google.common.base.Charsets;
import ii.pfc.command.impl.CommandRequestTransform;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UDPServer {

    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);

    //

    private final int port;

    //

    private DatagramSocket socket;

    private boolean running = false;

    private byte[] buffer = new byte[2046];

    public UDPServer(int port) {
        this.port = port;
    }

    /*

     */

    public void bind() {
        try {
            this.socket = new DatagramSocket(this.port);
            this.running = true;

            logger.info("UDP server running in port {}", this.port);

            this.listenForPackets();
        } catch(SocketException ex) {
            logger.error("Could not start UDP server");
            ex.printStackTrace();
        }
    }

    public void close() {
        this.running = false;
    }

    private void listenForPackets() {
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String string = new String(buffer, packet.getOffset(), packet.getLength());
                InetSocketAddress address = new InetSocketAddress(packet.getAddress(), packet.getPort());

                this.onReceive(string, address);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*

     */

    public void send(InetSocketAddress target, String data) {
        if (!running) {
            return;
        }

        try {
            DatagramPacket packet = new DatagramPacket(data.getBytes(Charsets.UTF_8), 0, data.length(), target);
            this.socket.send(packet);
        } catch (IOException e) {
            logger.error("Could not send UDP packet");
            e.printStackTrace();
        }
    }

    /*

     */

    public abstract void onReceive(String data, InetSocketAddress address);

}
