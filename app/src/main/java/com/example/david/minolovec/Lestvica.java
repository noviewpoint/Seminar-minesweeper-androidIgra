package com.example.david.minolovec;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Lestvica extends Activity implements View.OnClickListener
{
    String filename = "lestvica_";

    public ArrayList<JSONObject> rezultati = new ArrayList<>();

    Lestvica activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);

        this.activity = this;

        new Lestvica2(activity).execute();

        View x0 = findViewById(R.id.moji_highscores_button);
        x0.setOnClickListener(this);

        View x1 = findViewById(R.id.easy_highscores_button);
        x1.setOnClickListener(this);

        View x2 = findViewById(R.id.medium_highscores_button);
        x2.setOnClickListener(this);

        View x3 = findViewById(R.id.hard_highscores_button);
        x3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.moji_highscores_button:
                izpisiRezultate(0);
                break;
            case R.id.easy_highscores_button:
                izpisiRezultate(1);
                break;
            case R.id.medium_highscores_button:
                izpisiRezultate(2);
                break;
            case R.id.hard_highscores_button:
                izpisiRezultate(3);
                break;
            default:
                break;
        }
    }

    /*protected void onStart() {
        //super.onStart();
        // prikazi highscores
        izpisiHighscores("10");
    }*/

    public void izpisiRezultate(Integer x) {

        //WebView ble = (WebView) findViewById(R.id.ble);
        WebView ble = (WebView) new WebView(this);
        setContentView(ble);

        // naredi transparentni web view
        ble.getSettings();
        ble.setBackgroundColor(0x00000);

        String styles_table = "table {background-color: white; border: 1px solid black; width: 100%; text-align: center; margin: 0 auto;}th {font-size: 1.2em; text-align: center; padding-top: 7px; padding-bottom: 12px; background-color: rgb(123, 175, 43); color:white;}";
        String styles_th = "th {font-size: 1.2em; text-align: center; padding-top: 7px; padding-bottom: 12px; background-color: #A7C942; color:white;}";


        String izpisHTML = "<html><head><style>" + styles_table + "</style></head><body><table><tr><th>Name</th><th>Score</th><th>Country</th></tr>";

        int b = 0;

        for(int i = 0;(i < rezultati.size() && i < 10); i++)
        {
            try {
                String izpis;
                String ime;
                String dosezek;
                String drzava;
                String tezavnost = (String) rezultati.get(i).get("difficulty_scores");

                if (tezavnost.equals(x.toString())) {

                    ime = (String) rezultati.get(i).get("username_users");
                    dosezek = (String) rezultati.get(i).get("score_scores");
                    drzava = (String) rezultati.get(i).get("name_countries");

                    izpis = ime + dosezek + drzava;

                    // <img src='images/flags/".strtolower($row['flag']).".png' />
                    izpisHTML += "<tr><td>"+ime+"</td><td>"+dosezek+"</td><td>"+drzava+"</td></tr>";

                    b++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("izpis", "HTML je" + izpisHTML);
        izpisHTML += "</table></body>";
        ble.loadData(izpisHTML, "text/html; charset=utf-8", "utf-8");
    }


    //String izpisHTML = "";
    // rezultati.loadData(izpisHTML, "text/html", "utf-8");

    //String[] numberStrings = vsebina.split(",");

    //izpisHTML = "<body style='background-color: #C9C9C9;'>Tvoji rezultati za to zahtevnost so:<ul>";

       // izpisHTML += "<li>" + numberStrings[i] + " sekund</li>";


   // izpisHTML += "</ul></body>";

    //Log.d("this is my array", izpisHTML);

    //rezultati.loadData(izpisHTML, "text/html", "utf-8");

        /*CharSequence text = "Highscore so: " + vsebina;
        Toast.makeText(getApplicationContext(), text,
                Toast.LENGTH_SHORT).show();*/






    private String beriIzDatoteke(String ime){

        // ustvarimo vhodni podatkovni tok
        FileInputStream inputStream;

        //ugotovimo, koliko je velika datoteka
        File file = new File(getFilesDir(), ime);
        int length = (int) file.length();

        //pripravimo spremenljivko, v katero se bodo prebrali podatki
        byte[] bytes = new byte[length];

        //preberemo podatke
        try {
            inputStream = openFileInput(ime);
            inputStream.read(bytes);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //podatke pretvorimo iz polja bajtov v znakovni niz
        String vsebina = new String(bytes);

        return vsebina;
    }

    /*
    Metoda je poklicana preko povratnega klica iz enega izmed odjemalcev REST storitve, ko ta dobi odgovor od storitve
    * */
    public void odgovorStoritve(String odgovor) {

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(odgovor);


            if (jsonArray != null) {
                for (int i=0; i < jsonArray.length(); i++){
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    rezultati.add(explrObject);
                }
            }
        } catch (Exception e) {
            Log.d("izpis", "neki ni bilo v redu" + e);
        }

        // default izpise highscore za easy game
        izpisiRezultate(1);
    }

}
