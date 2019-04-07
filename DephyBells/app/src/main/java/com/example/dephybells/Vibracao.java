package com.example.dephybells;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Vibracao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibracao);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vibrações");

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        ListView listaConfig = (ListView) findViewById(R.id.lv_config);

        ArrayList<String> opcoes = listaOpcoesIniciais();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opcoes);
        listaConfig.setAdapter(arrayAdapter);
        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        listaConfig.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){


                    vibe.vibrate(new long[] { 0000, 3000 }, -1);

                }
                if (position == 1){
                    //vibe.vibrate(500);
                    vibe.vibrate(new long[] { 0000, 200, 100, 200, 100, 200, 100, 200, 100 }, -1);


                    // vibe.wait(300);
                    //vibe.vibrate(500);
                    // vibe.wait(300);
                   // vibe.vibrate(500);
                    // vibe.wait(300);
                   // vibe.vibrate(500);
                }
                if (position == 2){
                    vibe.vibrate(new long[] { 0000, 500, 500, 500, 500, 500, 500, 500, 500 }, -1);
                }
            }
        });
    }

    public ArrayList<String> listaOpcoesIniciais(){
        ArrayList<String> dados = new ArrayList<String>();
        dados.add("Constante");
        dados.add("Intermitente Rápida");
        dados.add("Intermitente Lenta");
        return dados;
    }


}
