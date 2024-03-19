package RequesterDispatcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.distribution.ZipfDistribution;

import Classes.*;
import Classes.Links.LinkContain;
import Functions.ServiceGeneration;

public class Monitor implements Callable<Object> {

    NFVIPoP pop;
    double endTime;
    double clock;

    HashMap<Integer, Double> cpuUsage;
    HashMap<Integer, Double> ramUsage;

    public Monitor(NFVIPoP pop, double endTime) {
        this.pop = pop;
        this.endTime = endTime;
        this.clock = 0;

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
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(100);
            clock += 1;
            for (LinkContain lc : pop.getLinkOwn().getDataCenter().getLinkContain()) {
                COTServer server = lc.getCOTServer();
                // Cpu usage
                if (server.getCpuUsage().containsKey(server.getCpu())) {
                    cputime = server.getCpuUsage().get(server.getCpu()) + 1;
                    server.insertCpuUsage(server.getCpu(), cputime);
                } else {
                    server.insertCpuUsage(server.getCpu(), 0.0);
                }

                // Ram Usage
                if (server.getRamUsage().containsKey(server.getRam())) {
                    ramtime = server.getRamUsage().get(server.getRam()) + 1;
                    server.insertRamUsage(server.getRam(), ramtime);
                } else {
                    server.insertRamUsage(server.getRam(), 0.0);
                }
                //printCpuUsage(server);
                //printRamUsage(server);
            }

        }

    }

    public void printCpuUsage(COTServer s) {
        System.out.println("CPU USAGE of "+s.getName());
        for (Object ob : s.getCpuUsage().keySet()) {
            String key = ob.toString();
            String value = s.getCpuUsage().get(ob).toString();
            System.out.println(key+" cores" + " for " + value+" h");
        }
        System.out.println("\n");
    }

    public void printRamUsage(COTServer s) {
        System.out.println("RAM USAGE of "+s.getName());
        for (Object ob : s.getRamUsage().keySet()) {
            String key = ob.toString();
            String value = s.getRamUsage().get(ob).toString();
            System.out.println(key+" GB" + " for " + value+" h");
        }
        System.out.println("\n");
    }

}
