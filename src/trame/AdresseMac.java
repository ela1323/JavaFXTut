package trame;

import java.util.List;

public class AdresseMac {
    private String adr;

    public AdresseMac(List<String> ls) {
        assert ls.size() == 6;
        StringBuilder sb = new StringBuilder();
        for (String s1 : ls) {
            sb.append(s1);
            sb.append(":");
        }
        sb.delete(sb.length() - 1, sb.length());
        adr = new String(sb);
    }

    @Override
    public String toString() {
        return adr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AdresseMac other = (AdresseMac) obj;
        if (adr == null) {
            if (other.adr != null)
                return false;
        } else if (!adr.equals(other.adr))
            return false;
        return true;
    }


}
