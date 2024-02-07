package Classes.Links;

import Classes.*;

public class LinkCompose {
    private final NFVI NFVI;
    private final NFVIPoP NFVIPoP;

    public LinkCompose(NFVI x, NFVIPoP y) throws Exception{
        if (x==null || y==null){
            throw new Exception("initialize objects");
        }
        NFVI = x;
        NFVIPoP = y;
    }

    public NFVI getNFVI(){
        return NFVI;
    }

    public NFVIPoP getNFVIPoP(){
        return NFVIPoP;
    }

    public boolean equals(Object o) {
        if (o != null && getClass().equals(o.getClass())) {
            LinkCompose b = (LinkCompose)o;
            return b.NFVI == NFVI && b.NFVIPoP == NFVIPoP;
        }else return false;
    }

    public int hashCode() {
        return NFVI.hashCode() + NFVIPoP.hashCode();
    }


}
