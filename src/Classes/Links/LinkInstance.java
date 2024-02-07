package Classes.Links;

import Classes.*;

public class LinkInstance {
    private final COTServer COTServer;
    private final Container Container;

    public LinkInstance(COTServer x, Container y) throws Exception{
        if (x == null || y == null){
            throw new Exception
                ("Gli oggetti devono essere inizializzati");
            }
            COTServer = x;
            Container = y;
    }


    public COTServer getCOTServer(){
        return COTServer;
    }

    public Container getContainer(){
        return Container;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkInstance b = (LinkInstance)o;
            return b.COTServer == COTServer && b.Container == Container;
        }else return false;
    }

    public int hashCode() {
        return COTServer.hashCode() + Container.hashCode();
    }
}
