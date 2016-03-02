import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Main {
    private static final int SELF_OPT = 0;
    private static final int EXIT_OPT = 1;
    private static final int IO_ERROR = 2;
    private static final Scanner scanIn = new Scanner(System.in);

    public static void main(String[] args) {
        UDPClient client = new UDPClient();
        client.start();

        int userOption;
        System.out.printf("Usage: 0 - start test; 1 - exit\n");
        do {
            try {
                userOption = Integer.parseInt(scanIn.nextLine());
                if (userOption == SELF_OPT)
                    client.startTesting();
            } catch (NumberFormatException e) {
                userOption = IO_ERROR;
                System.err.println(e);
            }
        } while (userOption != EXIT_OPT);
    }
}

class UDPClient extends Thread {
    protected DatagramSocket socket;
    protected byte[] buffer;
    protected InetAddress address;
    private static final int PORT = 5555;
    private static final int BUFFER_SIZE = 1000;
    private static final int PACKAGE_QUAT = 1000;
    private static final int FIVE_SECONDS = 5000;

    public UDPClient() {
        super("UDP CLIENT THREAD");
        buffer = new byte[BUFFER_SIZE];

        try {
            address = InetAddress.getByName("10.211.55.19");
        } catch (UnknownHostException e) {
            System.err.println(e);
        }

        try {
            socket = new DatagramSocket(PORT);
            socket.setBroadcast(true);
        } catch (SocketException e) {
            System.err.println(e);
        }
    }

    public void startTesting() {
        buffer = new byte[buffer.length];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);

        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println(e);
        }

        try {
            Thread.sleep(FIVE_SECONDS);
        } catch (InterruptedException e) {
            System.err.println(e);
        }

        for (int i = 0; i < PACKAGE_QUAT; i++) {
            try {
                socket.send(packet);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public void run() {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
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


