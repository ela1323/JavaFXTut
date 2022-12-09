package trame;

import java.util.Map;

public interface IPacket {
    public int getType();
    public String getComment();
    public IPAddress getIPSource();
    public IPAddress getIPDestination();
    public ISegment getISegment();
    public void setBrut(boolean brut);
    public void setOpt(boolean opt);
    public void setVV(boolean opt);
    public Map<Integer, String> getProtocolList();
}
