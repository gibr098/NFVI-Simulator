package Classes.Links;

import Classes.*;

public class LinkChain {
    private Service service;
    private VNF VNF;

    public LinkChain(Service x, VNF y) throws Exception{
        if(x == null || y == null){
            throw new Exception("initialize objects");
        }
        service = x;
        VNF = y;
    }

    public Service getService(){
        return service;
    }

    public VNF getVNF(){
        return VNF;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkChain b = (LinkChain)o;
            return b.service == service && b.VNF == VNF;
        }else return false;
    }

    public int hashCode() {
        return service.hashCode() + VNF.hashCode();
    }
}
