package com.example.brygo.cryptoapp.algorithmes;

import com.example.brygo.cryptoapp.Utils;

import java.util.ArrayList;
import java.util.List;

public class Delastelle {

    public static String execute(String msg, char[][] carrePolybe, String key, int fragmentSize, boolean crypt) {
        msg = msg.replaceAll("j","i").replaceAll("\\s+","").toLowerCase();

        String result = "";

        // Si on a pas déjà de carré de polybe en parametre c'est qu'on doit le générer avec la clé fournie
        if (carrePolybe == null) {
            carrePolybe = Utils.carrePolybeFromKey(key);
            //Log.d("debug", Arrays.deepToString(carrePolybe));
        }

        //On ajoute au préalable le bon nombre de lettre pour que ça soit un multiple de fragmentSize
        int nbToAdd =  msg.length()%fragmentSize > 0 ? fragmentSize - msg.length()%fragmentSize : 0;
        for(int i=0; i<nbToAdd;i++) {
            msg+= 'x';
        }

        //Nous allons utiliser deux tableaux lineIndices et colIndices :
        //  Lors d'un cryptage :
        //      - lineIndices contiendra le numero de la ligne pour chacune des lettres lues
        //      - colIndices contiendra le numero de la colonne pour chacune des lettres lues
        //
        // Lors d'un decryptage :
        //      - lineIndices contiendra les numero de ligne + numero de colonne (à la suite)
        //        des lettres cryptées représentant les numéro de ligne du vrai message
        //      - colIndices contiendra les numero de ligne + numero de colonne (à la suite)
        //        des lettres cryptées représentant les numéro de colonne du vrai message


        int nb_item = msg.length();
        int[] lineIndices = new int[nb_item];
        int[] colIndices = new int[nb_item];


        //    Transformation en 1-Dimension list pour optimiser la recherche d'element
        //    grâce à cela nous pourrons utiliser la méthode indexOf()
        List<Character> flatArray = new ArrayList<Character>();
        for (char[] row : carrePolybe) {
            for (char c : row)
                flatArray.add(c);
        }

        //Inialisation de variables qui servira à la phase decryptage
        int i=0,j=0; //incrementeur qui permettra de se situer sur les deux tableaux
        String selectedArray = "lineIndices"; //On testera ce string pour savoir quel tableau parcourir à un instant t
        int fragmentIndice = 1; //Pour savoir dans quel fragment on se trouve
        boolean doSwitch = false; //detecte le moment où on doit diviser les coordonnées (quand fragmentSize impair)

        //En crypt ou decrypt nous parcourons le message en paramètre et calculons les coordonnées (ligne,colonne)
        // de chacune des lettres.

        for (int k=0; k<msg.length(); k++) { //Boucle de lecture du message
            char c = msg.charAt(k);

            //Recherche des indices de la lettre actuelle
            int c_indice = flatArray.indexOf(c);

            int c_line = c_indice/5; //num ligne de c
            int c_col = c_indice%5; //num colonne de c

            // - Lors d'un crypt cette boucle sert juste à initialiser les tableaux de coordonnées
            // - Lors d'un decrypt on en profite pour récupérer les coordonnées des lettres cryptées
            //   et de les ajouter dans le bon ordre dans les tableaux de coordonnées en faisant un parcours
            //   alternatif entre lineIndices et colIndices.

            if (crypt) {
                lineIndices[k] = c_line;
                colIndices[k] = c_col;
            } else { //Decryptage
                if (j < colIndices.length) { //Notre travail se terminera quand l'incremeteur j sera à sa dernière position.
                    if (selectedArray == "lineIndices") { //Traitement à faire sur lineIndices
                        //Si on ne doit pas switcher (spliter le dernier nombre deux)
                        // On ajoute simplement la ligne et la colonne à la suite
                        if (!doSwitch) {
                            lineIndices[i] = c_line;
                            lineIndices[i + 1] = c_col;
                        } else {
                            //Dans le cas d'un switch on écrit le num de ligne dans lineIndices
                            // et le num de colonne dans colIndices.
                            doSwitch = false;
                            lineIndices[i] = c_line;
                            colIndices[j] = c_col;
                            j++; //on incrémente j car on vient d'utiliser le premier element
                            selectedArray = "colIndices"; //on est arrivé à la fin du fragment
                            i = fragmentIndice * fragmentSize; //on place notre i au début du prochain fragment
                            continue; //la suite n'est pas executée
                        }

                        //vu qu'on traite deux elements à la fois notre i doit augmenter de 2 en 2
                        //on fait donc le traitement suivant pour éviter de sortir du fragment
                        //que sa taille soit pair ou impair
                        if (i + 2 == fragmentIndice * fragmentSize) { //c'est le cas quand fragmentSize est pair
                            selectedArray = "colIndices";
                            i = fragmentIndice * fragmentSize;
                        } else if (i + 2 == fragmentIndice * fragmentSize - 1) { //fragmentSize impair
                            //C'est uniquement lorsque fragmentSize est impair qu'on doit faire un "switch"
                            doSwitch = true;
                            i+=2;
                            continue;
                        } else {
                            i += 2;
                        }
                    } else { //Dans le cas où on parcours le tableau colIndices
                        //Le traitement de colIndices est plus simple car peu importe la polarité de fragmentSize
                        // il nous restera toujours un nombre pair d'élements à parcourir
                        colIndices[j] = c_line;
                        colIndices[j + 1] = c_col;

                        if (j + 2 == fragmentIndice * fragmentSize) {
                            selectedArray = "lineIndices";
                            j = fragmentIndice * fragmentSize;
                            fragmentIndice++;
                        } else {
                            j += 2;
                        }
                    }
                }
            }
        }

        if (!crypt) { //decryptage
            //Le decryptage est presque terminé il nous suffit d'afficher dans l'ordre les lettres.
            //On parcours nos deux tableaux lineIndices et ColIndices pour afficher le message decrypté
            for (i=0; i<lineIndices.length; i++) {
                int lineIndice = lineIndices[i];
                int colIndice = colIndices[i];
                result+=carrePolybe[lineIndice][colIndice];
            }

        } else { //cryptage
            //Après avoir initialisé les tableaux de coordonnées il nous faut parcourir
            //ces tableaux de la même façon que pour le décryptage (voir ci-dessus)
            //afin de générer les bons couple ligne,colonne

            //=== Phase de découpage en fragment de taille fragmentSize
            //    et de génération du message crypté
            selectedArray = "lineIndices";
            i = 0;
            j = 0;
            fragmentIndice = 1;
            int crypted_lineIndice, crypted_colIndice;
            while (j < colIndices.length) { //tant qu'on est pas arrivé au dernier élement de colIndices

                //Dans le cas où on parcours le tableau lineIndices
                if (selectedArray == "lineIndices") {

                    crypted_lineIndice = lineIndices[i];
                    crypted_colIndice = lineIndices[i + 1];
                    result += carrePolybe[crypted_lineIndice][crypted_colIndice];

                    if (i + 2 == fragmentIndice * fragmentSize) { //c'est le cas quand fragmentSize est pair
                        selectedArray = "colIndices";
                        i = fragmentIndice * fragmentSize;
                    } else if (i + 2 == fragmentIndice * fragmentSize - 1) { //fragmentSize impair
                        crypted_lineIndice = lineIndices[i + 2];
                        crypted_colIndice = colIndices[j];
                        j++; //on incrémente j car on vient d'utiliser le premier element
                        result += carrePolybe[crypted_lineIndice][crypted_colIndice];

                        selectedArray = "colIndices";
                        i = fragmentIndice * fragmentSize;
                    } else {
                        i += 2;
                    }
                } else { //Dans le cas où on parcours le tableau colIndices
                    crypted_lineIndice = colIndices[j];
                    crypted_colIndice = colIndices[j + 1];
                    result += carrePolybe[crypted_lineIndice][crypted_colIndice];

                    if (j + 2 == fragmentIndice * fragmentSize) {
                        selectedArray = "lineIndices";
                        j = fragmentIndice * fragmentSize;
                        fragmentIndice++;
                    } else {
                        j += 2;
                    }
                }
            }
        }
        return result;
    }
}
