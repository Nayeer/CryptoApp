package com.example.brygo.cryptoapp.algorithmes;

import com.example.brygo.cryptoapp.Utils;

import java.util.ArrayList;
import java.util.List;

public class Playfair {

    public static String execute(String msg, String key, boolean crypt) {
        String result = "";

        //On commence par considérer les caractères uniques de la clé afin de savoir les dimensions
        // du tableau intermediaire (composée des lettres de l'alphabet avant transposition en 5x5)
        key = Utils.getUniques(key);

        int nb_line = (int) Math.ceil(25f/key.length());
        int nb_col = key.length();
        char[][] intermediateArray = new char[nb_line][nb_col];
        char[][] finalArray = new char[5][5];
        msg = msg.replaceAll("j","i").replaceAll("\\s+","").toLowerCase();

        //Ajout des lettres distinctes de la clé en premiere ligne et dans alreadyAdded,
        // ce dernier est un string qui va permettre de facilement savoir quelle lettre on a
        // déjà ajouté plutôt que de reparcourir le tableau multidimensionnel à chaque fois.
        String alreadyAdded = key;
        intermediateArray[0]= alreadyAdded.toCharArray();

        //remplissage du tableau intermediaire à partir de la clé (ajout des lettres manquantes)
        int i = 1; int j = 0;  //   array indices
        for (char c='a'; c<='z'; c++) {
            if (c=='j') continue;
            if (alreadyAdded.indexOf(c) == -1) {
                intermediateArray[i][j] = c;
                alreadyAdded += c;
                j++;
                if (j >= nb_col) {
                    j=0;
                    i++;
                }
            }
        }
        //Log.d("debug", Arrays.deepToString(intermediateArray));
        // Transposition de la "matrice" afin d'obtenir notre carré 5x5
        i = j = 0;
        for (int k=0; k<5; k++){
            for (int l=0; l<5; l++) {
                finalArray[k][l] = intermediateArray[i][j];
                if (i<nb_line-1 && intermediateArray[i+1][j] != '\u0000') i++;
                else {
                    i=0;
                    j++;
                }
            }
        }
        //Log.d("debug", Arrays.deepToString(finalArray));
        // Transformation en 1-Dimension list pour optimiser la recherche d'element
        List<Character> flatArray = new ArrayList<Character>();
        for (char[] row : finalArray) {
            for (char c : row)
                flatArray.add(c);
        }

        //Maintenant, on parcourt le message à crypter ou décrypter,
        char[] msgArray = msg.toCharArray();
        for (i=0; i<msgArray.length; i++) {

            char[] couple = new char[2];

            //On extrait le couple lu , grâce à i et i+1
            // mais dans le cas où la longueur du message n'est pas pair ou que les deux lettres sont identiques
            // alors on ajoute la lettre nulle 'q'
            couple[0] = msgArray[i];
            if (i+1 < msgArray.length && msgArray[i] != msgArray[i+1]) {
                couple[1] = msgArray[i + 1];
                i++;
            }
            else couple[1] = 'q';

            //On cherche les num de line et col des couples de façon optimisée grâce à notre flatArray et la méthode indexOf
            int c0_indice = flatArray.indexOf(couple[0]);
            int c1_indice = flatArray.indexOf(couple[1]);
            int c0_line = c0_indice/5; int c1_line = c1_indice/5;
            int c0_col = c0_indice%5; int c1_col = c1_indice%5;

            //Maintenant pour chaque cas de figure de positionnement des lettres on applique
            // la règle pour crypter ou décrypter notre couple de lettre

            if (c0_line != c1_line && c0_col != c1_col) {

                //Les deux lettres sont sur des lignes et colonnes différentes
                //Le traitement est donc symétrique entre le cryptage et le décryptage
                result += finalArray[c0_line][c1_col];
                result += finalArray[c1_line][c0_col];

            } else if (c0_line == c1_line) {

                //Les deux lettres sont sur la même ligne
                if (crypt) {
                    result += (c0_col + 1 >= 5) ? finalArray[c0_line][0] : finalArray[c0_line][c0_col + 1];
                    result += (c1_col + 1 >= 5) ? finalArray[c1_line][0] : finalArray[c1_line][c1_col + 1];
                } else {
                    result += (c0_col - 1 < 0) ? finalArray[c0_line][4] : finalArray[c0_line][c0_col - 1];
                    result += (c1_col - 1 < 0) ? finalArray[c1_line][4] : finalArray[c1_line][c1_col - 1];
                }

            } else if (c0_col == c1_col) {

                //Les deux lettres sont sur la même colonne
                if (crypt) {
                    result += (c0_line + 1 >= 5) ?  finalArray[0][c0_col] : finalArray[c0_line + 1][c0_col];
                    result += (c1_line + 1 >= 5) ?  finalArray[0][c1_col] : finalArray[c1_line + 1][c1_col];
                } else {
                    result += (c0_line - 1 < 0) ?  finalArray[4][c0_col] : finalArray[c0_line - 1][c0_col];
                    result += (c1_line - 1 < 0) ?  finalArray[4][c1_col] : finalArray[c1_line - 1][c1_col];
                }

            }
        }
        return result;
    }
}
