package RequesterDispatcher;

//Controller class used to simulate the behavior of an M/M/1 system.

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import Classes.NFVIPoP;
import Classes.Service;
import Functions.Allocation;
import Functions.Deallocation;

import java.io.*;

public class Dispatcher implements Callable<Object>{
    double endTime;
    double clock;
    NFVIPoP pop;
    LinkedList<Service> queue;
    boolean busy;
    int served;

    public Dispatcher(double endTime, NFVIPoP pop) {
        this.endTime = endTime;
        this.clock = 0;
        this.served = 0;
        this.pop = pop;

        this.busy = false;
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public void run() throws Exception {
        double time = 0;
        Service s = new Service("Service0", 3,0); //used only to initialize s
        while (clock != endTime) {
            TimeUnit.MILLISECONDS.sleep(1);
            clock += 1;
            queue = pop.getQueue();
            if (busy == false) {
                if (!queue.isEmpty()) {
                    s = queue.remove();
                    Allocation.AllocateService(s, pop);
                    System.out.println(+clock + "s"+ "\tD: " + s.getName() + " Allocated at: "+clock+"s");
                    served++;
                    time = clock;
                    busy = true;
                }else{
                    System.out.println(clock+"s"+" D: nothing");
                }
            }else {
                if (clock == time + 3) {
                    Deallocation.DeallocateService(s, pop);
                    System.out.println(clock + "s"+ "\tD: " + s.getName() + " Deallocated at: " + clock + "s");
                    busy = false;
                } else {
                    System.out.println(clock + "s"+ "\tD: "+ s.getName() + " running at " + clock);
                }
            }
        }
        System.out.println("D: Total requests served: " + served);
    }

}