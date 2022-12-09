package trame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trame implements ITrame{
    /* ATTRIBUTS */
    /* liste des octets bruts */
    private List<String> trameBrut;
    /* adresse MAC source */
    private AdresseMac macSource;
    /* adresse MAC destination */
    private AdresseMac macDest;
    /* le type du paquet encapsule */
    private String type;
    /* dans notre project, on reconnait que les paquets ipv4 */
    private IPacket paquet;

    /* les types reconnus */
    private Map<Integer, String> typesList;

    /* flag pour affichage (brut ou pas) */
    private boolean brut;
    private boolean opt;
    private boolean vv;

    /*
     * trame initialise a partir d'une liste de string, qui contient les octets
     * bruts
     */
    public Trame(List<String> liste) {

        // initialise les types reconnus
        typesList = new HashMap<>();
        initializeMapWithTypes();

        trameBrut = new ArrayList<>(liste); // garder la reference vers la liste
        macDest = new AdresseMac(trameBrut.subList(0, 6));
        macSource = new AdresseMac(trameBrut.subList(6, 12));

        // recuperer le type du paquet encapsule TODO: ajouter les interfaces
        int typeInt = Integer.parseInt(trameBrut.get(12) + trameBrut.get(13), 16);

        // initialiser le paquet (le type est bien dans typesList.get(typeInt))

        type = typesList.get(typeInt);
        if (typeInt == 2048) // si le type est ipv4 (0x0800)
        {
            paquet = new Packet(trameBrut.subList(14, trameBrut.size()));
            // paquet = null;
        } else if (typeInt == 2054) // si le type est arp (0x0806)
        {
            paquet = null;
        } else if (typeInt == 2054) // si le type est rarp (0x8035)
        {
            paquet = null;
        }
        /* affichage */
        brut = false;
        opt = false;
        vv = false;
        // this.opt = opt[0];
    }

    private void initializeMapWithTypes() {
        typesList.put(2048, "IPv4");
        typesList.put(2054, "ARP");
        typesList.put(32821, "RARP");
        typesList = Collections.unmodifiableMap(typesList);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        /* pretty print */
        if (!brut) {
            str.append("Trame [macDest=" + macDest + "], [macSource=" + macSource + "], type=" + type + "]\n");
            if (paquet != null)
                str.append(paquet.toString());
        }

        /* ugly print */
        else {
            for (String i : trameBrut) {
                str.append(i + " ");
            }
            str.delete(str.length() - 1, str.length());
        }
        return str.toString();
    }

    public String getComment() {
        StringBuilder sb = new StringBuilder();
        if (vv) {
            sb.append(toString());
        } else {
            if (paquet == null) {
                sb.append("Ethernet: ");
                sb.append("MAC Source=[" + macSource + "] MAC Destination=[" + macDest + "] type=[" + type + "]");
            } else
                sb.append(paquet.getComment());
        }
        return sb.toString();
    }

    /* GETTERS */
    public List<String> getTrameBrut() {
        return trameBrut;
    }

    public IPacket getIPacket() {
        return paquet;
    }

    public void setBrut(boolean brut) {
        this.brut = brut;
        if (paquet != null) {
            paquet.setBrut(brut);
        }
    }

    public void setOpt(boolean opt) {
        this.opt = opt;
        if (paquet != null) {
            paquet.setOpt(opt);
        }
    }

    public void setVV(boolean vv) {
        this.vv = vv;
        if (paquet != null) {
            paquet.setVV(opt);
        }
    }

    @Override
    public AdresseMac getMACSource() {
        return macSource;
    }

    @Override
    public AdresseMac getMACDestination() {
        return macDest;
    }

    @Override
    public int getType() {
        return 2048; //TODO: ethernet???
    }

    @Override
    public Map<Integer, String> getTypeList() {
        return typesList;
    }

    @Override
    public String getMesSrc() {
        StringBuilder sb1 = new StringBuilder();
        if(paquet != null) {
            if(paquet.getIPSource()!= null) {
                sb1.append(paquet.getIPSource());
            }
        }
        else {
            sb1.append(macSource);
        }
        return sb1.toString();
    }

    @Override
    public String getMesDst() {
        StringBuilder sb1 = new StringBuilder();
        if(paquet != null) {
            if(paquet.getIPDestination()!= null) {
                sb1.append(paquet.getIPDestination());
            }
        }
        else {
            sb1.append(macDest);
        }
        return sb1.toString();
    }

    @Override
    public String getPortSrc() {
        StringBuilder sb1 = new StringBuilder();
        if(paquet != null) {
            if(paquet.getISegment()!= null) {
                if(paquet.getISegment().getSourcePort() != 0)
                    sb1.append(paquet.getISegment().getSourcePort());
            }
        }
        return sb1.toString();
    }

    @Override
    public String getPortDst() {
        StringBuilder sb1 = new StringBuilder();
        if(paquet != null) {
            if(paquet.getISegment()!= null) {
                if(paquet.getISegment().getDestionationPort() != 0)
                    sb1.append(paquet.getISegment().getDestionationPort());
            }
        }
        return sb1.toString();
    }

}
