package RequestServeSample;

import java.util.LinkedList;

public class Queue {
    LinkedList<service> queue;

    public Queue(){
        this.queue = new LinkedList<service>();
    }

    public void addElement(service s){
        queue.add(s);
    }

    public LinkedList<service> getQueue(){
        return queue;
    }

    public void printQ() {
        String info = "( ";
        for (service s : queue) {
            info += s.getName() + " ";
        }
        info += " )";
        System.out.println(info);
    }
}
