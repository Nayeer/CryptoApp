package com.example.brygo.cryptoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.brygo.cryptoapp.algorithmes.Atbash;
import com.example.brygo.cryptoapp.algorithmes.Cesar;
import com.example.brygo.cryptoapp.algorithmes.Delastelle;
import com.example.brygo.cryptoapp.algorithmes.HomophoniquePolybe;
import com.example.brygo.cryptoapp.algorithmes.Playfair;
import com.example.brygo.cryptoapp.algorithmes.TranspoRect;
import com.example.brygo.cryptoapp.algorithmes.Vigenere;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner mySpinner;
    EditText clearText;
    EditText codedText;
    EditText keyText;
    Button cryptButton;
    Button decryptButton;
    Button clearButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySpinner = findViewById(R.id.spinner);
        clearText = findViewById(R.id.clearText);
        codedText = findViewById(R.id.codedText);
        keyText = findViewById(R.id.keyText);
        cryptButton = findViewById(R.id.buttonCrypt);
        decryptButton = findViewById(R.id.buttonDecrypt);
        clearButton = findViewById(R.id.buttonClear);

        cryptButton.setOnClickListener(this);
        decryptButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        //Lors de la selection d'un algo on appelle changeKeyUsage qui permet d'afficher
        // un "hint" qui sert d'indication sur la syntaxe de la clé
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                changeKeyUsage(selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Sert à activer le hideKeyboard lorsque l'on touche a l'exterieur d'un editText
        Utils.setupUI(this, findViewById(R.id.relativeLayout));
    }

    //Change les indications lorsque l'on selectionne un algorithme
    public void changeKeyUsage(String selectedAlgo) {
        keyText.setEnabled(true);
        keyText.setText("");
        switch (selectedAlgo) {
            case "Atbash":
                keyText.setHint("Pas de clé requise");
                keyText.setEnabled(false);
                break;
            case "Cesar":
                keyText.setHint("<Décalage:Integer>");
                break;
            case "Vigenere":
            case "Playfair":
            case "Transposition rectangulaire":
                keyText.setHint("<Key:String>");
                break;
            case "HomophoniquePolybe":
                keyText.setHint("-CarrePolybe5x5: \n <ligne1-...-ligne5>\nou\n-Auto-génération: \n <InitialKey:String>");
                keyText.setText("vbkua-clxrd-mysfn-zogpi-hqewt");
                break;
            case "Delastelle":
                keyText.setHint("-CarrePolybe5x5: \n <ligne1-...-ligne5> <fragmentSize:Int>\nou\n-Auto-génération: \n <InitialKey:String> <fragmentSize:Int>");
                keyText.setText("vbkua-clxrd-mysfn-zogpi-hqewt 11");
                break;
            default:
                keyText.setHint("Clé");
                keyText.setEnabled(true);
                break;
        }
    }

    //Parse et récupère toutes les clés nécessaires à l'algo depuis le champ key
    // puis les renvoie dans une liste d'object + gestion d'erreurs
    public List<Object> getKeys(String selectedAlgo) {
        List<Object> keys = new ArrayList<Object>();
        String keyString = keyText.getText().toString();

        //le toast qui va servir à afficher des messages à l'écran
        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        if (keyString.isEmpty()) {
            Utils.showMessage(toast, "Le champ Key est vide !");
            return keys; //vide
        }

        //En fonction de l'algo on va parser l'entrée et en extraire ce qu'il nous faut
        switch (selectedAlgo) {
            case "Cesar":
                // Décalage:Int
                try {
                    int decalage = Integer.parseInt(keyString);
                    keys.add(decalage);
                } catch (Exception e) {
                    Utils.showMessage(toast, "Le champ Key est incorrect !");
                    keys.clear();
                }
                break;

            case "Vigenere":
            case "Playfair":
            case "Transposition rectangulaire":
                // Key:String
                keys.add(keyString);
                break;
            case "HomophoniquePolybe":
                List<Object> res = Utils.getCarrePolybeFromInput(keyString, toast);
                if (!res.isEmpty()) {
                    keys.add(res.get(0)); //ajout du booleen indiquant si on a un carré de polybe ou une clé
                    keys.add(res.get(1)); //si true ça sera un carré char[][] sinon ça sera une clé
                }
                break;
            case "Delastelle":
                String[] parts = keyString.split(" ");
                if (parts.length != 2) {
                    Utils.showMessage(toast, "Key syntax inccorect !");
                    break;
                }
                String str_polybe = parts[0];
                String str_fragmentSize = parts[1];

                res = Utils.getCarrePolybeFromInput(str_polybe, toast);
                if (!res.isEmpty()) {
                    keys.add(res.get(0)); //ajout du booleen indiquant si on a un carré de polybe ou une clé
                    keys.add(res.get(1)); //si true ça sera un carré sinon ça sera une clé
                } else break;

                //Si ok on essaie de parser l'entier fragmentSize
                try {
                    int fragmentSize = Integer.parseInt(str_fragmentSize);
                    keys.add(fragmentSize);
                } catch (Exception e) {
                    Utils.showMessage(toast, "Le champ <fragmentSize:Int> est incorrect !");
                    keys.clear();
                }
                break;
        }

        return keys;
    }

    //onClick est enclenché lors du click sur un bouton quelconque
    @Override
    public void onClick(View view) {
        String selectedAlgo = mySpinner.getSelectedItem().toString();
        String nonCryptedMsg = clearText.getText().toString();
        String cryptedMsg = codedText.getText().toString();
        List<Object> keys = null;

        Utils.hideKeyboard(this);
        boolean crypt=true;
        String msg="";
        String result = "";

        //En fonction du bouton cliqué on initialise les bonnes variables
        switch (view.getId()) {
            case  R.id.buttonCrypt: {
                crypt = true;
                msg = nonCryptedMsg;
                break;
            }
            case R.id.buttonDecrypt: {
                crypt = false;
                msg = cryptedMsg;
                break;
            }
            case R.id.buttonClear: {
                clearText.setText("");
                return; //n'execute pas la suite
            }
        }

        //Lance l'execution du bon algo en récupérant les bons paramètres grâce à notre getKeys
        switch (selectedAlgo) {
            case "Atbash":
                result = Atbash.execute(msg, crypt);
                break;
            case "Cesar":
                keys = getKeys(selectedAlgo);
                if (!keys.isEmpty())
                    result = Cesar.execute(msg, (int) keys.get(0), crypt);
                break;
            case "Vigenere":
                keys = getKeys(selectedAlgo);
                if (!keys.isEmpty())
                    result = Vigenere.execute(msg, (String) keys.get(0), crypt);
                break;
            case "Playfair":
                keys = getKeys(selectedAlgo);
                if (!keys.isEmpty())
                    result = Playfair.execute(msg, (String) keys.get(0), crypt);
                break;
            case "HomophoniquePolybe":
                keys = getKeys(selectedAlgo);
                if (!keys.isEmpty())
                    if ((boolean) keys.get(0) == true) //input = carrePolybe5x5
                        result = HomophoniquePolybe.execute(msg, (char[][]) keys.get(1), null, crypt);
                    else //input = Clé classique
                        result = HomophoniquePolybe.execute(msg, null, (String) keys.get(1), crypt);
                break;
            case "Transposition rectangulaire":
                keys = getKeys(selectedAlgo);
                if (!keys.isEmpty())
                    result = TranspoRect.execute(msg, (String) keys.get(0), crypt);
                break;
            case "Delastelle":
                keys = getKeys(selectedAlgo);
                if (!keys.isEmpty())
                    if ((boolean) keys.get(0) == true) //input = carrePolybe5x5
                        result = Delastelle.execute(msg, (char[][]) keys.get(1), null, (int) keys.get(2), crypt);
                    else //input = Clé classique
                        result = Delastelle.execute(msg, null, (String) keys.get(1), (int) keys.get(2), crypt);
                break;
        }

        //A la fin de l'execution on update la valeur du bon field.
        if (crypt) codedText.setText(result);
        else clearText.setText(result);
    }
}