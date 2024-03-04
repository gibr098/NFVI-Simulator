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
import Classes.Links.LinkVM;
import Functions.*;
import RequesterDispatcher.Dispatcher;
import RequesterDispatcher.Requester;

import java.io.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        File dir = new File("logs");
        if (!dir.exists()) {
            dir.mkdir();
        }

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

        int virtual_machines = Integer.parseInt(prop.getProperty("virtual_machines"));

        int number_of_containers = Integer.parseInt(prop.getProperty("containers"));
        int container_cpu_usage = Integer.parseInt(prop.getProperty("container_cpu(%)"));
        int container_ram = Integer.parseInt(prop.getProperty("C-RAM(GB)"));
        int container_cpu = Integer.parseInt(prop.getProperty("C-CPU(Cores)"));
        int container_storage = Integer.parseInt(prop.getProperty("C-Storage(GB)"));
        int container_network = Integer.parseInt(prop.getProperty("C-Network(interfaces)"));

        double lambda = Double.parseDouble(prop.getProperty("lambda"));
        double duration = Double.parseDouble(prop.getProperty("time_of_simulation"));

        String policy = prop.getProperty("policy");

        // CONTROL Construction validity
        ControlConstructionValidity(number_of_servers, server_ram, server_cpu, server_storage, server_network, virtual_machines, number_of_containers, container_ram, container_cpu, container_storage, container_network);


        NFVI nfvi = new NFVI("NFVI");
        NFVIPoP pop = new NFVIPoP("PoP-1");
        DataCenter dc = new DataCenter("DC-1");

        LinkCompose lc = new LinkCompose(nfvi, pop);
        LinkOwn lo = new LinkOwn(pop, dc);

        nfvi.insertLinkCompose(lc);
        pop.insertLinkOwn(lo);

        for (int i = 0; i < number_of_servers; i++) {
            COTServer si = new COTServer("Server-" + i, server_ram, server_cpu, server_storage, server_network);
            LinkContain lci = new LinkContain(dc, si);
            dc.insertLinkContain(lci);
            for (int j = 0; j < virtual_machines; j++) {
                VirtualMachine vmij = new VirtualMachine("VM-" + i + j);
                LinkVM lvm = new LinkVM(si, vmij);
                si.insertLinkVM(lvm);
                for (int k = 0; k < number_of_containers; k++) {
                    Container cij = new Container("Container-" + i + j + k, container_ram, container_cpu,
                            container_storage,
                            container_network, container_cpu_usage);
                    LinkInstance lij = new LinkInstance(vmij, cij);
                    vmij.insertLinkInstance(lij);
                }
            }
        }

        // PRINT The structure of the NFVI
        printPoPStructure(nfvi);

        System.out.println("Press \"ENTER\" to run the simulation...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        // Run the Simulation
        AppRS app = new AppRS(lambda, duration, pop);
        // app.run();

        // Run the Simulation and write the log
        File file;
        int n = 1;
        if (dir.listFiles().length == 0) {
            file = new File("logs/sim1_log.txt");
        }else{
            n = Integer.parseInt(dir.listFiles()[dir.listFiles().length - 1].getName().replaceAll("[^0-9]", ""))
                    + 1;
            file = new File("logs/sim" + n + "_log.txt");
        }
        PrintWriter out;
        try {
            out = new PrintWriter(file);
            System.out.println("SIMULATION "+n+": lambda = " + lambda + ", Sim length = " + duration+"\n");
            out.println("SIMULATION "+n+": lambda = " + lambda + ", Sim length = " + duration + "\n");
            app.run(out);
            out.println("REQUESTS NOT SERVED: " + pop.getQueuePrint());
            System.out.println("Requests not served: " + pop.getQueuePrint()+"\n");
            System.out.println("Simulation complete.");
            System.out.println("Log: logs/sim"+n+"_log.txt");
            out.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        // TODO: PRINT The result of the Simulation ???
        //printSimulationResults();

    }

    public static void printPoPStructure(NFVI n) throws Exception {
        NFVIPoP pop = n.getLinkCompose().iterator().next().getNFVIPoP();
        DataCenter dc = pop.getLinkOwn().getDataCenter();
        String info = "Construction of " + n.getName() + " with:\n";
        info += "Point of Presence " + pop.getName() + " that has 1 Data Center " + dc.getName() + "\n";
        info += dc.getName() + " has " + dc.getLinkContain().size() + " servers: \n";
        for (LinkContain lc : dc.getLinkContain()) {
            COTServer s = lc.getCOTServer();
            info += " \n" + s.getTotalResourcesInfo() + " has:\n";
            for (LinkVM lvm : s.getLinkVM()) {
                VirtualMachine vm = lvm.getVirtualMachine();
                info += "  " + vm.getName() + " with \n";
                for (LinkInstance li : vm.getLinkInstance()) {
                    Container c = li.getContainer();
                    info += "   " + c.getTotalResourcesInfo() + "\n";
                }
            }
        }

        System.out.println(info);
    }

    public static void ControlConstructionValidity(int ns, int sram, int scpu, int sstor, int snet, int nvm, int nc, int cram, int ccpu, int cstor, int cnet){
        int tot_containers = nvm*nc;
        if(sram < tot_containers*cram || 
           scpu < tot_containers*ccpu ||
           sstor < tot_containers*cstor ||
           snet < tot_containers*cnet
           ){
            System.out.println("\nCONSTRUCTION PARAMETERS ARE NOT VALID!"+
            "\nPlease insert a valid configuration in the NFVI.config file\n");
            System.exit(0);
        }

    }

}
