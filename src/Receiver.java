import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


public class Receiver {

    private Parser parser = new Parser();

    public static void main(String[] args) {
        int port = args.length == 0 ? 20777 : Integer.parseInt(args[0]);
        new Receiver().run(port);
    }

    public void run(int port) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[256];
            String sendString = "polo";
            byte[] sendData = sendString.getBytes("UTF-8");

            System.out.printf("Listening on udp:%s:%d%n",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while(true)
            {
                serverSocket.receive(receivePacket);
//                String sentence = new String( receivePacket.getData(), 0,
//                        receivePacket.getLength() );
                byte[] data = receivePacket.getData();
                System.out.print("RECEIVED: ");
                printArray(parser.parseGForces(data));
                // now send acknowledgement packet back to sender
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        receivePacket.getAddress(), receivePacket.getPort());
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        // should close serverSocket in finally block
    }

    public void printArray(ArrayList<String> arr) {
        for (String s : arr) {
            System.out.print(s + "\t");
        }
        System.out.println();
    }
}