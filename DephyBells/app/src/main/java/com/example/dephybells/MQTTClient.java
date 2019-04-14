package com.example.dephybells;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DateFormat;
import java.util.Date;

public class MQTTClient {

    private static final String TAG = "MQTTClient";
    private String mqttBroker = "tcp://iot.eclipse.org:1883";
    private String mqttTopic = "codifythings/dephybells";
    private String deviceId = "androidClient";
    private String mqttTopicLed = "codifythings/led";
    private String messageContent = "troca";
    private String deviceIdLed = "androidClientLed";
    private MainActivity activity = null;

    public MQTTClient(MainActivity activity) {
        this.activity = activity;
    }

    public void connectToMQTT() throws MqttException {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);


        MqttClient client = new MqttClient(mqttBroker, deviceId, new MemoryPersistence());
        client.connect(options);

        // Verificar o callback
        client.setCallback(new MqttEventCallback());

        // Assinatura
        client.subscribe(mqttTopic, 0);
    }


    // Avaliar tempo de request, tentar mover método
    public void publishToMQTT() throws MqttException {
        // clean session
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        MqttClient client = new MqttClient(mqttBroker, deviceIdLed,
                new MemoryPersistence());
        client.connect(options);
        // Publish message to topic
        Log.i(TAG, "Publishing to Topic");
        MqttMessage mqttMessage =
                new MqttMessage(messageContent.getBytes());

        // Ver impacto do qos
        mqttMessage.setQos(1);
        client.publish(mqttTopicLed, mqttMessage);

        // Verificar condição para disconect
        client.disconnect();
    }

    // Mensagem publicada no topic
    private class MqttEventCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable arg0) {
            // Do nothing
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            // Do nothing
        }

        @Override
        public void messageArrived(String topic, final MqttMessage msg) throws Exception {

            try {

                DateFormat df = DateFormat.getDateTimeInstance();
                String sensorMessage = new String(msg.getPayload()) + " acionada em " +
                        df.format(new Date());


                activity.createNotification("ALERTA DE CAMPAINHA!", sensorMessage);


                activity.updateView(sensorMessage);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }


    }
}