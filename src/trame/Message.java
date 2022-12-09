package trame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements IMessage {

    private String champ1; // methode ou version
    private String champ2; // url ou code status
    private String champ3; // version ou message
    private List<String> messageBr;
    private Map<String, String> entetes = new HashMap<>();
    private Map<Integer, String> servicesList = new HashMap<>();
    private String corps;

    private boolean brut;
    private boolean opt;
    private boolean vv;

    public Message(List<String> messageBr) {

        servicesList.put(80, "HTTP");
        
        this.messageBr = messageBr; // garder le message brut

        /* lire ligne de requete */

        // champ1
        /* la champ1 */
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (!(messageBr.get(i).equals("20"))) {
            sb.append(Character.toString(Integer.parseInt(messageBr.get(i), 16)));
            i++;
        }
        champ1 = sb.toString();

        // System.out.println(champ1);
        /* le champ2 */
        sb = new StringBuilder();
        i++;
        while (!(messageBr.get(i).equals("20"))) {
            sb.append(Character.toString(Integer.parseInt(messageBr.get(i), 16)));
            i++;
        }
        champ2 = sb.toString();
        // System.out.println(champ2);

        /* la champ3 */
        sb = new StringBuilder();
        i++;
        while (!(messageBr.get(i).toLowerCase().equals("0d") && (messageBr.get(i + 1).toLowerCase().equals("0a")))) {
            sb.append(Character.toString(Integer.parseInt(messageBr.get(i), 16)));
            i++;
        }
        champ3 = sb.toString();
        // System.out.println(champ3);
        i += 2;
        /* lire lignes d'entete */
        StringBuilder val;
        while (!(messageBr.get(i).toLowerCase().equals("0d") && (messageBr.get(i + 1).toLowerCase().equals("0a")))) {
            sb = new StringBuilder();
            val = new StringBuilder();
            // lire une ligne
            while (!(messageBr.get(i).equals("20"))) {
                sb.append(Character.toString(Integer.parseInt(messageBr.get(i), 16)));
                i++;
            }
            // enlever les deux points: peut etre
            sb.delete(sb.length() - 1, sb.length());

            // System.out.println(sb.toString());
            i++;
            while (!(messageBr.get(i).toLowerCase().equals("0d")
                    && (messageBr.get(i + 1).toLowerCase().equals("0a")))) {
                val.append(Character.toString(Integer.parseInt(messageBr.get(i), 16)));
                i++;
            }
            // ligne finie
            i += 2;
            entetes.put(sb.toString(), val.toString());
        }
        // System.out.println(entetes.toString());

        /* lire corps */
        i += 2;
        sb = new StringBuilder();
        /* si on a du corps */
        if (i < messageBr.size()) {
            while (i < messageBr.size()) {
                sb.append(Character.toString(Integer.parseInt(messageBr.get(i), 16)));
                i++;
            }
        }
        // System.out.println(i + " " + messageBr.size());
        if (sb.length() == 0) {
            sb.append("vide");
        }
        corps = sb.toString();
        // System.out.println(corps);

        brut = false;
        opt = false;
        vv = false;


    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!brut) {
            // TODO: nommer les champs
            // if(champ1.equals("GET") || champ1.equals("POST") || champ1.equals("HEAD") ||
            // champ1.equals("PUT") || )
            sb.append("Message: champ1=" + champ1 + ", champ2=" + champ2 + ", champ3=" + champ3 + ", entetes=" + entetes);
                    // + ", corps=" + corps);
        } else {
            for (String i : messageBr) {
                sb.append(i + " ");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    @Override
    public int getType() {
        return 80; // http
    }

    @Override
    public String getComment() {
        StringBuilder sb = new StringBuilder();
        if (vv) {
            sb.append(toString());
        } else {
            sb.append("HTTP: ");
            sb.append("[" + champ1 + " " + champ2 + " " + champ3 + "] ");
            if (opt && entetes.size() > 0) {
                sb.append("Headers=[ ");
                for (Map.Entry<String, String> entry : entetes.entrySet()) {
                    sb.append(entry.getKey() + ":[" + entry.getValue() + "] ");
                }
                sb.append("]");
            }
        }
        return sb.toString();
    }

    public List<String> getMessageBrut() {
        return messageBr;
    }

    @Override
    public void setBrut(boolean brut) {
        this.brut = brut;
    }

    @Override
    public void setOpt(boolean opt) {
        this.opt = opt;
    }

    public void setVV(boolean vv) {
        this.vv = vv;
    }

}
