package trame;

import java.util.Map;

public interface ITrame {
    public int getType();
    public String getComment();
    public AdresseMac getMACSource();
    public AdresseMac getMACDestination();
    public IPacket getIPacket();
    public void setBrut(boolean brut);
    public void setOpt(boolean opt);
    public void setVV(boolean opt);
    public Map<Integer, String> getTypeList();

    public String getMesSrc();
    public String getMesDst();

    public String getPortSrc();
    public String getPortDst();


}
