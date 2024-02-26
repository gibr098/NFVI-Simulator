package RequestServeSample;

//Controller class used to simulate the behavior of an M/M/1 system.

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import Classes.Service;

import java.io.*;

public class Dispatcher1 implements Callable<Object>{
    double endTime;
    double clock;
    Queue queue;
    boolean busy;
    int served;

    public Dispatcher1(double endTime, Queue q) {
        this.endTime = endTime;
        this.clock = 0;
        this.queue = q;
        this.served = 0;

        this.busy = false;
    }

    @Override
    public Object call() throws Exception {
        run();
        return null;
    }

    public void run() throws InterruptedException {
        double time = 0;
        service s = new service("service0", 3);
        while (clock != endTime) {
            LinkedList<service> qq = queue.getQueue();
            TimeUnit.MILLISECONDS.sleep(1);
            clock += 1;

            if (busy == false) {
                if (!qq.isEmpty()) {
                    s = qq.remove();
                    System.out.println(+clock + "s"+ "\tD: " + s.getName() + " Allocated at: "+clock+"s");
                    served++;
                    time = clock;
                    busy = true;
                }else{
                    System.out.println(clock+"s"+" D: nothing");
                }
            }else {
                if (clock == time + 3) {
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