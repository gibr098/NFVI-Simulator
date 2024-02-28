import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkCompose;
import Classes.Links.LinkContain;
import Classes.Links.LinkInstance;
import Classes.Links.LinkOwn;
import Classes.Links.LinkProvide;
import Classes.Links.LinkRun;

import Functions.*;
import RequestServeSample.*;
import RequesterDispatcher.Dispatcher;
import RequesterDispatcher.Requester;

import java.io.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        Properties prop = new Properties();
        String fileName = "NFVI.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            // FileNotFoundException catch is optional and can be collapsed
        } catch (IOException ex) {

        }

        int number_of_servers = Integer.parseInt(prop.getProperty("number_of_servers"));
        int server_ram = Integer.parseInt(prop.getProperty("RAM(GB)"));
        int server_cpu = Integer.parseInt(prop.getProperty("CPU(Cores)"));
        int server_storage = Integer.parseInt(prop.getProperty("Storage(GB)"));
        int server_network = Integer.parseInt(prop.getProperty("Network(interfaces)"));

        int number_of_containers = Integer.parseInt(prop.getProperty("containers"));
        int container_cpu_usage = Integer.parseInt(prop.getProperty("container_cpu(%)"));
        int container_ram = Integer.parseInt(prop.getProperty("C-RAM(GB)"));
        int container_cpu = Integer.parseInt(prop.getProperty("C-CPU(Cores)"));
        int container_storage = Integer.parseInt(prop.getProperty("C-Storage(GB)"));
        int container_network = Integer.parseInt(prop.getProperty("C-Network(interfaces)"));

        double lambda = Double.parseDouble(prop.getProperty("lambda"));
        double duration = Double.parseDouble(prop.getProperty("time_of_simulation"));

        String policy = prop.getProperty("policy");


        NFVI nfvi = new NFVI("NFVI");
        NFVIPoP pop = new NFVIPoP("PoP-1");
        DataCenter dc = new DataCenter("DC-1");

        LinkCompose lc = new LinkCompose(nfvi, pop);
        LinkOwn lo = new LinkOwn(pop, dc);

        nfvi.insertLinkCompose(lc);
        pop.insertLinkOwn(lo);

        for(int i =0; i < number_of_servers; i++){
            COTServer si = new COTServer("Server-"+i,server_ram, server_cpu, server_storage, server_network);
            LinkContain lci = new LinkContain(dc, si);
            dc.insertLinkContain(lci);
            for(int j = 0; j < number_of_containers; j++){
                Container cij = new Container("Container-"+i +j, container_ram, container_cpu, container_storage, container_network, container_cpu_usage);
                LinkInstance lij = new LinkInstance(si, cij);
                si.insertLinkInstance(lij);
            }
        }

        //PRINT The structure of the NFVI
        printPoPStructure(nfvi);


        //Run the Simulation
        AppRS app = new AppRS(lambda, duration, pop);
        app.run();


        //PRINT The result of the Simulation ???
        printSimulationResults();

    }

    public static void printPoPStructure(NFVI n) throws Exception{
        NFVIPoP pop = n.getLinkCompose().iterator().next().getNFVIPoP();
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        String info = "Construction of "+n.getName()+" with:\n";
        info+="Point of Presence "+pop.getName()+" that has 1 Data Center "+ dc.getName()+"\n";
        info+=dc.getName()+" has "+dc.getLinkContain().size()+" servers: \n";
        for (LinkContain lc : dc.getLinkContain()){
            COTServer s = lc.getCOTServer();
            info+= " "+s.getTotalResourcesInfo()+" has:\n";
            for(LinkInstance li : s.getLinkInstance()){
                Container c = li.getContainer();
                info+="   "+c.getTotalResourcesInfo()+"\n";
            }
        }

        System.out.println(info);
    }

    public static void printSimulationResults(){

    }
}
