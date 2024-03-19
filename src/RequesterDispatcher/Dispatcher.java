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
    XYSeriesCollection dataset;

    public Dispatcher(double endTime, NFVIPoP pop, PrintWriter out, WritableSheet sheet) {
        this.endTime = endTime;
        this.clock = 0;
        this.served = 0;
        this.pop = pop;
        this.out = out;
        this.sheet = sheet;
        this.dataset = pop.getDataset();

        
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public void run() throws Exception{
        for(LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()){
            COTServer s = lc.getCOTServer();
            dataset.addSeries(s.getSeries());
        }
        //int cell = sheet.getRows()+1;
        //System.out.println("PROSSIMA RIGA DOVE SCRIVERE: "+sheet.getRows());
        int cell = sheet.getRows();
        int removeServiceCouter = 0;
        while(clock != endTime){
            TimeUnit.MILLISECONDS.sleep(100);
            clock += 1;
            queue = pop.getQueue();
            out.println("\n"+"t"+clock+": Servers' state\n"+pop.getServerState());
            for(LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()){
                COTServer s = lc.getCOTServer();
                s.addToSeries(clock, s.getCpu());
            }
            if (!queue.isEmpty()){
                Service s = queue.getFirst();
                if (Allocation.ServiceCanBeAllocated(s, pop)){
                    for(int i = 0; i< s.getDemand()-1; i++){
                    queue.add(Functions.ServiceGeneration.generateCopyService(s,i+1));
                    }
                    s = queue.remove();
                    if (Allocation.AllocateService(s, pop)) {
                        //s = queue.remove();
                        VirtualMachine vm = s.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer()
                        .getLinkInstance().getVirtualMachine();
                        COTServer sv = vm.getLinkVM().getCOTServer();
                        out.println("t"+clock+": "+s.getName()+"["+s.getChain()+"]" + " Allocated on "+sv.getName()+"["+vm.getName()+"]");
                        System.out.println("t"+clock + "\tDispatcher: " + s.getName() + " Allocated at: " + "t"+clock);
                        System.out.print("NFVI PROVIDE: " + pop.getLinkCompose().getNFVI().getServicesRunning() +  "\n");
                        served++;
                        s.setInitialAllocationTime(clock);
                        s.setAllocated(true);
                    } else {
                        System.out.println("ERROR IN ALLOCATING " + s.getName() + " at: " + "t"+clock);
                    }
                }
                // if there is a service running in pop (and queue is not empty)
                if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getTime();
                        if (clock == service_init_time + service_duration) {
                            if(s.getDemand()==1 && !s.getName().contains("[")){
                                writeDataset(sheet,cell,s,pop,clock);
                                cell++;
                                System.out.println(s.getName()+"SCRITTO IN DS");
                            }
                            //System.out.println(s.getName()+"----------------"+"["+String.valueOf(s.getReqDemand()-1)+"]");
                            if(s.getName().contains("["+String.valueOf(s.getReqDemand()-1)+"]")){
                                writeDataset(sheet,cell,s,pop,clock);
                                cell++;
                                System.out.println(s.getName()+"SCRITTO IN DS");
                            }
                            //writeDataset(sheet,cell,s,pop);
                            //cell++;
                            Deallocation.DeallocateService(s, pop);
                            System.out.println("t"+clock + "\tDispatcher: " + s.getName() + " Deallocated at: " + "t"+clock);
                            out.println("t"+clock+" "+s.getName() + " Deallocated");
                        } else {
                            System.out.println("t"+clock + "\tDispatcher: " + s.getName() + " running at " + "t"+clock);
                            out.println("t"+clock+": "+s.getName() + " running");
                        }
                    }
                } else {
                    System.out.println("t"+clock + " Dispatcher: nothig");
                    out.println("t"+clock+": waiting for requests");
                }
            }else{
                // if there is a service running in pop (and queue is empty)
                if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        Service s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getTime();
                        if (clock == service_init_time + service_duration) {
                            if(s.getDemand()==1 && !s.getName().contains("[")){
                                writeDataset(sheet,cell,s,pop, clock);
                                cell++;
                                System.out.println(s.getName()+"SCRITTO IN DS");
                            }
                            //System.out.println(s.getName()+"----------------"+"["+String.valueOf(s.getReqDemand()-1)+"]");
                            if(s.getName().contains("["+String.valueOf(s.getReqDemand()-1)+"]")){
                                writeDataset(sheet,cell,s,pop,clock);
                                cell++;
                                System.out.println(s.getName()+"SCRITTO IN DS");
                            }
                            //writeDataset(sheet,cell,s,pop);
                            //cell++
                            Deallocation.DeallocateService(s, pop);
                            System.out.println("t"+clock + "\tDispatcher: " + s.getName() + " Deallocated at: "+"t"+clock);
                            out.println("t"+clock+": "+s.getName() + " Deallocated");
                        } else {
                            System.out.println("t"+clock + "\tDispatcher: " + s.getName() + " running at " + "t"+clock);
                            out.println("t"+clock+": "+s.getName() + " running");
                        }
                    }
                } else {
                    System.out.println("t"+clock + " Dispatcher: nothing");
                    out.println("t"+clock+": waiting for requests");
                }
            }
        }
        System.out.println("Dispatcher: Total requests served: " + served);
        out.println("\nTOTAL REQUESTS SERVED: " + served);
    }

    public static void writeDataset(WritableSheet sheet, int cell, Service service, NFVIPoP pop, double clock) throws Exception{
        Properties prop = new Properties();
        String fileName = "Simulator\\src\\NFVI.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            // FileNotFoundException catch is optional and can be collapsed
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();

        }
        int number_of_servers = Integer.parseInt(prop.getProperty("number_of_servers"));
        int server_ram = Integer.parseInt(prop.getProperty("RAM(GB)"));
        int server_cpu = Integer.parseInt(prop.getProperty("CPU(Cores)"));
        int server_storage = Integer.parseInt(prop.getProperty("Storage(GB)"));
        int server_network = Integer.parseInt(prop.getProperty("Network(interfaces)"));
        double lambda = Double.parseDouble(prop.getProperty("lambda"));
        double duration = Double.parseDouble(prop.getProperty("time_of_simulation"));
        COTServer s = pop.getLinkOwn().getDataCenter().getLinkContain().iterator().next().getCOTServer();
        VirtualMachine vm = s.getLinkVM().iterator().next().getVirtualMachine();
        //int vmnum = (service.getLinkChainList().size()>4)? 2 : 1;
        int vmnum =(service.getLinkChainList().size()==vm.getContainerNumber())?1: (service.getLinkChainList().size()/vm.getContainerNumber())+1;
        String vmtype = (service.getLinkChainList().iterator().next().getVNF().getLinkRun().getContainer().getLinkInstance().getVirtualMachine().getContainerNumber() > 4)? "Large" : "Medium";
        int vnfnum = service.getLinkChainList().size();
        String vnftype = service.getStringChain();
        double servicecost = 0;
        for (LinkChain lc : service.getLinkChainList()) {
            VNF vnf = lc.getVNF();
            servicecost+= vnf.getCPU()*0.003; //0.003 euro per core hour
            servicecost+= vnf.getRam()*0.013;  // 0.013 euro per RAM(GB) hour
            servicecost+= vnf.getStorage()*0.00001; //0.00001 per GB (storage) per hour
        }
        servicecost = servicecost * service.getTime() * service.getReqDemand();
        
        // base energy consumption
        //double serverONcost= 0.300 * duration * 0.45;  //0.300 kWh * totale ore * 0.45 euro/KWatt
        double serverONcost= 0.300 * clock * 0.08;  //0.300 kWh * totale ore * 0.08 euro/kWh
        double ramUsagecost = 0;
        double cpuUsagecost = 0;
        for(LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()){
            COTServer server = lc.getCOTServer();
            for (int cores : server.getCpuUsage().keySet()) {
                // 200 Watt at 100% -> 0.2 kWh at 100%
                // 0.08 euro/kWh
                double cputime = server.getCpuUsage().get(cores);
                double percent = (cores/server_cpu == 0)? 0.01: cores/server_cpu;
                cpuUsagecost += percent * 0.2 * cputime * 0.08;
            }
            for (int ram: server.getRamUsage().keySet()) {
                // 3 Watt every 8 GB -> 0.4 Watt each GB -> 0.0004 kWh GB
                // 0.08 euro/kWh
                double ramtime = server.getRamUsage().get(ram);
                ramUsagecost += ram * 0.0004 * ramtime * 0.08;
            }
        }
        double serverUsageCost = ramUsagecost + cpuUsagecost;
        double renewableEnergy = 0;
        double energycost = serverONcost * number_of_servers + serverUsageCost - renewableEnergy;

        WriteData.insertIntCell(sheet,cell,0, number_of_servers);
        WriteData.insertIntCell(sheet,cell,1, server_ram);
        WriteData.insertIntCell(sheet,cell,2, server_cpu);
        WriteData.insertIntCell(sheet,cell,3, server_storage);
        WriteData.insertIntCell(sheet,cell,4, server_network);
        WriteData.insertIntCell(sheet,cell,5, vmnum);
        WriteData.insertStringCell(sheet,cell,6, vmtype);
        WriteData.insertIntCell(sheet,cell,7, vnfnum);
        WriteData.insertStringCell(sheet,cell,8, vnftype); //chain of vnf
        WriteData.insertIntCell(sheet,cell,9, pop.getLinkCompose().getNFVI().getServicesNumber()); //services running in the system at instant t
        WriteData.insertStringCell(sheet,cell,10, "FIFO, fixed VM size" ); //policy
        WriteData.insertDoubleCell(sheet,cell,11, lambda); //rate of requests arrivals
        WriteData.insertIntCell(sheet,cell,12, service.getReqDemand()); //size of request
        //WriteData.insertStringCell(sheet,cell,13, "???" ); //total usage of resources
        WriteData.insertDoubleCell(sheet,cell,13, service.getTime()); //service duration
        WriteData.insertDoubleCell(sheet,cell,14, servicecost ); //service cost
        WriteData.insertDoubleCell(sheet,cell,15, energycost); //energy cost
        WriteData.insertStringCell(sheet,cell,16, "no" ); //renewable energy

    }

}