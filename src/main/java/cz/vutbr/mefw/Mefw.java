package cz.vutbr.mefw;

import org.docopt.Docopt;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.*;

import cz.vutbr.mefw.Mefw;


public class Mefw {


    private ProcessorPool processorPool;
    private HTTPServer server;
    private Config config;

    private static String doc =
            "Mixed Emotions IO\n"
                    + "\n"
                    + "Usage:\n"
                    + "  mefw server <ip> [--port=<port> --config=<conf>]\n"
                    + "  mefw process <processor> <inputfile> <outputfile> [--config=<conf>]\n"
                    + "  mefw list processors [--config=<conf>]\n"
                    + "  mefw (-h | --help)\n"
                    + "  mefw --version\n"
                    + "\n"
                    + "Options:\n"
                    + "  --config=<conf>        Show this screen.\n"
                    + "  -h --help        Show this screen.\n"
                    + "  --version        Show version.\n"
                    + "  <ip>             IP address on which server will listen [default: 0.0.0.0].\n"
                    + "  --port=<num>     Server listening port [default: 80].\n"
                    + "\n";


    public Mefw(){
        config = new Config();
        processorPool =  new ProcessorPool(config);
        server  = new HTTPServer(processorPool);

    }

    public void loadConfig(String filepath){
        try {
            this.config.load(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processFile(String in, String out, String processorName)
    {
        processorPool.load(processorName);
        ProcessorAdapter processor = processorPool.get(processorName);
        // xorman00 kind error handling in cleaner way
        if(processor==null){
            System.err.println("Module/Processor could not be initialized.");
            return;
        }
        try {
            String input = new Scanner(new File(in)).useDelimiter("\\Z").next();
            String output = processor.process(input);
            PrintWriter outputfile = new PrintWriter(out);
            outputfile.println(output);
            outputfile.close();
        }
        /*
        try (Stream<String> stream = Files.lines(Paths.get(fileName)))
        {
            stream.forEach(System.out::println);
        }
         */
        catch (FileNotFoundException e0 )
        {
            System.err.println("Input file \""+in+"\" not found.");
            //e0.printStackTrace();
        }
        catch (IllegalStateException e1 )
        {
            System.err.println("Module/Processor was not initialized properly or at all.");
            //e1.printStackTrace();
        }
        /*
        catch (IOException e3 )
        {
            System.err.println("Input file \""+in+"\" not found.");
            //e0.printStackTrace();
        }
        */

    }

    private void startServer(String host, Integer port){
        System.out.println("Initialize HTTP server at: " + host + ":" + port);
        processorPool.loadAll();
        server.initialize(host, port);
        server.start();
    }

    private void listProcessors(){
        System.out.println("List of available processors:");
        List<String> classes = processorPool.getProcessorList(true);
        for (int i = 0; i < classes.size(); i++) {
            System.out.println(classes.get(i));
        }
        processorPool.loadAll();
    }




    public static void main(String [] args){
        Map<String, Object> opts = new Docopt(doc).withVersion("Mixed Emotions 0.1").parse(args);
        //TODO: make global debug mode
        System.out.println(opts);
        Mefw core = new Mefw();
        // xorman00 I am not sure, but it seems like path to config file is missing
        if((String)opts.get("--config") != null){
            core.loadConfig((String)opts.get("--config"));
        }
        else
        {
            core.loadConfig("config.ini");
        }

        if((Boolean)opts.get("server")){
            System.out.println("starting server");
            core.startServer((String)opts.get("<ip>"), Integer.parseInt((String)opts.get("--port")));
            //TODO:Instead readline() use Thread.join for wait and sigint (ctrl+c) for ending
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if((Boolean)opts.get("process")){
            core.processFile((String)opts.get("<inputfile>"), (String)opts.get("<outputfile>"), (String)opts.get("<processor>"));
        }else if((Boolean)opts.get("list")){
            core.listProcessors();
        }


    }

}
