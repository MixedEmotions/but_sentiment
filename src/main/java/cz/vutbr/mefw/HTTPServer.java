package cz.vutbr.mefw;

import org.glassfish.grizzly.http.server.*;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;


public class HTTPServer {

    private ProcessorPool processorPool;
    HttpServer server;

    public HTTPServer(ProcessorPool processorPool){
        this.processorPool = processorPool;
    }



    public void start(){
        try {
            server.start();
            System.out.println("Starting HTTP server");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop(){


    }

    public void initialize(String host, Integer port){
        server = new HttpServer();
        NetworkListener listener = new NetworkListener("grizzly", host, port);
        listener.setSecure(false);
        server.addListener(listener);
        ServerConfiguration config = server.getServerConfiguration();

        final Map<String, ProcessorAdapter> processors = processorPool.getAll();
        Set<String> keys = processors.keySet();
        HttpHandler handler;
        for (final String key : keys) {
            handler = new HttpHandler() {
                @Override
                public void service(Request request, Response response) throws Exception {
                    ProcessorAdapter processor = processors.get(key);
                    final char[] buf = new char[128];
                    Writer out = null;
                    String input = new String();
                    String output;
                    Map<String, String> query;
                    try {
                        input = request.getQueryString(); // put the stream in blocking mode
                        query = splitQuery(input);

                        //TODO: Think about using MIME types instead query keys
                        if (query.containsKey("text")) {
                            output = processor.process(input);
                        } else if (query.containsKey("jsonld")) {
                            //System.out.println("jsonld key found");
                            //output = processor.process(input);
                            output = new String("sory not implemented yet");
                        } else {
                            response.setStatus(404);
                            output = new String("Invalid query key. Use text or jsonld");
                        }

                        out = response.getWriter();
                        out.write(output.toCharArray());
                        out.flush();

                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ignore) {
                            }
                        }
                    }
                }

                public Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
                    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
                    String[] pairs = query.split("&");
                    for (String pair : pairs) {
                        int idx = pair.indexOf("=");
                        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                    }
                    return query_pairs;
                }
            };

            config.addHttpHandler(handler, "/"+key);
        }

        //config.addHttpHandler(handler, uri.getPath());

    }



}