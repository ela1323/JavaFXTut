package trame;


public interface IMessage {
    public int getType();
    public String getComment();
    public void setBrut(boolean brut);
    public void setOpt(boolean opt);
    public void setVV(boolean opt);
}
