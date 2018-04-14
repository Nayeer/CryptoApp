package com.example.brygo.cryptoapp.algorithmes;


public class Atbash {

    public static String execute(String msg, boolean crypt) {
        //c'est la même manipulation pour crypt ou non
        String result = "";
        for (char c : msg.toCharArray()) {
            if (c < 32 || c > 126) continue;
            result += (char) (126 - c + 32);
        }

        return result;
    }

}
