package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    static Receiver receiver = new Receiver();

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
        response.append("\"{");
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> list = data.get(i);
            response.append("\\\"").append(list.get(0)).append("\\\": [");
            for (int j = 1; j < list.size(); j++) {
                String s = list.get(j);
                response.append(s);
                if(j != list.size() - 1) {
                    response.append(", ");
                }
            }
            response.append("]");
            if(i != data.size() - 1) {
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

class QueryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        String query = exchange.getRequestURI().getQuery().substring(2);
        String[] moduleList = query.split(",");
        for (String s :
                moduleList) {
            System.out.println(s);
            response += "<script>";
            File file;
            try {
                file = new File("src/module/" + s + ".js");
                BufferedReader bufferedReader =
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file)));
                String line = bufferedReader.readLine();
                while(line != null) {
                    response += line + "\n";
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            response += "</script>";
        }
        OutputStream os;
        if(query.isEmpty()) {
            exchange.sendResponseHeaders(418, "i'm a teapot".length());
            os = exchange.getResponseBody();
            os.write("i'm a teapot".getBytes());
        } else {
            exchange.sendResponseHeaders(200, response.length());
            os = exchange.getResponseBody();
            os.write(response.getBytes());
        }
        os.close();
    }

    private String buildFile(ArrayList<File> files) {
        return null;
    }
}