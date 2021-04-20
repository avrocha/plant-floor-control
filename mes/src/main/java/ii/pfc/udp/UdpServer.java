package ii.pfc.udp;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UdpServer {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    //

    private final int port;

    private final List<UdpListener> udpListeners = new ArrayList<>();

    //

    private DatagramSocket socket;

    private boolean running = false;

    private byte[] buffer = new byte[2048];

    public UdpServer(int port) {
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

                String data = new String(buffer, packet.getOffset(), packet.getLength());
                InetSocketAddress address = new InetSocketAddress(packet.getAddress(), packet.getPort());

                for (UdpListener udpListener : this.udpListeners) {
                    udpListener.onReceive(data, address);
                }
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

    public void addListener(UdpListener listener) {
        this.udpListeners.add(listener);
    }

}
