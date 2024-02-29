package RequesterDispatcher;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import Classes.NFVIPoP;
import Classes.Service;
import Classes.Links.LinkContain;
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

    public void run() throws Exception{
        while(clock != endTime){
            TimeUnit.MILLISECONDS.sleep(500);
            clock += 1;
            queue = pop.getQueue();
            if (!queue.isEmpty()){
                Service s = queue.getFirst();
                if (Allocation.ServiceCanBeAllocated(s, pop)) {
                    s = queue.remove();
                    if (Allocation.AllocateService(s, pop)) {
                        System.out.println(clock + "s" + "\tDispatcher: " + s.getName() + " Allocated at: " + clock + "s");
                        System.out.print("NFVI PROVIDE: " + pop.getLinkCompose().getNFVI().getServicesRunning() +  "\n");
                        served++;
                        s.setInitialAllocationTime(clock);
                        s.setAllocated(true);
                    } else {
                        System.out.println("ERROR IN ALLOCATING " + s.getName() + " at: " + clock + "s");
                    }
                }
                // if there is a service running in pop (and queue is not empty)
                if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getTime();
                        if (clock == service_init_time + service_duration) {
                            Deallocation.DeallocateService(s, pop);
                            System.out.println(clock + "s" + "\tDispatcher: " + s.getName() + " Deallocated at: " + clock + "s");
                        } else {
                            System.out.println(clock + "s" + "\tDispatcher: " + s.getName() + " running at " + clock);
                        }
                    }
                } else {
                    System.out.println(clock + "s" + " Dispatcher: nothing");
                }
            }else{
                // if there is a service running in pop (and queue is empty)
                if (!pop.getLinkCompose().getNFVI().getLinkProvide().isEmpty()) {
                    for (LinkProvide lp : pop.getLinkCompose().getNFVI().getLinkProvide()) {
                        Service s = lp.getService();
                        double service_init_time = s.getInitalAllocationTime();
                        double service_duration = s.getTime();
                        if (clock == service_init_time + service_duration) {
                            Deallocation.DeallocateService(s, pop);
                            System.out.println(clock + "s" + "\tDispatcher: " + s.getName() + " Deallocated at: "+clock);
                        } else {
                            System.out.println(clock + "s" + "\tDispatcher: " + s.getName() + " running at " + clock);
                        }
                    }
                } else {
                    System.out.println(clock + "s" + " Dispatcher: nothing");
                }
            }
        }
        System.out.println("Dispatcher: Total requests served: " + served);
    }

}