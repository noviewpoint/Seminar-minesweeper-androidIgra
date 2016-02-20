package com.example.david.minolovec;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class Lestvica extends Activity implements View.OnClickListener
{
    public WebView rezultati;
    String filename = "lestvica_";
    public LinearLayout tabela;
    public TextView[] t;
    public LinearLayout.LayoutParams dim;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);
        //rezultati = (WebView) findViewById(R.id.rezultati);
        tabela = (LinearLayout)findViewById(R.id.tabelaRezultatov);

        t = new TextView[10];
        dim = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.TEXT_ALIGNMENT_GRAVITY);


        View x1 = findViewById(R.id.easy_highscores_button);
        x1.setOnClickListener(this);

        View x2 = findViewById(R.id.medium_highscores_button);
        x2.setOnClickListener(this);

        View x3 = findViewById(R.id.hard_highscores_button);
        x3.setOnClickListener(this);

        // defautl izpise highscore za easy game
        izpisiHighscores("10");
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.easy_highscores_button:
                izpisiHighscores("10");
                break;
            case R.id.medium_highscores_button:
                izpisiHighscores("40");
                break;
            case R.id.hard_highscores_button:
                izpisiHighscores("99");
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


    public void izpisiHighscores(String tezavnost) {

        // odstrani prejsnje childe
        if(((LinearLayout) tabela).getChildCount() > 0)
        {
            ((LinearLayout) tabela).removeAllViews();
        }

        String vsebina = beriIzDatoteke(filename + tezavnost);
        String izpisHTML = "";
        // izbrise prejsnje vrednosti
        //rezultati.loadData(izpisHTML, "text/html", "utf-8");
        //Log.d("this is my array", vsebina);

        String[] numberStrings = vsebina.split(",");

        izpisHTML = "<body style='background-color: #C9C9C9;'>Tvoji rezultati za to zahtevnost so:<ul>";

        for(int i = 0;(i < numberStrings.length && i < 10); i++)
        {
            izpisHTML += "<li>" + numberStrings[i] + " sekund</li>";

            if (!numberStrings[i].isEmpty())
            {
                t[i] = new TextView(this);
                t[i].setLayoutParams(dim);
                t[i].setText(numberStrings[i] + " sekund");
                t[i].setGravity(Gravity.CENTER);
                t[i].setBackgroundResource(R.color.gumbi);
                t[i].setTextColor(Color.BLACK);
                t[i].setTextSize(14);
                tabela.addView(t[i]);
            }
        }

        izpisHTML += "</ul></body>";

        //Log.d("this is my array", izpisHTML);

        //rezultati.loadData(izpisHTML, "text/html", "utf-8");

        /*CharSequence text = "Highscore so: " + vsebina;
        Toast.makeText(getApplicationContext(), text,
                Toast.LENGTH_SHORT).show();*/
    }

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
}
