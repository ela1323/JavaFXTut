package trame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TrameLoader {

    public static List<Trame> loadTrames(String filename) {
       /* la liste de trames */
        List<Trame> listTr = new ArrayList<>();
        /* buffer pour stocker les octets d'une trame */
        List<String> buffer = new ArrayList<>();
        int lig = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // un compteur pour retenir le numero de la ligne qu'on charge actuallement
            lig = 0;

            // on itere sur toutes les lignes du fichier
            for (String line = br.readLine(); line != null; line = br.readLine()) {

                lig++;

                if(line.length() == 0) {
                    continue;
                }
                // on split la ligne d'apres les espaces
                String[] octets = line.split("\\s+"); // couper la string sur les

                // le offset
                int offset = Integer.parseInt(octets[0], 16);

                // nouvelle trame
                if (offset == 0) {
                    if (buffer.size() != 0) {
                        Trame t = new Trame(buffer);
                        listTr.add(t);
                        buffer.clear();
                    }
                }

                // si ajoute les octets dans la liste
                for (int i = 1; i < octets.length; i++) {
                    buffer.add(octets[i]);
                }

            }

            if (buffer.size() != 0) {
                Trame t = new Trame(buffer);
                listTr.add(t);
                buffer.clear();
            }
            br.close();

        } catch (Exception e) {
            System.out.println("Erreur a la ligne " + lig + e.getMessage());
        }

        return listTr;
    }
}
