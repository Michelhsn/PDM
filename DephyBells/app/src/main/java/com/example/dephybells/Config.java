package com.example.dephybells;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Config extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Configurações");

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
        listaConfig.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Intent intent = new Intent(getApplicationContext(), Vibracao.class);
                    startActivity(intent);
                } else if(position == 2){
                    try {
                        new MQTTClient(new MainActivity()).publishToMQTT();
                    } catch (Exception ex) {

                    }
                }
            }
        });
    }

    public ArrayList<String> listaOpcoesIniciais(){
        ArrayList<String> dados = new ArrayList<String>();
        dados.add("Vibração");
        dados.add("Iluminação");
        dados.add("Liga / Desliga");
        return dados;
    }

    public void telaConfiguracoes(View v){



    }

}
