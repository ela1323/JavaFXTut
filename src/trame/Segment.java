package trame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Segment implements ISegment {
    private List<String> segmentBrut; // la liste des octets bruts
    private int sourcePort; // port Source
    private int destinationPort; // port Destination
    private long sequenceNumber; // numero de sequence
    private long ackNumber; // numero de aquitement
    private int THL; // taille header length (+ options)
    private int reserved; // reserve
    private int URG; // flag d'urgence
    private int ACK; // flag d'aquitement
    private int PSH; // flag push
    private int RST; // flag reset
    private int SYN; // flag synchronise
    private int FIN; // flag fin

    private int window; // la fenetre
    private int checksum; // somme de controle
    private int urgentPointer; // pointeur d'urgence

    /* le message encapsule */
    private IMessage message;

    /* liste des applications reconnus */
    private Map<Integer, String> applicationList;
    /* liste pour les options reconnues */
    private Map<Integer, String> optionList;
    /* liste des options + leur longueurs */
    List<String> options = new ArrayList<>();
    List<Integer> longueurs = new ArrayList<>();

    /* flag pour affichage */
    private boolean brut;
    private boolean opt;
    private boolean vv;

    public Segment(List<String> sBr) {
        assert (sBr.size() >= 20); // si la longueur du segment n'est pas valid assert error

        applicationList = new HashMap<>();
        initializeMapWithApplications();
        // initialiser la map avec les options reconnus
        optionList = new HashMap<>();
        initializeMapWithOptions();

        /* DECODAGE SEGMENT */
        // on garde la liste des octets bruts
        segmentBrut = sBr;

        // port source (4 octets)
        sourcePort = Integer.parseInt(sBr.get(0) + sBr.get(1), 16);

        // port destination (4 octets)
        destinationPort = Integer.parseInt(sBr.get(2) + sBr.get(3), 16);

        // Sequence number (8 octets)
        sequenceNumber = Long.parseLong(sBr.get(4) + sBr.get(5) + sBr.get(6) + sBr.get(7), 16);

        // Acknowledgment number (8 octets)
        ackNumber = Long.parseLong(sBr.get(8) + sBr.get(9) + sBr.get(10) + sBr.get(11), 16);

        // On prend toutes les informations de THL à FIN (très utile pour juste après)
        // (sur 2 octets)
        String story = Integer.toBinaryString(Integer.parseInt(sBr.get(12) + sBr.get(13), 16));

        String[] tab = story.split("");
        String[] stab = new String[16];
        int fill = 16 - tab.length;
        for (int i = 0; i < 16; i++) {
            if (i < fill)
                stab[i] = "0";
            else
                stab[i] = tab[i - fill];
        }

        // THL (4 bits)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(stab[i]);
        }
        String s = sb.toString();

        THL = Integer.parseInt(s, 2);

        // Reserved (6 bits)
        sb = new StringBuilder();
        for (int i = 4; i < 10; i++) {
            sb.append(stab[i]);
        }
        s = sb.toString();

        reserved = Integer.parseInt(s, 2);

        // URG
        URG = Integer.parseInt(stab[10]);

        // ACK
        ACK = Integer.parseInt(stab[11]);

        // PSH
        PSH = Integer.parseInt(stab[12]);

        // RST
        RST = Integer.parseInt(stab[13]);

        // SYN
        SYN = Integer.parseInt(stab[14]);

        // FIN
        FIN = Integer.parseInt(stab[15]);

        // Window (2 octets)
        window = Integer.parseInt(sBr.get(14) + sBr.get(15), 16);

        // Checksum (2 octets)
        checksum = Integer.parseInt(sBr.get(16) + sBr.get(17), 16);

        // Urgent Pointer (2 octets)
        urgentPointer = Integer.parseInt(sBr.get(18) + sBr.get(19), 16);

        // Options
        int startOptions = 20;
        int finOptions = THL * 4;
        if (4 * THL > 20) {

            int option;
            while (finOptions - startOptions > 1) {
                option = Integer.parseInt(sBr.get(startOptions), 16);
                if (option == 0) {
                    // options de bourrage, donc ne s'arrête pas jusqu'à la fin
                    // ajout du nom de l'option à la liste
                    options.add(optionList.get(option));
                    // ajout de la longueur du bourrage à la liste
                    longueurs.add(finOptions - startOptions + 1);
                    // on quitte le while puisque c'est la dernière option
                    break;
                }
                if (option == 1) {
                    // pas compris si c'est comme End Of Option List ou pas
                    options.add(optionList.get(option));
                    longueurs.add(1);
                    startOptions += 1;
                }
                if (option > 1) {
                    // ajout du nom de l'option à la liste
                    options.add(optionList.get(option));
                    // ajout de la longueur de l'option à la liste
                    longueurs.add(Integer.parseInt(sBr.get(startOptions + 1), 16));
                    // incrémentation de startOptions jusqu'à la prochaine option
                    startOptions += Integer.parseInt(sBr.get(startOptions + 1), 16);
                }
            }
        }

        /* INITIALISATION MESSAGE */
        /*
         * notre analysateur reconnait juste http, pour autres services faut ajouter les
         * destinations connus et initialiser le message avec le type souhaite
         */

        /*
         * si la trame se termine ici on n'a pas de message encapsule donc http = null
         */
        boolean http;
        if (finOptions == sBr.size()) {
            http = false;
        }
        /* on a un message apres les options */
        else {
            http = false;
            int dM = -1, im2 = -1, im1 = -1;
            StringBuilder mot3 = new StringBuilder();
            StringBuilder mot1 = new StringBuilder();

            // on cherche 0d 0a
            for (int i = finOptions; i < sBr.size() - 1; i++) {
                if (sBr.get(i).toLowerCase().equals("0d") && (sBr.get(i + 1).toLowerCase().equals("0a"))) {
                    dM = i;
                    break;
                }
            }
            // si on a un 0d 0a, on cherche un 20, en retenant le mot
            if (dM != -1) {
                for (int i = dM - 1; i >= 0; i--) {
                    if (sBr.get(i).equals("20")) {
                        im2 = i;
                        break;
                    } else {
                        mot3.insert(0, Character.toString(Integer.parseInt(sBr.get(i), 16)));
                    }
                }
                // si on a un 20, on cherche un autre 20
                if (im2 != -1) {
                    for (int i = im2 - 1; i >= 0; i--) {
                        if (sBr.get(i).equals("20")) {
                            im1 = i;
                            break;
                        }
                    }
                    if (im1 != -1) {
                        for (int i = im1 - 1; i >= finOptions; i--) {
                            mot1.insert(0, Character.toString(Integer.parseInt(sBr.get(i), 16)));
                        }
                        if (mot1.toString().equals("HTTP/1.1") || mot1.toString().equals("HTTP/1.2")
                                || (mot3.toString().equals("HTTP/1.1") || mot3.toString().equals("HTTP/1.2")))

                        {
                            http = true;
                        }

                    }

                }

            }
        }

        //if(SYN == ) http = false;
        if (http && (destinationPort == 80 || sourcePort == 80)) {
            message = new Message(sBr.subList(finOptions, sBr.size()));
        } else {
            message = null;
        }

        brut = false;
        opt = false;
        vv = false;
    }

    @Override
    public int getProtocol() {
        return 6; // tcp
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!brut) {
            sb.append("Segment: sourcePort=" + sourcePort + ", destinationPort="
                    + destinationPort + ", sequenceNumber=" + sequenceNumber + ", ackNumber=" + ackNumber + ", THL="
                    + THL
                    + ", reserved=" + reserved + ", URG=" + URG + ", ACK=" + ACK + ", PSH=" + PSH + ", RST=" + RST
                    + ", SYN=" + SYN + ", FIN=" + FIN + ", window=" + window + ", checksum=" + checksum
                    + ", urgentPointer="
                    + urgentPointer + ", options=" + options + ", longueurs=" + longueurs
                    + "]");

            if (message != null) {
                sb.append(message.toString());
            }
        } else {
            for (String i : segmentBrut) {
                sb.append(i + " ");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    @Override
    public String getComment() {
        StringBuilder sb = new StringBuilder();
        if (vv) {
            sb.append(toString());
        } else {
            if (message == null) {
                sb.append("TCP: ");
                sb.append(sourcePort + " -> " + destinationPort + " ");
                if (URG == 1 || ACK == 1 || PSH == 1 || RST == 1 || SYN == 1 || FIN == 1) {
                    sb.append("Flags=[");
                    if (URG == 1)
                        sb.append(" URG ");
                    if (ACK == 1)
                        sb.append(" ACK ");
                    if (PSH == 1)
                        sb.append(" PSH ");
                    if (RST == 1)
                        sb.append(" RST ");
                    if (SYN == 1)
                        sb.append(" SYN ");
                    if (FIN == 1)
                        sb.append(" FIN ");
                    sb.append("] ");
                }
                sb.append("Seq=[" + sequenceNumber + "] Ack=[" + ackNumber + "] ");
                sb.append("Window=[" + window + "] ");
                if (opt && options.size() != 0) {
                    sb.append("Options:[ ");
                    for (int i = 0; i < options.size(); i++) {
                        sb.append(options.get(i) + "(" + longueurs.get(i) + ") ");
                    }
                    sb.append("]");
                }
            } else
                sb.append(message.getComment());
        }
        return sb.toString();
    }

    private void initializeMapWithApplications() {
        applicationList.put(80, "HTTP");
        applicationList = Collections.unmodifiableMap(applicationList);

    }

    private void initializeMapWithOptions() {
        optionList.put(0, "EOL");
        optionList.put(1, "NOP");
        optionList.put(2, "MSS");
        optionList.put(3, "Window Scale");
        optionList.put(4, "SACK Permitted");
        optionList.put(5, "SACK (Selective ACK)");
        optionList.put(6, "Echo");
        optionList.put(7, "Echo Reply");
        optionList.put(8, "Time Stamp Option");
        optionList.put(9, "Partial Order Connection Permitted");
        optionList.put(10, "Partial Order Service Profile");
        optionList.put(11, "CC");
        optionList.put(12, "CC.NEW");
        optionList.put(13, "CC.ECHO");
        optionList.put(14, "TCP Alternate Checksum Request");
        optionList.put(15, "TCP Alternate Checksum Data");

        optionList = Collections.unmodifiableMap(optionList);
    }

    @Override
    public int getSourcePort() {
        return sourcePort;
    }

    @Override
    public int getDestionationPort() {
        return destinationPort;
    }

    @Override
    public void setBrut(boolean brut) {
        this.brut = brut;
        if (message != null) {
            message.setBrut(brut);
        }
    }

    @Override
    public void setOpt(boolean opt) {
        this.opt = opt;
        if (message != null) {
            message.setOpt(opt);
        }
    }

    public void setVV(boolean vv) {
        this.vv = vv;
        if (message != null) {
            message.setVV(opt);
        }
    }

    @Override
    public IMessage getIMessage() {
        return message;
    }

    @Override
    public Map<Integer, String> getApplicationList() {
        return applicationList;
    }

    

    

}
