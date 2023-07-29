package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    static Receiver receiver = new Receiver();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/echo", new TelemetryHandler(receiver));
        server.createContext("/rpm", new RPMHandler(receiver));
        server.createContext("/view", new PageHandler("index.html"));
        server.createContext("/script.js", new PageHandler("script.js"));
        server.createContext("/style.css", new PageHandler("style.css"));
        server.start();
    }
}

class TelemetryHandler implements HttpHandler {
    Receiver receiver;

    public TelemetryHandler(Receiver receiver) {
        super();

        this.receiver = receiver;
    }

    public void handle(HttpExchange xchg) throws IOException {
        xchg.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (xchg.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            xchg.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            xchg.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            xchg.sendResponseHeaders(204, -1);
            return;
        }
        StringBuilder response = new StringBuilder();
        ArrayList<ArrayList<String>> data = receiver.getAll();
        if (data == null) {
            xchg.sendResponseHeaders(200, 0);
            OutputStream os = xchg.getResponseBody();
            os.close();
            return;
        }
        for (ArrayList<String> list : data) {
            for (String s : list) {
                response.append(s).append("\t");
            }
            response.append("\n");
        }
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}

class RPMHandler implements HttpHandler {
    Receiver receiver;

    public RPMHandler(Receiver receiver) {
        super();
        this.receiver = receiver;
    }

    public void handle(HttpExchange xchg) throws IOException {
        xchg.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (xchg.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            xchg.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            xchg.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            xchg.sendResponseHeaders(204, -1);
            return;
        }
        StringBuilder response = new StringBuilder();
        ArrayList<String> data = receiver.getAll().get(0);
        if (data == null) {
            xchg.sendResponseHeaders(200, 0);
            OutputStream os = xchg.getResponseBody();
            os.close();
            return;
        }
        for (String s : data) {
            response.append(s).append("\n");
        }
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}

class SpeedHandler implements HttpHandler {
    Receiver receiver;

    public SpeedHandler(Receiver receiver) {
        super();
        this.receiver = receiver;
    }

    public void handle(HttpExchange xchg) throws IOException {
        Headers headers = xchg.getRequestHeaders();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();

        StringBuilder response = new StringBuilder();
        ArrayList<String> data = receiver.getAll().get(1);
        if (data == null) {
            xchg.sendResponseHeaders(200, 0);
            OutputStream os = xchg.getResponseBody();
            os.close();
            return;
        }
        for (String s : data) {
            response.append(s).append("\n");
        }
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}

class PageHandler implements HttpHandler {

    String fileName;

    public PageHandler(String fileName) {
        super();
        this.fileName = fileName;
    }

    public void handle(HttpExchange xchg) throws IOException {
        File file;
        String response = "";
        try {
            file = new File("src/webview/" + fileName);
            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(file)));
            String curr = bufferedReader.readLine();
            while(curr != null) {
                response += curr + "\n";
                curr = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}