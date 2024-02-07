package Classes.Links;

import Classes.*;

public class LinkContain {
    private final DataCenter DataCenter;
    private final COTServer COTServer;

    public LinkContain(DataCenter x, COTServer y) throws Exception{
        if (x == null || y == null){
            throw new Exception
                ("Gli oggetti devono essere inizializzati");
            }
            DataCenter = x;
            COTServer = y;
    }

    public DataCenter getDataCenter(){
        return DataCenter;
    }

    public COTServer getCOTServer(){
        return COTServer;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkContain b = (LinkContain)o;
            return b.DataCenter == DataCenter && b.COTServer == COTServer;
        }else return false;
    }

    public int hashCode() {
        return DataCenter.hashCode() + COTServer.hashCode();
    }
}
