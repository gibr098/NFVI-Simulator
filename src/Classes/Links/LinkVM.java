package Classes.Links;

import Classes.*;

public class LinkVM {
    private final COTServer COTServer;
    private final VirtualMachine VirtualMachine;

    public LinkVM(COTServer x, VirtualMachine y) throws Exception{
        if (x == null || y == null){
            throw new Exception
                ("Gli oggetti devono essere inizializzati");
            }
            COTServer = x;
            VirtualMachine = y;
    }


    public COTServer getCOTServer(){
        return COTServer;
    }

    public VirtualMachine getVirtualMachine(){
        return VirtualMachine;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkVM b = (LinkVM)o;
            return b.COTServer == COTServer && b.VirtualMachine == VirtualMachine;
        }else return false;
    }

    public int hashCode() {
        return COTServer.hashCode() + VirtualMachine.hashCode();
    }
}
