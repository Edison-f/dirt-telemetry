package server;

import java.io.IOException;
import java.io.OutputStream;
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


    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/echo", new TelemetryHandler());
        server.start();
    }
}

class TelemetryHandler implements HttpHandler {
    Receiver receiver = new Receiver();
    public void handle(HttpExchange xchg) throws IOException {
        Headers headers = xchg.getRequestHeaders();
        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();

        StringBuffer response = new StringBuffer();
        ArrayList<ArrayList<String>> data = receiver.getAll();
        if (data == null) {
            xchg.sendResponseHeaders(200, 0);
            OutputStream os = xchg.getResponseBody();
            os.close();
            return;
        }
        for (ArrayList<String> list : data) {
            for (String s : list) {
                response.append(s + "\t");
            }
            response.append("\n");
        }
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
