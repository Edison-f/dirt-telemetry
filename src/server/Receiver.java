package server;

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
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[256];
            String sendString = "polo";
            byte[] sendData = sendString.getBytes("UTF-8");

            System.out.printf("Listening on udp:%s:%d%n",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while (true) {
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
        } finally {
            System.out.println("Closing socket");
            serverSocket.close();
        }
    }

    public ArrayList<ArrayList<String>> getAll() {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(20777);
            byte[] receiveData = new byte[256];
            String sendString = "polo";
            byte[] sendData = sendString.getBytes("UTF-8");
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
            serverSocket.receive(receivePacket);
            byte[] data = receivePacket.getData();
            // now send acknowledgement packet back to sender
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            serverSocket.send(sendPacket);
            return parser.parseAll(data);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            System.out.println("Closing socket");
            serverSocket.close();
        }
        return null;
    }

    public void printArray(ArrayList<String> arr) {
        for (String s : arr) {
            System.out.print(s + "\t");
        }
        System.out.println();
    }
}