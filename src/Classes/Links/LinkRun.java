package Classes.Links;

import Classes.*;

public class LinkRun {
    private VNF VNF;
    private Container container;

    public LinkRun(VNF x, Container y) throws Exception{
        if(x==null || y==null){
            throw new Exception("initialize objects");
        }
        VNF = x;
        container = y;

    }

    public VNF getVNF(){
        return VNF;
    }

    public Container getContainer(){
        return container;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkRun b = (LinkRun)o;
            return b.VNF == VNF && b.container == container;
        }else return false;
    }

    public int hashCode() {
        return VNF.hashCode() + container.hashCode();
    }

}
