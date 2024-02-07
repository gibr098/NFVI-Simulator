package Classes.Links;

import Classes.COTServer;
import Classes.DataCenter;
import Classes.NFVIPoP;

public class LinkOwn {
    private final NFVIPoP NFVIPoP;
    private final DataCenter DataCenter;

    public LinkOwn(NFVIPoP x, DataCenter y) throws Exception{
        if(x == null || y == null){
            throw new Exception("initialize objects");
        }
        NFVIPoP = x;
        DataCenter = y;
    }

    public NFVIPoP getNFVIPoP(){
        return NFVIPoP;
    }

    public DataCenter getDataCenter(){
        return DataCenter;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkOwn b = (LinkOwn)o;
            return b.NFVIPoP == NFVIPoP && b.DataCenter == DataCenter;
        }else return false;
    }

    public int hashCode() {
        return NFVIPoP.hashCode() + DataCenter.hashCode();
    }

}
