package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    static final Receiver receiver = new Receiver();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/telemetry", new TelemetryHandler(receiver));
        server.createContext("/view", new PageHandler("index.html"));
        server.createContext("/script.js", new PageHandler("script.js"));
        server.createContext("/style.css", new PageHandler("style.css"));
        server.createContext("/module", new QueryHandler());
        server.start();
    }
}

record TelemetryHandler(Receiver receiver) implements HttpHandler {

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
        response.append("\"{");
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> list = data.get(i);
            response.append("\\\"").append(list.get(0)).append("\\\": [");
            for (int j = 1; j < list.size(); j++) {
                String s = list.get(j);
                response.append(s);
                if (j != list.size() - 1) {
                    response.append(", ");
                }
            }
            response.append("]");
            if (i != data.size() - 1) {
                response.append(",");
            }
        }
        response.append("}\"");
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}

record PageHandler(String fileName) implements HttpHandler {

    public void handle(HttpExchange xchg) throws IOException {
        File file;
        StringBuilder response = new StringBuilder();
        try {
            file = new File("src/webview/" + fileName);
            readFile(file, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    static void readFile(File file, StringBuilder response) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(file)));
        String curr = bufferedReader.readLine();
        while (curr != null) {
            response.append(curr).append("\n");
            curr = bufferedReader.readLine();
        }
        bufferedReader.close();
    }
}

class QueryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder response = new StringBuilder();
        String query = exchange.getRequestURI().getQuery().substring(2);
        String[] moduleList = query.split(",");
        for (String s :
                moduleList) {
            System.out.println(s);
            response.append("<script>");
            File file;
            try {
                file = new File("src/module/" + s + ".js");
                PageHandler.readFile(file, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.append("</script>");
        }
        OutputStream os;
        if(query.isEmpty()) {
            exchange.sendResponseHeaders(418, "i'm a teapot".length());
            os = exchange.getResponseBody();
            os.write("i'm a teapot".getBytes());
        } else {
            exchange.sendResponseHeaders(200, response.length());
            os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
        }
        os.close();
    }

}