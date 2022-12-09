package trame;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListeTrames {
    List<ITrame> liste;
    List<String> listIP;

    public ListeTrames() {
        liste = new ArrayList<>();
        listIP = new ArrayList<>();
    }

    public void add(ITrame t) {
        liste.add(t);
    }

    public ITrame get(int i) {
        return liste.get(i);
    }

    public int size() {
        return liste.size();
    }

    public List<String> getListIP() {
        listIP = new ArrayList<>();
        List<IPAddress> listIPIP = new ArrayList<>();
        List<AdresseMac> listMAC = new ArrayList<>();
        for(ITrame t : liste) {
            if(t.getIPacket() != null) {
                if (!listIPIP.contains(t.getIPacket().getIPSource())) {
                    listIPIP.add(t.getIPacket().getIPSource());
                    listIP.add(t.getIPacket().getIPSource().toString());
                }
                if (!listIPIP.contains(t.getIPacket().getIPDestination())){
                    listIPIP.add(t.getIPacket().getIPDestination());
                    listIP.add(t.getIPacket().getIPDestination().toString());
                }
            }
            else {
                if (!listMAC.contains(t.getMACSource())) {
                    listMAC.add(t.getMACSource());
                    listIP.add(t.getMACSource().toString());
                }
                if (!listMAC.contains(t.getMACDestination())) {
                    listMAC.add(t.getMACDestination());
                    listIP.add(t.getMACDestination().toString());
                }
            }
        }
        return listIP;
    }

    public static ListeTrames loadTrames(String filename) {
        /* la liste de trames */
        ListeTrames listTr = new ListeTrames();
        /* buffer pour stocker les octets d'une trame */
        List<String> buffer = new ArrayList<>();
        int lig = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // un compteur pour retenir le numero de la ligne qu'on charge actuallement
            lig = 0;

            // on itere sur toutes les lignes du fichier
            for (String line = br.readLine(); line != null; line = br.readLine()) {

                lig++;

                /* formater la ligne (retirer les espaces ou les \n) */

                if (line.length() == 0) {
                    continue;
                }
                // on split la ligne d'apres les espaces
                String[] octetsL = line.split("  "); // couper la string sur les espaces

                // le offset
                int offset = Integer.parseInt(octetsL[0], 16);

                // nouvelle trame
                if (offset == 0) {
                    if (buffer.size() != 0) {
                        ITrame t = new Trame(buffer);
                        listTr.add(t);
                        buffer.clear();
                    }
                }
                String[] octets = octetsL[1].split(" ");
                // si ajoute les octets dans la listTr
                for (int i = 0; i < octets.length; i++) {
                    if (octets[i].length() == 2)
                        buffer.add(octets[i]);
                }

            }

            if (buffer.size() != 0) {
                ITrame t = new Trame(buffer);
                listTr.add(t);
                buffer.clear();
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Erreur a la ligne " + lig + e.getMessage());
        }
        return listTr;
    }

    // TODO: iterator
    /**
     * Retire toutes les trames qui ne contient l'adresse ip passee en parametre
     * Attention! cette operation modifie la ListeTrames, TODO: copy avant (maybe)
     * 
     * @param un String qui contient une adresse ip format point decimale
     * @return le nombre des trames supprimees
     */

    public int filtreParIp(String ipS) {
        IPAddress ip = new IPAddress(ipS);
        List<ITrame> cible = new ArrayList<>();
        int cpt = 0;
        for (ITrame trame : liste) {
            if (ip.equals(trame.getIPacket().getIPDestination()) || ip.equals(trame.getIPacket().getIPSource()))
                cible.add(trame);
            else
                cpt++;
        }
        liste = cible;
        return cpt;
    }

    /**
     * Retire toutes les trames qui ne contient pas une adresse ip dans la liste
     * passe en param
     * Attention! cette operation modifie la ListeTrames, TODO: copy avant (maybe)
     * 
     * @param ipS une liste des adresses IP en decimal pointe
     * @return le nombre des elements retires
     */
    public int filtreParIp(String[] ipS) {
        IPAddress ip;
        List<ITrame> cible = new ArrayList<>();
        int cpt = 0;
        for (ITrame trame : liste) {
            for (int i = 0; i < ipS.length; i++) {
                ip = new IPAddress(ipS[i]);
                if (trame.getIPacket() != null) {
                    if (ip.equals(trame.getIPacket().getIPDestination()) || ip.equals(trame.getIPacket().getIPSource()))
                        cible.add(trame);
                    else
                        cpt++;
                }
            }
        }
        liste = cible;
        return cpt;
    }

    /**
     * Retire toutes les trames qui ne contient pas un protocol de la liste
     * passe en param
     * Attention! cette operation modifie la ListeTrames, TODO: copy avant (maybe)
     * 
     * @param ipS une liste des protocols en decimal pointe
     * @return le nombre des elements retires
     */
    public int filtreParProtocol(String[] pS) {
        List<ITrame> cible = new ArrayList<>();
        int cpt = 0;
        Map<Integer, String> li;
        boolean added = false;
        for (ITrame trame : liste) {
            added = false;
            for (int i = 0; i < pS.length; i++) {
                // pS[i] est le filtre qu'on veut appliquer
                // on va chercher ce mot dans les listes reconnus, commencant par message,
                // paquet, segment
                if (trame.getIPacket() != null) {
                    if (trame.getIPacket().getISegment() != null) {
                        if (trame.getIPacket().getISegment().getIMessage() != null) {
                            li = trame.getIPacket().getISegment().getApplicationList();
                            // on commence par le type de message
                            for (Map.Entry<Integer, String> pair : li.entrySet()) {
                                if (pair.getValue().toLowerCase().equals(pS[i].toLowerCase())) {
                                    // filtre reconnu
                                    if (pair.getKey() == trame.getIPacket().getISegment().getIMessage().getType()) {
                                        // filtre trouve
                                        added = true;
                                        break;
                                    }
                                }
                            }

                        }
                        if (!added) {
                            li = trame.getIPacket().getProtocolList();
                            // on commence par le type de message
                            for (Map.Entry<Integer, String> pair : li.entrySet()) {
                                if (pair.getValue().toLowerCase().equals(pS[i].toLowerCase())) {
                                    // filtre reconnu
                                    if (pair.getKey() == trame.getIPacket().getISegment().getProtocol()) {
                                        // filtre trouve
                                        added = true;
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    if (!added) {
                        li = trame.getTypeList();
                        // on commence par le type de message
                        for (Map.Entry<Integer, String> pair : li.entrySet()) {
                            if (pair.getValue().toLowerCase().equals(pS[i].toLowerCase())) {
                                // filtre reconnu
                                if (pair.getKey() == trame.getIPacket().getType()) {
                                    // filtre trouve
                                    added = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (added)
                cible.add(trame);
            else
                cpt++;
        }
        liste = cible;
        return cpt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ITrame t : liste) {
            sb.append(t.toString());
        }
        return sb.toString();
    }

    public String getComment() {
        StringBuilder sb = new StringBuilder();
        for (ITrame t : liste) {
            sb.append(t.getComment());
        }
        return sb.toString();
    }

    
    public static final String RESET = "\033[0m";      // TEXT RESET
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    private String[] couleurs = {RED, GREEN, YELLOW, BLUE, PURPLE, CYAN, WHITE, BLACK};

    public void prettyPrint() {
        StringBuilder sb1, sb2;
        int portSrc, portDest;
        Character s1cdeb, s1cfin;
        List<String> commsb1 = new ArrayList<>();
        List<String> commsb2 = new ArrayList<>();
        boolean newcom = false;
        int co=0;

        System.out.printf(
                "--------------------------------------------------------------------------------------------------------------%n");
        System.out.printf("| %-20s      |      %-20s | %-50s |\n", "Client", "Serveur",
                "Commentaire");

        System.out.printf(
                "--------------------------------------------------------------------------------------------------------------%n");

        for (ITrame t : liste) {
            /* source = client = a gauche; destination = server = a droit */
            /* Faire le message a gauche (le client) et a droit (le serveur) */
            sb1 = new StringBuilder();
            sb2 = new StringBuilder();

            AdresseMac macs = t.getMACSource(); // on est sur d'avoir, car il y a que ethernet
            AdresseMac macd = t.getMACDestination();
            sb1.append(macs);
            sb2.append(macd);
            s1cdeb = '-';
            s1cfin = '>';

            if (t.getIPacket() != null) {
                IPAddress ipS = t.getIPacket().getIPSource();
                IPAddress ipD = t.getIPacket().getIPDestination();
                if (ipS != null || ipD != null) { // ip exists
                    sb1.setLength(0);
                    sb2.setLength(0);
                    sb1.append(ipS);
                    sb2.append(ipD);
                    s1cdeb = '-';
                    s1cfin = '>';
                    if (t.getIPacket().getISegment() != null) { // ports exist
                        portSrc = t.getIPacket().getISegment().getSourcePort();
                        portDest = t.getIPacket().getISegment().getDestionationPort();
                        if (portSrc != 0 && portDest != 0) { // si portes valides
                            if (portSrc <= 1024) {
                                sb1.setLength(0);
                                sb2.setLength(0);
                                sb1.append(ipD + ":" + portDest);
                                sb2.append(ipS + ":" + portSrc);
                                s1cdeb = '<';
                                s1cfin = '-';
                            } else if (portDest <= 1024) {
                                sb1.append(":" + portSrc);
                                sb2.append(":" + portDest);
                                s1cdeb = '-';
                                s1cfin = '>';
                            } else {
                                sb1.append(":" + portSrc);
                                sb2.append(":" + portDest);
                                s1cdeb = '-';
                                s1cfin = '>';
                            }
                        }
                    }
                }
            }

            /* split the comment into multiple comments the size of the comment column k */
            int k = 50, i;
            boolean depassement; // si il y a de depassement dans le commentaire

            StringBuilder sb3 = new StringBuilder(); // sb temporaire pour mettre les sections de comm dans le terminal
            List<String> ls = new ArrayList<>(); // la liste de commentaire tronque a k caracteres

            if (k >= t.getComment().length()) {
                ls.add(t.getComment());
                depassement = false;
            } else {
                depassement = true;
                for (i = 0; i < t.getComment().length(); i++) {
                    if (i >= 1 && i % k == 0) {
                        // sb3.append(" |\n\t\t\t\t\t\t | ");
                        // String s = new String("| %-20s | %-20s | %-40s |\n")
                        // sb3.append("| %-20s | %-20s | %-40s |\n")
                        // sb3.append(" | \n\t\t\t\t\t\t");
                        ls.add(sb3.toString());
                        sb3.setLength(0);
                    }
                    if (t.getComment().charAt(i) == '\n')
                        sb3.append(' ');
                    else
                        sb3.append(t.getComment().charAt(i));
                }
                ls.add(sb3.toString());
            }
            
            // Changement de couleur si changement de traffic 
            newcom=true;
            for(int b=0; b<commsb1.size();b++){
                if(commsb1.get(b).equals(sb1.toString()) && commsb2.get(b).equals(sb2.toString())){
                    co = b;
                    newcom = false;
                }
            }

            if(newcom){
                commsb1.add(sb1.toString());
                commsb2.add(sb2.toString());
                co = commsb1.size() - 1;
            }
            
            // System.out.println(ls);
            System.out.printf(couleurs[co%couleurs.length]+"| %-20s %c---------%c %20s | %-50s |\n", sb1.toString(), s1cdeb, s1cfin, sb2.toString(),
                    ls.get(0));

            if (depassement) {
                for (i = 1; i < ls.size(); i++) {
                    System.out.printf("| %-20s             %-20s | %-50s |\n", "", "", ls.get(i));
                }
            }
            System.out.printf(
                    "--------------------------------------------------------------------------------------------------------------%n");

            // System.out.println(t.getComment());

        }
        System.out.printf(
                "--------------------------------------------------------------------------------------------------------------%n");
    }

    public void prettyPrint2() throws FileNotFoundException{

        PrintStream out = new PrintStream(new FileOutputStream("./output.txt"));
        System.setOut(out);

        StringBuilder sb1, sb2;
        int portSrc, portDest;
        Character s1cdeb = '-', s1cfin = '>';
        List<String> commsb1 = new ArrayList<>();
        List<String> commsb2 = new ArrayList<>();
        boolean newcom = false;
        int co=0;

        System.out.printf(
                "==============================================================================================================%n");
        System.out.printf("| %-20s      |      %-20s | %-50s |\n", "Client", "Serveur",
                "Commentaire");

        System.out.printf(
                "==============================================================================================================%n");

        // "--------------------------------------------------------------------------------------------------------------%n");

        for (ITrame t : liste) {
            /* source = client = a gauche; destination = server = a droit */
            /* Faire le message a gauche (le client) et a droit (le serveur) */
            sb1 = new StringBuilder();
            sb2 = new StringBuilder();

            if (t.getPortSrc().length() != 0 && t.getPortDst().length() != 0) { // si portes valides
                portSrc = Integer.parseInt(t.getPortSrc());
                portDest = Integer.parseInt(t.getPortDst());
                if (portSrc <= 1024) {
                    sb1.append(t.getMesDst() + ":" + portDest);
                    sb2.append(t.getMesSrc() + ":" + portSrc);
                    s1cdeb = '<';
                    s1cfin = '-';
                } else {
                    sb1.append(t.getMesSrc() + ":" + portSrc);
                    sb2.append(t.getMesDst() + ":" + portDest);
                    s1cdeb = '-';
                    s1cfin = '>';
                }
            } else {
                sb1.append(t.getMesSrc());
                sb2.append(t.getMesDst());
                s1cdeb = '-';
                s1cfin = '>';
            }

            /* split the comment into multiple comments the size of the comment column k */
            int k = 50, i;
            boolean depassement; // si il y a de depassement dans le commentaire

            StringBuilder sb3 = new StringBuilder(); // sb temporaire pour mettre les sections de comm dans le terminal
            List<String> ls = new ArrayList<>(); // la liste de commentaire tronque a k caracteres

            if (k >= t.getComment().length()) {
                ls.add(t.getComment());
                depassement = false;
            } else {
                depassement = true;
                for (i = 0; i < t.getComment().length(); i++) {
                    if (i >= 1 && i % k == 0) {
                        // sb3.append(" |\n\t\t\t\t\t\t | ");
                        // String s = new String("| %-20s | %-20s | %-40s |\n")
                        // sb3.append("| %-20s | %-20s | %-40s |\n")
                        // sb3.append(" | \n\t\t\t\t\t\t");
                        ls.add(sb3.toString());
                        sb3.setLength(0);
                    }
                    if (t.getComment().charAt(i) == '\n')
                        sb3.append(' ');
                    else
                        sb3.append(t.getComment().charAt(i));
                }
                ls.add(sb3.toString());
            }
            // Changement de couleur si changement de traffic 
            newcom=true;
            for(int b=0; b<commsb1.size();b++){
                if(commsb1.get(b).equals(sb1.toString()) && commsb2.get(b).equals(sb2.toString())){
                    co = b;
                    newcom = false;
                }
            }

            if(newcom){
                commsb1.add(sb1.toString());
                commsb2.add(sb2.toString());
                co = commsb1.size() - 1;
            }
            // System.out.println(ls);
            System.out.printf("| %-20s %c---------%c %20s | %-50s |\n", sb1.toString(), s1cdeb, s1cfin, sb2.toString(),
                    ls.get(0));

            if (depassement) {
                for (i = 1; i < ls.size(); i++) {
                    System.out.printf("| %-20s             %-20s | %-50s |\n", "", "", ls.get(i));
                }
            }
            System.out.printf(
                    "--------------------------------------------------------------------------------------------------------------%n");
        }
        
        // System.out.println(t.getComment());
        System.out.printf(
                "==============================================================================================================%n");

        // "--------------------------------------------------------------------------------------------------------------%n");

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    public void setBrut(boolean brut) {
        for (ITrame t : liste) {
            t.setBrut(brut);
        }
    }

    public void setOpt(boolean opt) {
        for (ITrame t : liste) {
            t.setOpt(opt);
        }
    }

    public void setVV(boolean b) {
        for (ITrame t : liste) {
            t.setVV(b);
        }
    }
    public List<ITrame> getTrames() {
        return liste;
    }

    public static void menuHelp(){
        System.out.println(
        "\033[0;36mCette application a pour but de visualiser le traffic entre un ou des clients\n" +
        "avec un ou plusieurs serveurs. Notre visualisateur décode les Trames Ethernet,\n" +
        "les paquets IPv4, les segments TCP ainsi que les messages HTTP .\033[0m\n\n" +
        
        "\033[0;31mPour avoir accès au menu help :\n"+
        "java -jar reseaux.jar -h\033[0m\n\n" +
        
        "Notre visualisateur possède 2 interfaces: terminale et graphique.\n" +
        "Il faut ajouter l'option -X pour lancer le visualisateur sous l'interface graphique.\n\n" +
        
        "java -jar reseaux.jar {fichier} // Visualisateur terminale\n\n" +
        
        "java -jar reseaux.jar -X // Visualisateur graphique\n\n" +
        
        "Notre programme possède multiples options permettant une visualisation plus précise du traffic :\n\n" +
        
        "-v  : affiche les options de l'entête la plus haute encapsulé si il y en a.\n" +
        "Exemple :\n"+
        "\033[0;32mjava -jar reseaux.jar {fichier} -v\033[0m\n\n" +
        
        "-vv : affiche toutes les informations de la trame.\n" +
        "Exemple :\n" +
        "\033[0;32mjava -jar reseaux.jar {fichier} -vv\033[0m\n\n" +
         
        "-i  : permet d'afficher les trames possédant seulement les ip fournies (séparé d'une virgule).\n" +
        "Exemple :\n" +
        "\033[0;32mjava -jar reseaux.jar {fichier} -i 145.254.160.237,216.239.59.99\033[0m\n\n" +
        
        "-p  : permet d'afficher les trames possédant l'entête fournie (séparé d'une virgule).\n" +
        "Exemple :\n" +
        "\033[0;32mjava -jar reseaux.jar {fichier} -p tcp,http\033[0m\n\n" +
        
        "Evidemment toutes les options sont cumulables.\n" +
        "Exemple : \n" +
        "\033[0;32mjava -jar reseaux.jar {fichier} -v -i 145.254.160.237,216.239.59.99 -p tcp \033[0m" 
        );
    }
}
