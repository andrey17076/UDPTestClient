import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Main {
    private static final int SELF_OPT = 0;
    private static final int EXIT_OPT = 1;
    private static final Scanner scanIn = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        UDPClient client = new UDPClient();
        client.start();

        int userOption;
        System.out.printf("Usage: 0 - start test; 1 - exit\n");
        do {
            userOption = Integer.parseInt(scanIn.nextLine());
            if (userOption == SELF_OPT)
                client.startTesting();
        } while (userOption != EXIT_OPT);
    }
}

class UDPClient extends Thread {
    private static final int PORT = 5555;
    private static final int BUFFER_SIZE = 1000;
    private static final int PACKAGE_QUAT = 1000;
    private static final int FIVE_SECONDS = 5000;
    private static final int START_MSG = 1;
    private static final int END_MSG = 2;

    protected DatagramSocket socket;
    protected DatagramPacket packet;
    protected byte[] buffer;
    protected InetAddress address;

    public UDPClient() throws SocketException, UnknownHostException {
        super("UDP CLIENT THREAD");

        buffer = new byte[BUFFER_SIZE];
        address = InetAddress.getByName("10.211.55.19");
        socket = new DatagramSocket(PORT);
    }

    public void startTesting() throws IOException, InterruptedException{
        buffer = new byte[buffer.length];

        buffer[0] = START_MSG;
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        socket.send(packet);

        buffer = new byte[buffer.length];
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);

        for (int i = 0; i < PACKAGE_QUAT; i++) {
            socket.send(packet);
        }

        Thread.sleep(FIVE_SECONDS);

        buffer[0] = END_MSG;
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        socket.send(packet);

    }

    @Override
    public void run() {
        packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                socket.receive(packet);
                int numberOfPackets = ByteBuffer.wrap(packet.getData()).getInt();
                System.out.println("Received data: " + Integer.toString(numberOfPackets));
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}


