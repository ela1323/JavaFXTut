package trame;

import java.util.Map;

public interface ISegment {
    public int getProtocol();
    public String getComment();
    public int getSourcePort();
    public int getDestionationPort();
    public void setBrut(boolean brut);
    public void setOpt(boolean opt);
    public void setVV(boolean opt);
    public IMessage getIMessage();
    public Map<Integer,String> getApplicationList();
}
