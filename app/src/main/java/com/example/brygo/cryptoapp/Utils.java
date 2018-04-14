package com.example.brygo.cryptoapp;


import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {

//--------------------------------------------------------------------------------------------------
//                                          Utils
//--------------------------------------------------------------------------------------------------

    //fonction permettant de cacher le clavier quand on en a besoin
    public static void hideKeyboard(Activity context) {
        try {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    //ajoute un touch Listener à chaque objet qui n'est pas un editText afin de faire disparaitre le clavier
    //plus facilement
    public static void setupUI(final Activity context, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(context);
                    return false;
                }
            });
        }
    }

    //affiche un messsage dans Toast donné en paramètre
    public static void showMessage(Toast t, String msg) {
        t.setText(msg);
        t.show();
    }

    //Récupère les élements uniques d'une chaine de caractère
    public static String getUniques(String s) {
        String uniques = "";
        for(int i=0;i<s.length();i++)
        {
            if(uniques.indexOf(s.charAt(i))==-1) {
                uniques += s.charAt(i);
            }
        }
        return uniques;
    }

    //Génère un nombre aléatoire entre min et max , en évitant que ça soit égal à exception
    public static int randomExcept(int min, int max, int exception) {
        Random r = new Random();
        int res;
        do {
            res = r.nextInt((max - min) + 1) + min;
        } while (res == exception);
        return res;
    }

    //retourne un carré de polybe directement depuis l'input ou le string servant à l'autogénération d'un carré
    //+ gestion d'erreurs
    public static List<Object> getCarrePolybeFromInput(String input, Toast toast) {
        List<Object> output = new ArrayList<Object>();
        // CarrePolybe5x5 ou Autogeneration
        if (input.contains("-")) { // hypothèse : CarrePolybe5x5
            char[][] carrePolybe = parseCarrePolybe(input); //Extraction du carré de polybe depuis le champ key

            if (carrePolybe == null) { //si egal au tableau vide
                showMessage(toast, "Le carré de polybe en entrée est incorrect !");
            }
            else {
                output.add(true); //true si carré polybe false si clé classique.
                output.add(carrePolybe);
            }
        }
        else { //hypothèse : clé classique (Autogeneration)
            if (!String_isAlphabetical(input)) {
                showMessage(toast, "La clé doit être composée de lettres de l'alphabet !");
            }
            else {
                output.add(false); //true si carré polybe false si clé classique.(
                output.add(input);
            }
        }

        return output;
    }

    //fonction permettant de convertir un string de la forme xxxxx-xxxxx-xxxxx-xxxxx-xxxxx
    // en un carré polybe char[5][5]
    public static char[][] parseCarrePolybe(String s) {
        // s de la forme : xxxxx-xxxxx-xxxxx-xxxxx-xxxxx
        char[][] carrePolybe = new char[5][5];
        String alreadyAdded = ""; //contiendra toutes les lettres déjà ajoutées
        boolean error = false;
        int j =0;
        int k = 0;
        if (s.length() != 29) error = true; //Si il n'y a pas exactement 29 char
        else {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (i == 5 || i == 11 || i == 17 || i == 23) {
                    if (c != '-') {
                        error = true;
                        break;
                    }
                    j++;
                    k = 0;
                } else if (c < 'a' || c > 'z' || alreadyAdded.indexOf(c) != -1) {
                    error = true;
                    break;
                }
                else {
                    carrePolybe[j][k] = c;
                    alreadyAdded +=c;
                    k++;
                }
            }
        }

        if (error) return null; //si erreur retourne null

        return carrePolybe;

    }

    //fonction permettant d'auto-génerer un carré de polybe à partir d'une chaine de caractère
    //en ajoutant au fur et à mesure les lettres de l'alphabet manquantes
    public static char[][] carrePolybeFromKey(String key) {
        int nb_line=5;
        int nb_col=5;
        char[][] carrePolybe = new char[nb_line][nb_col];
        key = key.replaceAll("j","i").replaceAll("\\s+","").toLowerCase();
        key = getUniques(key); //on ne garde que les lettres uniques de la clé


        //remplissage du carre à partir de la clé
        int i = 0; int j = 0;  //   array indices

        // 1 - ajout de la clé
        for (char c : key.toCharArray()) {
            carrePolybe[i][j] = c;
            j++;
            if(j>=nb_col) { i++; j=0; }
        }

        // 2 - ajout de lettres manquantes
        String alreadyAdded = key;
        for (char c='a'; c<='z'; c++) {
            if (c=='j') continue; //on enleve le j qui sera confondu avec le i
            if (alreadyAdded.indexOf(c) == -1) {
                carrePolybe[i][j] = c;
                alreadyAdded += c;
                j++;
                if (j>=nb_col) { i++; j=0; }
            }
        }

        return carrePolybe;
    }

    //Teste si tous les caractères d'un string sont compris entre 'a' et 'z'
    public static boolean String_isAlphabetical(String s) {
        s = s.toLowerCase();
        for (char c : s.toCharArray()) {
            if (c<'a' || c>'z') return false;
        }
        return true;
    }

}
