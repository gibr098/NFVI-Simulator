package RequesterDispatcher;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Classes.COTServer;
import Classes.NFVIPoP;
import Classes.Service;
import Classes.VNF;
import Classes.VirtualMachine;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkProvide;
import Functions.Allocation;
import Functions.CostChart;
import Functions.Deallocation;
import Functions.ServiceGeneration;
import Functions.WriteData;
import jxl.write.WritableSheet;

import java.io.*;

public class Dispatcher implements Callable<Object> {
    double endTime;
    double clock;
    NFVIPoP pop;
    LinkedList<Service> queue;
    int served;
    PrintWriter out;
    WritableSheet sheet;
    String fileName;
    XYSeriesCollection dataset;
    String ss_policy;
    String q_policy;
    String s_isolation;

    public Dispatcher(double endTime, String ss_policy, String q_policy, String s_isolation, NFVIPoP pop, PrintWriter out, WritableSheet sheet, String fileName) {
        this.endTime = endTime;
        this.clock = 0;
        this.served = 0;
        this.pop = pop;
        this.out = out;
        this.fileName = fileName;
        this.sheet = sheet;
        this.dataset = pop.getDataset();
        this.ss_policy = ss_policy;
        this.q_policy = q_policy;
        this.s_isolation = s_isolation;

    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public void run() throws Exception {
        for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
            COTServer s = lc.getCOTServer();
            dataset.addSeries(s.getSeries());
        }
        // int cell = sheet.getRows()+1;
        // System.out.println("PROSSIMA RIGA DOVE SCRIVERE: "+ sheet.getRows());
        int cell = sheet.getRows();
        String id = (sheet.getRows() == 1)? "1" : String.valueOf(Integer.parseInt(sheet.getCell(0,sheet.getRows()-1).getContents())+1);
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(1000);
            clock += 1;
            queue = pop.getQueue();

            System.out.println("QUEUE: "+ pop.getQueuePrint());
            /* //---------Check code--------------//
            System.out.println("QUEUE: "+ pop.getQueuePrint());
            for(int i=0; i < queue.size(); i++){
                System.out.println(queue.get(i).getName()+" duration: "+ queue.get(i).getDuration()+" chain: "+ queue.get(i).getVNFNumber()+" vnfs");
            }
            //---------Check code---------------// */


            out.println("\n" + "t" + clock + ": Servers' state\n" + pop.getServerState());
            for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
                COTServer s = lc.getCOTServer();
                s.addToSeries(clock, s.getCpu());
            }
            if (!queue.isEmpty()) {
                Service s;// = new Service("null", 0, 0);
                switch(q_policy){
                    case "FIFO":
                    s = queue.getFirst();
                    break;

                    case "LIFO":
                    s = queue.getLast();
                    break;

                    case "SDF":
                    Service smin = queue.getFirst();
                    for(int i=0; i < queue.size(); i++){
                        if ( queue.get(i).getDuration() < smin.getDuration() ){
                            smin = queue.get(i);
                        }
                        //System.out.println("ciclo: "+i+"smin = "+smin.getName()+" " +smin.getDuration());
                    }
                    s = smin;
                    break;

                    case "LDF":
                    Service smax = queue.getFirst();
                    for(int i=0; i < queue.size(); i++){
                        if ( queue.get(i).getDuration() > smax.getDuration() ){
                            smax = queue.get(i);
                        }
                        //System.out.println("ciclo: "+i+"smax = "+smax.getName()+" " +smax.getDuration());
                    }
                    s = smax;
                    break;

                    case "SCF":
                    Service scmin = queue.getFirst();
                    for(int i=0; i < queue.size(); i++){
                        if ( queue.get(i).getVNFNumber() < scmin.getVNFNumber() ){
                            scmin = queue.get(i);
                        }
                        //System.out.println("ciclo: "+i+"scmin = "+scmin.getName()+" " +scmin.getVNFNumber());
                    }
                    s = scmin;
                    break;

                    case "LCF":
                    Service scmax = queue.getFirst();
                    for(int i=0; i < queue.size(); i++){
                        if ( queue.get(i).getVNFNumber() > scmax.getVNFNumber() ){
                            scmax = queue.get(i);
                        }
                        //System.out.println("ciclo: "+i+"scmax = "+scmax.getName()+" " +scmax.getVNFNumber());
                    }
                    s = scmax;
                    break;

                    case "RANDOM":
                    int r_index = Functions.ServiceGeneration.getRandomNumber(0, queue.size());
                    s = queue.get(r_index);
                    break;

                    default:
                    s = queue.getFirst();
                    System.out.println("Policy " + q_policy + " not supported");
                    System.exit(0);
                }
                if (Allocation.NewServiceCanBeAllocated(s, pop)) {
                    for (int i = 0; i < s.getDemand() - 1; i++) {
                        queue.add(Functions.ServiceGeneration.generateCopyService(s, i + 1));
                    }
                    //s = queue.remove();
                    queue.remove(s);
                    /*
                    boolean allocationResult = false;
                    switch (a_policy){
                        case "FAS":
                        allocationResult = Allocation.AllocateService(s, pop);
                        break;

                        case "RANDOM":
                        allocationResult = Allocation.AllocateService(s, pop);
                        break;

                        default:
                        System.out.println("Policy " + a_policy + " not supported");
                        System.exit(0);
                    }*/
                    
                    if (Allocation.NewAllocateService(s, pop, ss_policy, s_isolation)) {
                        // s = queue.remove();
                        VirtualMachine vm = s.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer()
                                .getLinkInstance().getVirtualMachine();
                        COTServer sv = vm.getLinkVM().getCOTServer();
                        out.println("t" + clock + ": " + s.getName() + "[" + s.getChain() + "]" + " Allocated on "
                                + sv.getName() + "[" + vm.getName() + "]");
                        System.out.println(
                                "t" + clock + "\tDispatcher: " + s.getName() + " Allocated at: " + "t" + clock);
                        System.out.print("NFVI PROVIDE: " + pop.getLinkCompose().getNFVI().getServicesRunning() + "\n");
                        served++;
                        s.setInitialAllocationTime(clock);
                        s.setAllocated(true);
                    } else {
                        System.out.println("ERROR IN ALLOCATING " + s.getName() + " at: " + "t" + clock);
                    }
                }
                // if there is a service running in pop (and queue is not empty)
                if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getDuration();
                        if (clock == service_init_time + service_duration) {
                            if (s.getDemand() == 1 && !s.getName().contains("[")) {
                                writeDataset(sheet, cell, s, pop, clock, id, fileName);
                                cell++;
                                System.out.println(s.getName() + "SCRITTO IN DS");
                            }
                            // System.out.println(s.getName()+"----------------"+"["+String.valueOf(s.getReqDemand()-1)+"]");
                            if (s.getName().contains("[" + String.valueOf(s.getReqDemand() - 1) + "]")) {
                                writeDataset(sheet, cell, s, pop, clock, id, fileName);
                                cell++;
                                System.out.println(s.getName() + "SCRITTO IN DS");
                            }
                            // writeDataset(sheet,cell,s,pop);
                            // cell++;
                            Deallocation.DeallocateService(s, pop);
                            System.out.println(
                                    "t" + clock + "\tDispatcher: " + s.getName() + " Deallocated at: " + "t" + clock);
                            out.println("t" + clock + " " + s.getName() + " Deallocated");
                        } else {
                            System.out.println(
                                    "t" + clock + "\tDispatcher: " + s.getName() + " running at " + "t" + clock);
                            out.println("t" + clock + ": " + s.getName() + " running");
                        }
                    }
                } else {
                    System.out.println("t" + clock + " Dispatcher: nothig");
                    out.println("t" + clock + ": waiting for requests");
                }
            } else {
                // if there is a service running in pop (and queue is empty)
                if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        Service s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getDuration();
                        if (clock == service_init_time + service_duration) {
                            if (s.getDemand() == 1 && !s.getName().contains("[")) {
                                writeDataset(sheet, cell, s, pop, clock, id, fileName);
                                cell++;
                                System.out.println(s.getName() + "SCRITTO IN DS");
                            }
                            // System.out.println(s.getName()+"----------------"+"["+String.valueOf(s.getReqDemand()-1)+"]");
                            if (s.getName().contains("[" + String.valueOf(s.getReqDemand() - 1) + "]")) {
                                writeDataset(sheet, cell, s, pop, clock, id, fileName);
                                cell++;
                                System.out.println(s.getName() + "SCRITTO IN DS");
                            }
                            // writeDataset(sheet,cell,s,pop);
                            // cell++
                            Deallocation.DeallocateService(s, pop);
                            System.out.println(
                                    "t" + clock + "\tDispatcher: " + s.getName() + " Deallocated at: " + "t" + clock);
                            out.println("t" + clock + ": " + s.getName() + " Deallocated");
                        } else {
                            System.out.println(
                                    "t" + clock + "\tDispatcher: " + s.getName() + " running at " + "t" + clock);
                            out.println("t" + clock + ": " + s.getName() + " running");
                        }
                    }
                } else {
                    System.out.println("t" + clock + " Dispatcher: nothing");
                    out.println("t" + clock + ": waiting for requests");
                }
            }
            
        }
        System.out.println("Dispatcher: Total services allocated: " + served);
        out.println("\nTOTAL SERVICES ALLOCATED: " + served);
    }

    public static void writeDataset(WritableSheet sheet, int cell, Service service, NFVIPoP pop, double clock, String id, String fileName)
            throws Exception {
        Properties prop = new Properties();
        //String fileName = "Simulator\\src\\NFVI.config";
        //String fileName = "Simulator\\Config files\\POP-1.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            // FileNotFoundException catch is optional and can be collapsed
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();

        }

        String servicename = service.getName();
        if (servicename.contains("[")) {
            int startIndex = servicename.indexOf("[");
            int endIndex = servicename.indexOf("]");
            String replacement = "";
            String toBeReplaced = servicename.substring(startIndex, endIndex+1);
            //servicename = servicename.replace("[", replacement);
            //servicename = servicename.replace("]", replacement);
            servicename = servicename.replace(toBeReplaced, replacement);
            //servicename.replace("S", "replacement");
        }

        double cpu_cost = Double.parseDouble(prop.getProperty("cpu_cost"));
        double ram_cost = Double.parseDouble(prop.getProperty("ram_cost"));
        double storage_cost = Double.parseDouble(prop.getProperty("storage_cost"));
        double energy_cost = Double.parseDouble(prop.getProperty("energy_cost"));
        double renewable_energy = Double.parseDouble(prop.getProperty("renewable_energy"));

        double lambda = Double.parseDouble(prop.getProperty("lambda"));
        COTServer s = pop.getLinkOwn().getDataCenter().getLinkContain().iterator().next().getCOTServer();
        VirtualMachine vm = s.getLinkVM().iterator().next().getVirtualMachine();
        // int vmnum = (service.getLinkChainList().size()>4)? 2 : 1;
        int vmnum = (service.getLinkChainList().size() == vm.getContainerNumber()) ? 1
                : (service.getLinkChainList().size() / vm.getContainerNumber()) + 1;
        //String vmtype = (service.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer()
          //      .getLinkInstance().getVirtualMachine().getContainerNumber() > 3) ? "Medium" : "Small";
        String vmtype = service.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer()
              .getLinkInstance().getVirtualMachine().getType();
        int vnfnum = service.getLinkChainList().size();
        String vnftype = service.getStringChain();
        double servicecost = 0;
        for (LinkChain lc : service.getLinkChainList()) {
            VNF vnf = lc.getVNF();
            //servicecost += vnf.getCPU() * 0.003; // 0.003 euro per core hour
            //servicecost += vnf.getRam() * 0.013; // 0.013 euro per RAM(GB) hour
            //servicecost += vnf.getStorage() * 0.00001; // 0.00001 euro per GB (storage) hour
            servicecost += vnf.getCPU() * cpu_cost; // 0.003 euro per core hour
            servicecost += vnf.getRam() * ram_cost; // 0.013 euro per RAM(GB) hour
            servicecost += vnf.getStorage() * storage_cost; // 0.00001 euro per GB (storage) hour
        }
        servicecost = servicecost * service.getDuration() * service.getReqDemand();

        // base energy consumption
        // double serverONcost= 0.300 * duration * 0.45; //0.300 kW * totale ore * 0.45
        // euro/KWatt
        double serverONcost = 0.300 * clock * energy_cost; // 0.300 kW * totale ore * 0.08 euro/kWh
        double ramUsagecost = 0;
        double cpuUsagecost = 0;
        /*for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
            COTServer server = lc.getCOTServer();
            for (int cores : server.getCpuUsage().keySet()) {
                // 200 Watt at 100% -> 0.2 kWh at 100%
                // 0.08 euro/kWh
                double cputime = server.getCpuUsage().get(cores);
                double percent = (cores / server_cpu == 0) ? 0.01 : cores / server_cpu;
                cpuUsagecost += percent * 0.2 * cputime * 0.08;
            }
            for (int ram : server.getRamUsage().keySet()) {
                // 3 Watt every 8 GB -> 0.4 Watt each GB -> 0.0004 kWh GB
                // 0.08 euro/kWh
                double ramtime = server.getRamUsage().get(ram);
                ramUsagecost += ram * 0.0004 * ramtime * 0.08;
            }
        }*/
        for(LinkChain lc : service.getLinkChainList()){
            VNF vnf = lc.getVNF();
            ramUsagecost += vnf.getRam();
            cpuUsagecost += vnf.getCPU();
        }
        ramUsagecost = ramUsagecost * 0.0004 * service.getDuration() * 0.08;
        cpuUsagecost = cpuUsagecost * 0.2 * service.getDuration() * 0.08;
        double serverUsageCost = ramUsagecost + cpuUsagecost;
        double renewableEnergy = renewable_energy;
        double serviceEnergycost = (serverONcost + serverUsageCost - renewableEnergy)* service.getReqDemand();

        WriteData.insertStringCell(sheet, cell, 0, id); //simulation id
        WriteData.insertStringCell(sheet, cell, 1, "t-" + (int) clock); // timestamp
        WriteData.insertStringCell(sheet, cell, 2, servicename+id); // service name
        WriteData.insertIntCell(sheet, cell, 3, vmnum); // number of VMs used
        WriteData.insertStringCell(sheet, cell, 4, vmtype); // type of VMs
        WriteData.insertIntCell(sheet, cell, 5, vnfnum); // number of vnf
        WriteData.insertStringCell(sheet, cell, 6, vnftype); // chain of vnf
        WriteData.insertStringCell(sheet, cell, 7, "-"); // allocation policy
        WriteData.insertDoubleCell(sheet, cell, 8, lambda); // rate of requests arrivals
        WriteData.insertIntCell(sheet, cell, 9, service.getReqDemand()); // size of request
        WriteData.insertDoubleCell(sheet, cell, 10, service.getDuration()); // service duration
        WriteData.insertDoubleCell(sheet, cell, 11, servicecost); // service cost
        WriteData.insertDoubleCell(sheet, cell, 12, serviceEnergycost); // service energy cost

    }

}