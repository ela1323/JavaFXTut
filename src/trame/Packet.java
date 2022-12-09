package trame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Packet implements IPacket {
    /* la liste des octets bruts */
    private List<String> packetBrut;
    private int v; // version
    private int ipHL; // ip header length
    private int tos; // type of service
    private int tlen; // total length
    private int identification;
    private int flagR; // Flag R
    private int flagDF; // Don't Fragment
    private int flagMF; // More Fragment
    private int fragmentoffset; // Fragment offset
    private int ttl; // time to live
    private int protocol; // protocol utilise (tcp), la cle du protocol dans le map
    private int checksum; // checksum
    private IPAddress ipSource; // adresse IP Source
    private IPAddress ipDest; // adresse IP Destination
    private ISegment segment; // le segment encapsule

    /* liste des options + leur longueurs */
    List<String> options = new ArrayList<>();
    List<Integer> longueurs = new ArrayList<>();

    /* flag pour options et affichage */
    private boolean brut;
    private boolean opt;
    private boolean vv;

    /* liste des protocols reconnus */
    private Map<Integer, String> protocolList;
    /* liste des options reconnus */
    private Map<Integer, String> optionList;

    public Packet(List<String> pBr) {
        // initialise les protocols reconnus
        protocolList = new HashMap<>();
        initializeMapWithProtocols();

        // initialiser les options reconnues
        optionList = new HashMap<>();
        initializeMapWithOptions();

        // verification que la trame au moins une longueur de 20 octets (header minimal)
        assert pBr.size() >= 20;

        // on garde le packet brut
        packetBrut = new ArrayList<>(pBr);

        /* DECODAGE */
        // on prend la version (1 octet)
        v = pBr.get(0).charAt(0) - '0';

        // on prend la longueur du header (en decimal en nobre des octets) (sur 1 octet)
        ipHL = Character.getNumericValue(pBr.get(0).charAt(1)); // header length in octets (entre 0 et f)

        // type of service (2 octets)
        tos = Integer.parseInt(pBr.get(1), 16);

        // on prend la longueur totale (en octets) (sur 4 octets)
        tlen = Integer.parseInt(pBr.get(2) + pBr.get(3), 16);

        // verification longueur totale de la trame
        // assert pBr.size() == tlen;

        // identification (sur 4 octets)
        identification = Integer.parseInt(pBr.get(4) + pBr.get(5), 16);

        // flags (sur 3 bits)
        String flag = Integer.toBinaryString(Integer.parseInt(pBr.get(6) + pBr.get(7), 16));

        String[] tab = flag.split("");
        String[] ftab = new String[16];
        int fill = 16 - tab.length;
        for (int i = 0; i < 16; i++) {
            if (i < fill)
                ftab[i] = "0";
            else
                ftab[i] = tab[i - fill];
        }

        flagR = 0;
        flagDF = Integer.parseInt(ftab[1]);
        flagMF = Integer.parseInt(ftab[2]);

        // fragment offset(sur 13 bits)
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < 16; i++) {
            sb.append(ftab[i]);
        }
        String s = sb.toString();

        fragmentoffset = Integer.parseInt(s, 2);

        // time to live (sur 2 octets)
        ttl = Integer.parseInt(pBr.get(8), 16);

        // le protocol (sur 2 octets)
        protocol = Integer.parseInt(pBr.get(9), 16);

        // on prend le checksum
        checksum = Integer.parseInt(pBr.get(9) + pBr.get(10), 16);

        // on prend les adresses ip (sur 8 + 8 octets chacune)
        ipSource = new IPAddress(pBr.subList(12, 16));
        ipDest = new IPAddress(pBr.subList(16, 20));

        // peut etre une liste qui garde l'option + sa longueur

        if (ipHL > 5) {
            // on a des options
            int startOptions = 20;
            int finOptions = ipHL * 4;
            int combien;
            while (startOptions <= finOptions) {
                // System.out.print(startOptions + " ");

                int optInt = Integer.parseInt(pBr.get(startOptions), 16); // le type de l'option
                options.add(optionList.get(optInt)); // on l'ajoute a la liste

                if (optInt > 1)
                    combien = Integer.parseInt(pBr.get(startOptions + 1), 16); // sa longueur
                else if (optInt == 1) {
                    combien = 1;
                } else {
                    combien = finOptions - startOptions + 1;
                }
                // System.out.println("Option " + optionList.get(opt) + " de longueur " +
                // combien); // affichage des options
                longueurs.add(combien);
                startOptions += combien; // regarder l'option suivante
            }
            // derniere option (1 octet bourrage)
            // int opt = Integer.parseInt(pBr.get(startOptions), 16); // le type de l'option
            // System.out.println("Option " + optionList.get(opt) + " de longueur " + 1); //
            // affichage des options

        }

        /* INITIALISATION SEGMENT */
        if (protocol == 6) { // on a tcp
            segment = new Segment(pBr.subList(ipHL * 4, pBr.size()));
            // segment = null;
        }
        if (protocol == 1) {
            segment = null;
        }
        if (protocol == 17) {
            segment = null;
        }
        /* flag pour affichage */
        opt = false;
        brut = false;
        vv = false;

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if (!brut) {
            str.append("Packet v=" + v + ", ipHL=" + ipHL + ", tos=" + tos + ", totallength=" + tlen
                    + ", identification=" + identification + ", flagR=" + flagR + ", flagDF=" + flagDF + ", flagMF="
                    + flagMF + ", ttl=" + ttl + ", protocol=" + protocol + "(" + protocolList.get(protocol)
                    + "), checksum=" + checksum + ", ipSource="
                    + ipSource.toString() + ", ipDest=" + ipDest.toString() + "] ");

            str.append("Options:[ ");
            for (int i = 0; i < options.size(); i++) {
                str.append(options.get(i) + "(" + longueurs.get(i) + ") ");
            }
            str.append("] ");

            if (segment != null)
                str.append(segment.toString());
        } else {
            for (String i : packetBrut) {
                str.append(i + " ");
            }
            str.delete(str.length() - 1, str.length());
        }

        return str.toString();
    }

    @Override
    public String getComment() {
        StringBuilder sb = new StringBuilder();
        if (vv) {
            sb.append(toString());
        } else {
            if (segment == null) {
                sb.append("IPV4: ");
                sb.append("IP Source=[" + ipSource + "] IP Destination=[" + ipDest + "] ");
                if (flagDF == 1 || flagMF == 1 || flagR == 1) {
                    sb.append("Flags=[");
                    if (flagR == 1)
                        sb.append(" R ");
                    if (flagDF == 1)
                        sb.append(" DF ");
                    if (flagMF == 1)
                        sb.append(" MF ");
                    sb.append("] ");
                }
                sb.append("TTL=[" + ttl + "] ");
                sb.append("Protocol=[" + protocolList.get(protocol) + "] ");
                if (opt && options.size() != 0) {
                    sb.append("Options:[ ");
                    for (int i = 0; i < options.size(); i++) {
                        sb.append(options.get(i) + "(" + longueurs.get(i) + ") ");
                    }
                    sb.append("]");
                }
            } else {
                sb.append(segment.getComment());
            }
        }
        return sb.toString();
    }

    private void initializeMapWithOptions() {
        optionList.put(0, "EOOL");
        optionList.put(1, "NOP");
        optionList.put(7, "RR");
        optionList.put(68, "TS");
        optionList.put(131, "LSR");
        optionList.put(137, "SSR");

        optionList = Collections.unmodifiableMap(optionList);
    }

    private void initializeMapWithProtocols() {
        protocolList.put(1, "ICMP");
        protocolList.put(2, "IGMP");
        protocolList.put(6, "TCP");
        protocolList.put(8, "ECP");
        protocolList.put(9, "ICP");
        protocolList.put(17, "UDP");
        protocolList.put(36, "XTP");
        protocolList.put(46, "RSVP");

        protocolList = Collections.unmodifiableMap(protocolList);
    }

    @Override
    public int getType() {
        return 2048; // ipv4
    }

    @Override
    public ISegment getISegment() {
        return segment;
    }

    @Override
    public IPAddress getIPSource() {
        return ipSource;
    }

    @Override
    public IPAddress getIPDestination() {
        return ipDest;
    }

    @Override
    public void setBrut(boolean brut) {
        this.brut = brut;
        if (segment != null) {
            segment.setBrut(brut);
        }
    }

    @Override
    public void setOpt(boolean opt) {
        this.opt = opt;
        if (segment != null) {
            segment.setOpt(opt);
        }
    }

    public void setVV(boolean vv) {
        this.vv = vv;
        if (segment != null) {
            segment.setVV(opt);
        }
    }

    /* GETTERS */
    public List<String> getPacketBrut() {
        return packetBrut;
    }

    @Override
    public Map<Integer,String> getProtocolList() {
        return protocolList;
    }

}
