package com.example.brygo.cryptoapp.algorithmes;

import com.example.brygo.cryptoapp.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomophoniquePolybe {

    public static String execute(String msg, char[][] carrePolybe, String key,  boolean crypt) {
        msg = msg.replaceAll("j","i").replaceAll("\\s+","").toLowerCase();

        String result = "";

        // Si on a pas déjà de carré de polybe en parametre c'est qu'on doit le générer avec la clé fournie
        if (carrePolybe == null) {
            carrePolybe = Utils.carrePolybeFromKey(key);
            //Log.d("debug", Arrays.deepToString(carrePolybe));
        }

        //    Transformation du carré de polybe en 1-Dimension list pour optimiser la recherche d'element
        //    grâce à cela nous pourrons utiliser la méthode indexOf()
        List<Character> flatArray = new ArrayList<Character>();
        for (char[] row : carrePolybe) {
            for (char c : row)
                flatArray.add(c);
        }

        boolean expectNewCouple = true; //servira lors du decryptage à savoir à quelle étape on est
        int num_line = -1;
        int num_col = -1;
        for (int i=0; i<msg.length(); i++) { //Boucle de lecture du message
            char c = msg.charAt(i);

            //Recherche de l'indice de la lettre actuelle
            int c_indice = flatArray.indexOf(c);
            int c_line = c_indice / 5; //num ligne de c
            int c_col = c_indice % 5; //num colonne de c

            if (crypt) { //Cryptage
                //On génére le couple de lettre crypté
                int random_col = Utils.randomExcept(0, 4, c_col);
                int random_line = Utils.randomExcept(0, 4, c_line);
                result += carrePolybe[c_line][random_col] + "" + carrePolybe[random_line][c_col];
            } else {

                //Decryptage
                if (expectNewCouple) {
                    num_line = c_line;
                    expectNewCouple = false;
                }
                else {
                    num_col = c_col;
                    result += carrePolybe[num_line][num_col];
                    expectNewCouple = true;
                }
            }
        }

        return result;
    }

}
