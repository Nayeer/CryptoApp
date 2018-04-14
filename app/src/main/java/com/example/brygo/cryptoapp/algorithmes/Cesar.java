package com.example.brygo.cryptoapp.algorithmes;

public class Cesar {

    public static String execute(String msg, int decalage, boolean crypt) {
        String result = "";
        for (char c : msg.toCharArray()) {

            if (c < 32 || c > 126) continue;
            if (crypt) {
                //Si l'addition dépasse 126 , on repasse à 31 et on ajoute le dépassement
                //Sinon on ajoute directement
                if (c + decalage > 126) result += (char) (31 + ((c + decalage) - 126));
                else result += (char) (c + decalage);
            } else {
                //Si la soustraction est inférieure à 32, on repasse à 126 et retire le dépassement
                //Sinon on ajoute directement
                if (c - decalage < 32) result += (char) (126 - (31 -(c - decalage)));
                else result += (char) (c - decalage);
            }
        }

        return result;
    }
}
