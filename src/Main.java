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
        int userOption;
        System.out.println("Usage: 0 - start test; 1 - exit");
        do {
            userOption = Integer.parseInt(scanIn.nextLine());
            if (userOption == SELF_OPT)
                client.startTesting();
        } while (userOption != EXIT_OPT);
    }
}

class UDPClient {
    private static final int PORT = 5555;
    private static final int BUFFER_SIZE = 1000;
    private static final int PACKAGE_QUANT = 1000;
    private static final int TIMEOUT = 5000;

    private static final int START_MSG = PACKAGE_QUANT + 1;
    private static final int END_MSG = START_MSG + 1;

    protected final DatagramSocket socket;
    protected DatagramPacket packet;
    protected byte[] buffer;
    protected InetAddress address;

    public UDPClient() throws SocketException, UnknownHostException {
        buffer = new byte[BUFFER_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);
        address = InetAddress.getByName("10.211.55.19");
        socket = new DatagramSocket(PORT);
    }

    protected void sendPackage(int valueToSend) throws IOException{
        buffer = ByteBuffer.allocate(BUFFER_SIZE).putInt(valueToSend).array();
        packet = new DatagramPacket(buffer, buffer.length, address, PORT);
        socket.send(packet);
    }

    public void startTesting() throws IOException, InterruptedException{
        sendPackage(START_MSG);
        System.out.println("\nTest started\n===================");

        Thread.sleep(TIMEOUT);
        long startTime = System.currentTimeMillis();
        for (int packIndex = 0; packIndex < PACKAGE_QUANT; packIndex++) {
            sendPackage(packIndex);
        }
        long transferringTime = System.currentTimeMillis() - startTime;
        Thread.sleep(TIMEOUT);

        sendPackage(END_MSG);
        System.out.println("\nTest finished\nWaiting for results\n===================");

        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        int numberOfReceivedPackages = ByteBuffer.wrap(packet.getData()).getInt();
        int lost = PACKAGE_QUANT - numberOfReceivedPackages;
        String testResult = String.format("Sent: %d\nLost: %d\nPercent of lost: %d%%\nSpeed: %d kbit/s",
                PACKAGE_QUANT, lost, lost * 100 / PACKAGE_QUANT, numberOfReceivedPackages * 1000 / transferringTime);

        System.out.println(testResult);
    }
}


