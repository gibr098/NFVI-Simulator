package RequesterDispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.distribution.ZipfDistribution;

import Classes.*;
import Classes.Links.LinkChain;
import Classes.Links.LinkContain;
import Classes.Links.LinkProvide;
import Classes.Links.LinkVM;
import Functions.ServiceGeneration;
import Functions.WriteData;
import jxl.write.WritableSheet;

public class Monitor implements Callable<Object> {

    NFVIPoP pop;
    double endTime;
    double clock;

    WritableSheet sheet;

    HashMap<Integer, Double> cpuUsage;
    HashMap<Integer, Double> ramUsage;

    public Monitor(NFVIPoP pop, double endTime, WritableSheet sheet) {
        this.pop = pop;
        this.endTime = endTime;
        this.clock = 0;

        this.sheet = sheet;

        this.cpuUsage = new HashMap<>();
        this.ramUsage = new HashMap<>();
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public void run() throws InterruptedException, Exception {
        double ramtime = 0.0;
        double cputime = 0.0;
        double stortime = 0.0;
        double c = 0.0;
        int cell = sheet.getRows();
        String id = (sheet.getRows() == 1)? "1" : String.valueOf(Integer.parseInt(sheet.getCell(0,sheet.getRows()-1).getContents())+1);
        while (clock != endTime) {
            System.out.println("t" + clock + " Monitor");
            TimeUnit.MILLISECONDS.sleep(100);
            clock += 1;
            for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
                COTServer server = lc.getCOTServer();
                // Cpu usage
                if (server.getCpuUsage().containsKey(server.getCpu())) {
                    cputime = server.getCpuUsage().get(server.getCpu()) + 1;
                    server.insertCpuUsage(server.getCpu(), cputime);
                } else {
                    server.insertCpuUsage(server.getCpu(), 1.0);
                }

                // Ram Usage
                if (server.getRamUsage().containsKey(server.getRam())) {
                    ramtime = server.getRamUsage().get(server.getRam()) + 1;
                    server.insertRamUsage(server.getRam(), ramtime);
                } else {
                    server.insertRamUsage(server.getRam(), 1.0);
                }

                // Storage Usage
                if (server.getStorageUsage().containsKey(server.getStorage())) {
                    stortime = server.getStorageUsage().get(server.getStorage()) + 1;
                    server.insertStorageUsage(server.getStorage(), stortime);
                } else {
                    server.insertStorageUsage(server.getStorage(), 1.0);
                }
                // printCpuUsage(server);
                // printRamUsage(server);
            }

            if (clock % 5 == 0) {
                writeDataset(sheet, cell, pop, clock, id);
                cell++;
            }
        }

    }

    public void printCpuUsage(COTServer s) {
        System.out.println("CPU USAGE of " + s.getName());
        for (Object ob : s.getCpuUsage().keySet()) {
            String key = ob.toString();
            String value = s.getCpuUsage().get(ob).toString();
            System.out.println(key + " cores" + " for " + value + " h");
        }
        System.out.println("\n");
    }

    public void printRamUsage(COTServer s) {
        System.out.println("RAM USAGE of " + s.getName());
        for (Object ob : s.getRamUsage().keySet()) {
            String key = ob.toString();
            String value = s.getRamUsage().get(ob).toString();
            System.out.println(key + " GB" + " for " + value + " h");
        }
        System.out.println("\n");
    }

    public static void writeDataset(WritableSheet sheet, int cell, NFVIPoP pop, double clock, String id) throws Exception {
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

        double energy_cost = Double.parseDouble(prop.getProperty("energy_cost"));
        double renewable_energy = Double.parseDouble(prop.getProperty("renewable_energy"));

        COTServer s = pop.getLinkOwn().getDataCenter().getLinkContain().iterator().next().getCOTServer();
        // VirtualMachine vm = s.getLinkVM().iterator().next().getVirtualMachine();
        // int vmnum = (service.getLinkChainList().size()>4)? 2 : 1;

        String consumeOfram = "";
        String consumeOfcpu = "";
        String consumeOfstrorage = "";

        int vmnum = 0;
        String vmtype = "";
        HashMap<String, Integer> vmused = new HashMap<>();

        // base energy consumption
        // double serverONcost= 0.300 * duration * 0.45; //0.300 kWh * totale ore * 0.45
        // euro/KWatt
        //double serverONcost = 0.300 * clock * 0.08; // 0.300 kW * totale ore * 0.08 euro/kWh
        double serverONcost = 0.300 * clock * energy_cost;
        double ramUsagecost = 0;
        double cpuUsagecost = 0;
        for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
            COTServer server = lc.getCOTServer();
            consumeOfcpu += server.getName() + "( ";
            consumeOfram += server.getName() + "( ";
            consumeOfstrorage += server.getName() + "( ";
            for (int cores : server.getCpuUsage().keySet()) {
                // 200 Watt at 100% -> 0.2 kW at 100%
                // 0.08 euro/kWh
                double cputime = server.getCpuUsage().get(cores);
                double percent = (cores / server_cpu == 0) ? 0.01 : cores / server_cpu;
                //cpuUsagecost += percent * 0.2 * cputime * 0.08;
                cpuUsagecost += percent * 0.2 * cputime * energy_cost;

                String keyc = String.valueOf(server_cpu - cores);
                String valuec = String.valueOf(cputime);
                consumeOfcpu += keyc + " cores" + " for " + valuec + "h ";
            }
            consumeOfcpu += " )\n";
            for (int ram : server.getRamUsage().keySet()) {
                // 3 Watt every 8 GB -> 0.4 Watt each GB -> 0.0004 kW GB
                // 0.08 euro/kWh
                double ramtime = server.getRamUsage().get(ram);
                //ramUsagecost += ram * 0.0004 * ramtime * 0.08;
                ramUsagecost += ram * 0.0004 * ramtime * energy_cost;

                String keyr = String.valueOf(server_ram - ram);
                String valuer = String.valueOf(ramtime);
                consumeOfram += keyr + " GB" + " for " + valuer + "h ";
            }
            consumeOfram += " )\n";
            for (int stor : server.getStorageUsage().keySet()) {
                double stortime = server.getStorageUsage().get(stor);
                String keys = String.valueOf(server_storage - stor);
                String values = String.valueOf(stortime);
                consumeOfstrorage += keys + " GB" + " for " + values + "h ";
            }
            consumeOfstrorage += " )\n";


            // consumeOfram += server.getRamUsagePrint()+"\n";
            // consumeOfcpu += server.getCpuUsagePrint()+"\n";
            // consumeOfstrorage += server.getStorageUsagePrint()+"\n";

            for (LinkVM lvm : server.getLinkVM()) {
                VirtualMachine vm = lvm.getVirtualMachine();
                if (vm.isRunningService()) {
                    vmnum++;
                    if (vmused.keySet().contains(vm.getType())) {
                        int n = vmused.get(vm.getType()) + 1;
                        vmused.put(vm.getType(), n);
                    } else {
                        vmused.put(vm.getType(), 1);
                    }
                }
            }
            vmtype = "( ";
            for (String type : vmused.keySet()) {
                //String k = o.toString();
                String v = vmused.get(type).toString();
                vmtype += type + ": " + v + " ";
            }
            vmtype += " )";
        }
        double serverUsageCost = ramUsagecost + cpuUsagecost;
        double renewableEnergy = renewable_energy;
        double energycost = serverONcost * number_of_servers + serverUsageCost - renewableEnergy;

        int servicenum = pop.getLinkCompose().getNFVI().getServicesNumber();
        //String servicesrunning = pop.getLinkCompose().getNFVI().getServicesRunning();

        String servicesrunning = "(";
        if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
            for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                servicesrunning += lp.getService().getName()+id + " ";
            }
        }else{
            servicesrunning+="No services";
        }
        servicesrunning += " )";
    



        WriteData.insertStringCell(sheet, cell, 0, id); //simulation id
        WriteData.insertStringCell(sheet, cell, 1, "t-" + (int) clock); // timestamp
        WriteData.insertIntCell(sheet, cell, 2, number_of_servers); // number of servers
        WriteData.insertIntCell(sheet, cell, 3, server_ram); // ram
        WriteData.insertIntCell(sheet, cell, 4, server_cpu); // cpu
        WriteData.insertIntCell(sheet, cell, 5, server_storage); // storage
        WriteData.insertIntCell(sheet, cell, 6, server_network); // network
        WriteData.insertIntCell(sheet, cell, 7, vmnum); // VMs active
        WriteData.insertStringCell(sheet, cell, 8, vmtype); // VMs type
        WriteData.insertIntCell(sheet, cell, 9, servicenum); // number of services running
        WriteData.insertStringCell(sheet, cell, 10, servicesrunning); // service running
        WriteData.insertStringCell(sheet, cell, 11, "FIFO, fixed VM size"); // allocation policy
        WriteData.insertDoubleCell(sheet, cell, 12, lambda); // req rate
        WriteData.insertStringCell(sheet, cell, 13, consumeOfram); // consume of ram
        WriteData.insertStringCell(sheet, cell, 14, consumeOfcpu); // consume of cpu
        WriteData.insertStringCell(sheet, cell, 15, consumeOfstrorage); // consume of storage
        WriteData.insertDoubleCell(sheet, cell, 16, energycost); // energy cost
        WriteData.insertStringCell(sheet, cell, 17, "no"); // renewable

    }

}
