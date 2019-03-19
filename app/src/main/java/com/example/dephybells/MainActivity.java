

package com.example.dephybells;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateView("");

        try
        {
            MQTTClient client = new MQTTClient(this);
            client.connectToMQTT();
        }
        catch(Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateView(String sensorMessage) {
        try {
            SharedPreferences sharedPref = getSharedPreferences(
                    "deohybells.PREFERENCE_FILE_KEY",
                    Context.MODE_PRIVATE);

            if (sensorMessage == null || sensorMessage == "") {
                sensorMessage = sharedPref.getString("lastSensorMessage",
                        "Nenhum Alerta");
            }

            final String tempSensorMessage = sensorMessage;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    TextView updatedField = (TextView)
                            findViewById(R.id.updated_field);
                    updatedField.setText(tempSensorMessage);
                }
            });

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lastSensorMessage", sensorMessage);
            editor.commit();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


    public void createNotification(String notificationTitle,
                                   String notificationMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.bell_icon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage);


        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getApplicationContext(),
                MainActivity.class);

        // The stack builder object will contain an artificial back
        // stack for the started Activity. This ensures that navigating
        // backward from the Activity leads out of your application to the
        // Home screen.
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

        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }
}
