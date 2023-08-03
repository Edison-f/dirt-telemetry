package server;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.*;


public class Receiver {

    private final Parser parser = new Parser();

    public static void main(String[] args) {
        int port = args.length == 0 ? 20777 : Integer.parseInt(args[0]);
        new Receiver().run(port);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run(int port) {
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            byte[] receiveData = new byte[256];
            String sendString = "polo";
            byte[] sendData = sendString.getBytes(StandardCharsets.UTF_8);

            System.out.printf("Listening on udp:%s:%d%n",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket);
                byte[] data = receivePacket.getData();
                System.out.print("RECEIVED: ");
                printArray(parser.parseGForces(data));
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        receivePacket.getAddress(), receivePacket.getPort());
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            System.out.println("Closing socket");
        }
    }

    public ArrayList<ArrayList<String>> getAll() {
        try (DatagramSocket serverSocket = new DatagramSocket(20777)) {
            serverSocket.setReuseAddress(true);
            byte[] receiveData = new byte[256];
            String sendString = "polo";
            byte[] sendData = sendString.getBytes(StandardCharsets.UTF_8);
            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
            byte[] data;
            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Object> task = () -> {
                serverSocket.receive(receivePacket);
                return null;
            };
            Future<Object> future = executor.submit(task);
            try {
                future.get(100, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {
                System.out.println("Timeout - No data found from game");
                serverSocket.close();
                return null;
            } finally {
                future.cancel(true);
            }
            data = receivePacket.getData();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            serverSocket.send(sendPacket);
            return parser.parseAll(data);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("No information found");
        return null;
    }

    public void printArray(ArrayList<String> arr) {
        for (String s : arr) {
            System.out.print(s + "\t");
        }
        System.out.println();
    }
}