package Classes.Links;

import Classes.*;

public class LinkProvide {
    private NFVI NFVI;
    private Service service;

    public LinkProvide(NFVI x, Service y) throws Exception{
        if(x==null || y==null) throw new Exception("initialize objects");
        NFVI = x;
        service = y;
    }

    public NFVI getNFVI(){
        return NFVI;
    }

    public Service getService(){
        return service;
    }
    
    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkProvide b = (LinkProvide)o;
            return b.NFVI == NFVI && b.service == service;
        }else return false;
    }

    public int hashCode() {
        return NFVI.hashCode() + service.hashCode();
    }
}
