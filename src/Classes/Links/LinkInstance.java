package Classes.Links;

import Classes.*;

public class LinkInstance {
    private final VirtualMachine VirtualMachine;
    private final Container Container;

    public LinkInstance(VirtualMachine x, Container y) throws Exception{
        if (x == null || y == null){
            throw new Exception
                ("Gli oggetti devono essere inizializzati");
            }
            VirtualMachine = x;
            Container = y;
    }


    public VirtualMachine getVirtualMachine(){
        return VirtualMachine;
    }

    public Container getContainer(){
        return Container;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkInstance b = (LinkInstance)o;
            return b.VirtualMachine == VirtualMachine && b.Container == Container;
        }else return false;
    }

    public int hashCode() {
        return VirtualMachine.hashCode() + Container.hashCode();
    }
}
