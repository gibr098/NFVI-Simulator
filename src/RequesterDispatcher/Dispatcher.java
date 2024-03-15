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
                                writeDataset(sheet,cell,s,pop);
                                cell++;
                                System.out.println(s.getName()+"SCRITTO IN DS");
                            }
                            //System.out.println(s.getName()+"----------------"+"["+String.valueOf(s.getReqDemand()-1)+"]");
                            if(s.getName().contains("["+String.valueOf(s.getReqDemand()-1)+"]")){
                                writeDataset(sheet,cell,s,pop);
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
                                writeDataset(sheet,cell,s,pop);
                                cell++;
                                System.out.println(s.getName()+"SCRITTO IN DS");
                            }
                            //System.out.println(s.getName()+"----------------"+"["+String.valueOf(s.getReqDemand()-1)+"]");
                            if(s.getName().contains("["+String.valueOf(s.getReqDemand()-1)+"]")){
                                writeDataset(sheet,cell,s,pop);
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

    public static void writeDataset(WritableSheet sheet, int cell, Service service, NFVIPoP pop) throws Exception{
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
            servicecost+= vnf.getCPU(); 
            servicecost+= vnf.getRam();
            servicecost+= vnf.getStorage();
        }
        servicecost = servicecost * service.getTime() * service.getReqDemand();
        
        // base energy consumption
        double energycost = number_of_servers*server_ram*server_cpu*server_storage*duration;

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
        WriteData.insertStringCell(sheet,cell,13, "???" ); //total usage of resources
        WriteData.insertDoubleCell(sheet,cell,14, service.getTime()); //service duration
        WriteData.insertDoubleCell(sheet,cell,15, servicecost ); //service cost
        WriteData.insertIntCell(sheet,cell,16, 0); //energy cost
        WriteData.insertStringCell(sheet,cell,17, "no" ); //renewable energy

    }

}