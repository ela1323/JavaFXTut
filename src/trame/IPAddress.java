package trame;

import java.util.Arrays;
import java.util.List;

public class IPAddress {
    private int[] adr;

    public IPAddress(List<String> address) {
        assert (address.size() == 4);
        int k = 0;
        adr = new int[4];
        for (String s : address) {
            adr[k] = Integer.parseInt(s, 16);
            assert (adr[k] >= 0 && adr[k] <= 255);
            k++;
        }
    }

    public IPAddress(String address) {
        // assert (address.size() == 4);
        String s[] = address.split("\\.");
        int k = 0;
        adr = new int[4];
        for (int i = 0; i < s.length; i++) {
            adr[k] = Integer.parseInt(s[i]);
            assert (adr[k] >= 0 && adr[k] <= 255);
            k++;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(adr[i]);
            sb.append(".");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IPAddress other = (IPAddress) obj;
        if (!Arrays.equals(adr, other.adr))
            return false;
        return true;
    }
    

}
