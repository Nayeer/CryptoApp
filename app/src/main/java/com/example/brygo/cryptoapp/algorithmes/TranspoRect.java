package com.example.brygo.cryptoapp.algorithmes;

import java.util.Arrays;

public class TranspoRect {

    public static String execute(String msg, String key, boolean crypt) {
        String result = "";
        char[] keyArray = key.toCharArray();
        int[] columnsIndices = new int[key.length()];
        String keyCopy = key; //copie de la clé sur laquelle on effectuera des modifications

        // On tri chaque caractère dans l'ordre ascendant dans le tableau keyArray
        // et grâce à ce tableau on peut facilement associer le bon indice dans keyIndices
        Arrays.sort(keyArray);
        //Parcours de chaque char triés
        for (int i = 0; i < keyArray.length; i++) {
            int currentCharIndice = keyCopy.indexOf(keyArray[i]);
            //remplacement du caractère actuel par le caractère vide afin de ne plus le considérer lors d'un indexOf
            keyCopy = keyCopy.substring(0, currentCharIndice) + '\u0000' + keyCopy.substring(currentCharIndice + 1);

            //On va récupérer directement la position de la 1ere lettre, puis de la deuxième etc
            //ce qui donne l'indice des colonnes qu'on veut dans l'ordre
            columnsIndices[i] = currentCharIndice;
        }

        if (crypt) { //cryptage
            // On génère le message crypté en ayant les numéros de colonne
            int keyLen = key.length();
            for (int i = 0; i < columnsIndices.length; i++) {
                int letterPos = columnsIndices[i];
                while (letterPos < msg.length()) {
                    result += msg.charAt(letterPos);
                    letterPos += keyLen;
                }
            }

        } else { //decryptage
            int nb_line = (int) Math.ceil(msg.length()/(key.length()*1f));
            int nb_col = key.length();
            char[][] decryptArray = new char[nb_line][nb_col];

            int nbEmptyCell =  msg.length()%nb_col > 0 ? nb_col - msg.length()%nb_col : 0;
            //remplissage des cases vides dans la dernière ligne
            for (int i=nb_col-1; i>=nb_col - nbEmptyCell; i--) {
                decryptArray[nb_line-1][i] = 'x';
            }
            int letterPos = 0; //incremeteur pour le parcours du message crypté
            for (int columnIndice : columnsIndices) {
                //on ecrit chaque lettre dans la bonne colonne
                for (int j=0; j<nb_line;j++) {
                    if (decryptArray[j][columnIndice] != 'x') {
                        decryptArray[j][columnIndice] = msg.charAt(letterPos);
                        letterPos++;
                    }
                }
            }
            //Log.d("debug", Arrays.deepToString(decryptArray));
            //Generation du resultat
            for (char[] row : decryptArray) {
                for (char c : row) {
                    if (c == 'x') break;
                    result += c;
                }
            }
        }
        return result;
    }

}
