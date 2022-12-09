package trame;

import java.io.File;
import java.io.FileNotFoundException;

/*TODO:
 * - parametres ( filtres mac )  - BONUS
 * - sauvegarde avec fichier en -o BONUS
 * - sauvegarde to pdf ou txt 
 * - menu help - BONUS
 * - copy trame? 
 * - interface graphique 
 * - video 
 * - ip a partir d'un fichier - BONUS
 */
public class VisualisateurMain {
    public static void main(String[] args) {
        // parametres
        // boolean[] opt = { false };
        if (args.length == 0) {
            System.out.println("Il faut donner des parametres\n");
            System.exit(1);
        }
        if (args[0].equals("-h")) {
            ListeTrames.menuHelp();
            System.exit(1);
        }

        /* interface graphique */
        if (args[0].equals("-X")) {
            trame.extra.VisuGraphMain.main(null);
            return;
        }

        File f = new File(args[0]);
        if (!f.isFile()) {
            System.out.println("Veuillez entrez un fichier en premier parametre");
            System.exit(1);
        }

        // charger la liste des trames depuis le ficher passe en parametre
        ListeTrames lt = ListeTrames.loadTrames(f.getAbsolutePath());

        /* appliquer les filtre ou option (si il y a) */
        for (int i = 1; i < args.length; i++) {

            if (args[i].equals("-vv")) {

                lt.setVV(true);
            }

            else if (args[i].equals("-v")) {
                lt.setOpt(true);
            }

            else if (args[i].equals("-i")) {
                i++;
                String[] ipa = args[i].split("\\,");
                lt.filtreParIp(ipa);
            }

            else if (args[i].equals("-p")) {
                i++;
                String[] ipa = args[i].split("\\,");
                lt.filtreParProtocol(ipa);
            }
        }

        lt.prettyPrint();
        try{
            lt.prettyPrint2();
        } catch(FileNotFoundException e){ // Pas de soucis cela créer un fichier output.txt quoi qu'il arrive à l'endroit ou le main a été executé
            System.out.println(e.getMessage());
        }

        System.out.println(lt.getListIP());

    }
}
