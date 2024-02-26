package RequesterDispatcher;

//Controller class used to simulate the behavior of an M/M/1 system.

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import Classes.NFVIPoP;
import Classes.Service;
import Classes.Links.LinkProvide;
import Functions.Allocation;
import Functions.Deallocation;

import java.io.*;

public class Dispatcher implements Callable<Object> {
    double endTime;
    double clock;
    NFVIPoP pop;
    LinkedList<Service> queue;
    int served;

    public Dispatcher(double endTime, NFVIPoP pop) {
        this.endTime = endTime;
        this.clock = 0;
        this.served = 0;
        this.pop = pop;
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public void run() throws Exception {
        // Service s = new Service("Service0", 3, 0); // used only to initialize s
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(1);
            clock += 1;
            queue = pop.getQueue();
            if (!queue.isEmpty()) {
                Service s = queue.getFirst();
                if (Allocation.ServiceCanBeAllocated(s, pop)) {
                    s = queue.remove();
                    if (!s.isAllocated()) {
                        if (Allocation.AllocateService(s, pop)) {
                            System.out.println(clock + "s" + "\tD: " + s.getName() + " Allocated at: " + clock + "s");
                            System.out.print("NFVI PROVIDE: "+ pop.getLinkCompose().getNFVI().getServicesRunning());
                            served++;
                            s.setInitialAllocationTime(clock);
                            s.setAllocated(true);
                        } else {
                            System.out.println("ERROR IN ALLOCATING " + s.getName() + " at: " + clock + "s");
                        }
                    } else {
                        // service is running
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getTime();
                        System.out.println(
                                "service_init_time: " + service_init_time + ", service_duration: " + service_duration);
                        if (clock == service_init_time + service_duration) {
                            System.out.println(clock + "s" + "\tD: " + s.getName() + " Deallocated at: " + clock + "s");
                            Deallocation.DeallocateService(s, pop);
                        } else {
                            System.out.println(clock + "s" + "\tD: " + s.getName() + " running at " + clock);
                        }
                    }
                }
            } else {
                // if there is a service running in pop
                if (pop.getLinkCompose().getNFVI().getLinkProvide().size() != 0) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        Service s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getTime();
                        System.out.println(
                                "service_init_time: " + service_init_time + ", service_duration: " + service_duration);
                        if (clock == service_init_time + service_duration) {
                            System.out.println(clock + "s" + "\tD: " + s.getName() + " Deallocated at: " + clock + "s");
                            Deallocation.DeallocateService(s, pop);
                            
                        } else {
                            System.out.println(clock + "s" + "\tD: " + s.getName() + " running at " + clock);
                        }
                    }
                }else{
                    System.out.println(clock + "s" + " D: nothing");
                }
                System.out.println(clock + "s" + " D: nothing");
            }

        }
        System.out.println("D: Total requests served: " + served);
    }

}