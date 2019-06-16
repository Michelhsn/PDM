package com.example.dephybells;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class Config extends AppCompatActivity {
    private String mqttBroker = "tcp://iot.eclipse.org:1883";
    private String mqttTopicLed = "codifythings/led";
    private String messageContent = "desliga/liga";
    private String deviceIdLed = "androidClientLed";
    private MqttClient client = null;

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

        // Carregar no create do config
        try
        {
            // clean session
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client = new MqttClient(mqttBroker, deviceIdLed,
                    new MemoryPersistence());

            client.connect(options);
        }
        catch(Exception ex)
        {
        }



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
                        publishToMQTT();
                        // Mudança no tempo de resposta??????
                        //new MQTTClient(new MainActivity()).publishToMQTT();
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
        dados.add("Abrir/Fechar Porta");
        return dados;
    }

    public void telaConfiguracoes(View v){



    }

    public void publishToMQTT() throws MqttException {
        // clean session
        // evitar reconectar caso já esteja aberta
        if (client != null && client.isConnected()){
            MqttMessage mqttMessage =
                    new MqttMessage(messageContent.getBytes());

            // Ver impacto do qos
            mqttMessage.setQos(0);
            client.publish(mqttTopicLed, mqttMessage);

        } else{
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client = new MqttClient(mqttBroker, deviceIdLed,
                    new MemoryPersistence());
            client.connect(options);
            // Publish message to topic
            MqttMessage mqttMessage =
                    new MqttMessage(messageContent.getBytes());

            // Ver impacto do qos
            mqttMessage.setQos(0);
            client.publish(mqttTopicLed, mqttMessage);
        }


        // Verificar condição para disconect!!!
        //client.disconnect();
    }

}
