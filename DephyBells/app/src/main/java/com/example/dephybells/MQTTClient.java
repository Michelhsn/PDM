package com.example.dephybells;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MQTTClient extends Service implements MqttCallback {

    private static final String TAG = "MQTTClient";
    private String mqttBroker = "tcp://iot.eclipse.org:1883";
    private String mqttTopic = "codifythings/dephybells";
    private String deviceId = "androidClient";
    private String mqttTopicLed = "codifythings/led";
    private String messageContent = "troca";
    private String deviceIdLed = "androidClientLed";
    private MainActivity activity = null;
    private static final int 		MQTT_KEEP_ALIVE = 240000; // KeepAlive Interval in MS
    private static final String		MQTT_KEEP_ALIVE_TOPIC_FORAMT = "/users/%s/keepalive"; // Topic format for KeepAlives
    private static final byte[] 	MQTT_KEEP_ALIVE_MESSAGE = { 0 }; // Keep Alive message to send
    private static final int		MQTT_KEEP_ALIVE_QOS = 0; // Default Keepalive QOS
    private MqttTopic mKeepAliveTopic;			// Instance Variable for Keepalive topic
    private AlarmManager mAlarmManager;

    public MQTTClient() {
    }

    public MQTTClient(MainActivity activity) {
        this.activity = activity;

    }

    /*private void startKeepAlives() {
        Intent i = new Intent();
        i.setClass(this, MqttService.class);
        i.setAction("a" + ".KEEP_ALIVE");
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + MQTT_KEEP_ALIVE,
                MQTT_KEEP_ALIVE, pi);
    }

    private synchronized MqttDeliveryToken sendKeepAlive()
            throws  MqttPersistenceException, MqttException {


        MqttClient client = new MqttClient(mqttBroker, deviceId, new MemoryPersistence());
        if(mKeepAliveTopic == null) {
            mKeepAliveTopic = client.getTopic(
                    String.format(Locale.US, MQTT_KEEP_ALIVE_TOPIC_FORAMT,deviceId));
        }


        MqttMessage message = new MqttMessage(MQTT_KEEP_ALIVE_MESSAGE);
        message.setQos(MQTT_KEEP_ALIVE_QOS);

        return mKeepAliveTopic.publish(message);
    }
*/


    @Override
    public void onCreate() {
        super.onCreate();
        //startKeepAlives();
        try {
            connectToMQTT();
            //createNotification("ALERTA DE CAMPAINHA!", "create");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //String action = intent.getAction();
        try {
            /*if (action == null){

            }
            else if (action.equals("a" + ".KEEP_ALIVE")){
                sendKeepAlive();
            }*/
            connectToMQTT();

        } catch (MqttException e) {
            e.printStackTrace();
        }
        //createNotification("ALERTA DE CAMPAINHA!", "create");
        Log.i("a","  command");
        //intent.setAction("a" + ".KEEP_ALIVE");

        return START_STICKY;
    }

    public void connectToMQTT() throws MqttException {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);


        MqttClient client = new MqttClient(mqttBroker, deviceId, new MemoryPersistence());
        client.connect(options);

        // Verificar o callback
        client.setCallback(this);
        // Assinatura
        client.subscribe(mqttTopic, 0);


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        //activity.createNotification("ALERTA DE CAMPAINHA!", "a");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        try {

            DateFormat df = DateFormat.getDateTimeInstance();
            String dia = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String horario = new SimpleDateFormat("HH:mm:ss").format(new Date());

            final String sensorMessage = new String(mqttMessage.getPayload()) + " acionada em " +
                    dia + " às " + horario;


            createNotification("ALERTA DE CAMPAINHA!", sensorMessage);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("mensagem", sensorMessage);
            startActivity(intent);



           // activity.updateView(sensorMessage);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        activity.createNotification("ALERTA DE CAMPAINHA!", "a");

    }

    public void createNotification(String notificationTitle,
                                   String notificationMessage) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.bell_icon)
                            .setContentTitle(notificationTitle)
                            .setDefaults(Notification.DEFAULT_SOUND
                                    | Notification.DEFAULT_VIBRATE
                                    | Notification.DEFAULT_LIGHTS)
                            .setContentText(notificationMessage).setLights(0xff00ff00, 300, 100);


            // Ver forma melhor de usar vibrate
            SharedPreferences pref = getApplicationContext().getSharedPreferences("vibracaoPref", MODE_WORLD_READABLE); // 0 - for private mode
            Integer tipoVibracao = pref.getInt("vibracaoAtual", -1);
            if (tipoVibracao == 0){
                mBuilder.setVibrate(new long[] { 0000, 3000 });
            } else if (tipoVibracao == 1) {
                mBuilder.setVibrate(new long[]{0000, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100, 200, 100});
            } else if (tipoVibracao == 2){
                mBuilder.setVibrate(new long[] { 0000, 500, 500, 500, 500, 500, 500, 500, 500 });
            } else {
                mBuilder.setVibrate(new long[] { 0000, 500, 500, 500, 500, 500, 500, 500, 500 });
            }

            mBuilder.setLights(0xff00ff00, 300, 100);
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getApplicationContext(),
                    MainActivity.class);



            // The stack builder object will contain an artificial back
            // stack for the started Activity. This ensures that navigating
            // backward from the Activity leads out of your application to the
            // Home screen.
            // Voltar pra home no back
            TaskStackBuilder stackBuilder =
                    TaskStackBuilder.create(getApplicationContext());

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = new Notification();
        notif.ledARGB = 0xFFff0000;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 100;
        notif.ledOffMS = 100;

        mNotificationManager.notify(101, notif);
        // mId allows you to update the notification later on.
            mNotificationManager.notify(100, mBuilder.build());

    }




    // Mensagem publicada no topic
    /*private class MqttEventCallback implements MqttCallback {
        @Override
        public void connectionLost(Throwable arg0) {

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

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


    }*/
}
