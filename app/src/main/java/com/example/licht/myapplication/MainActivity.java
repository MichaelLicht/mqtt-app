package com.example.licht.myapplication;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements MqttCallback{

    MqttAndroidClient mqttClient = null;
    String url = null;
    String cntId = null;
    String top = null;
    int id = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.txt_activity_main_ueberschrift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        /*// Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Test").setContentText("Auch ein Test");
        // Erstellt explizites Intent
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickConnect(final View sfNormal) {
        // TODO
        // Auslesen der Textboxen und setzen der Variablen
        EditText broker = (EditText) findViewById(R.id.txt_broker_url);
        url = broker.getText().toString();
        EditText clientId = (EditText) findViewById(R.id.txt_clientId);
        cntId = clientId.getText().toString();
        // Setzen der Verbindungsoptionen
        MqttConnectOptions options = new MqttConnectOptions();
        options.setKeepAliveInterval(0);
        options.setCleanSession(false);

        try {
            //MemoryPersistence per = new MemoryPersistence();
            mqttClient = new MqttAndroidClient(this.getApplicationContext(), url, cntId);
            mqttClient.setCallback(this);
            //mqttClient.connect(options);
            IMqttToken token = mqttClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    // Connected
                    System.out.println("Connected");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    // Failure
                }
            });

        } catch (MqttException mq) {
            mq.printStackTrace();
        }
    }

    public void onClickSubscribe(final View sfNormal) {
        // TODO
        EditText topic = (EditText) findViewById(R.id.txt_topic);
        top = topic.getText().toString();

        try {
            IMqttToken subToken = mqttClient.subscribe(top, 1);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    // Subscribed
                    System.out.println("Subscribed yeee");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

                }
            });

        } catch (MqttException mq) {
            mq.printStackTrace();
        }

    }

    public void onClickUnsubscribe(final View sfNormal) {
        // TODO
        EditText topic = (EditText) findViewById(R.id.txt_topic);
        top = topic.getText().toString();

        try {
            IMqttToken unsubtoken = mqttClient.unsubscribe(top);
            unsubtoken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    // Unsubscribed
                    System.out.println("Unsubscribed yeee");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

                }
            });
            mqttClient.unsubscribe(top);
        } catch (MqttException mq) {
            mq.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        // TODO Auto-generated method stub

    }

    @Override
    public void messageArrived(final String topic, MqttMessage message)
            throws Exception {
        final String str = message.toString();
        System.out.println("Message:" + str);
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CharSequence cs = new String("Neue Nachricht");
                Toast.makeText(MainActivity.this, cs, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(topic)
                        .setMessage(str)
                        .setNeutralButton("OK", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Neue Nachricht").setContentText(str).setStyle(new NotificationCompat.BigTextStyle().bigText(str))/*.addAction(R.drawable.dismiss, getString(R.string.dismiss), piDismiss)*/;
        // Erweitertes Layout
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];

        inboxStyle.setBigContentTitle("Event tracker details:");
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        builder.setStyle(inboxStyle);

        // Erstellt explizites Intent
        /*Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);*/
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub
        System.out.println("KEKS");
    }
}
