package com.example.licht.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.android.service.MqttService;

public class MainActivity extends AppCompatActivity implements MqttCallback{

    MqttClient mqttClient = null;
    String url = null;
    String cntId = null;
    String top = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.txt_activity_main_ueberschrift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    public void onClickConnect(final View sfNormal){
        // TODO
        // Auslesen der Textboxen und setzen der Variablen
        EditText broker = (EditText)findViewById(R.id.txt_broker_url);
        url = broker.getText().toString();
        EditText clientId = (EditText)findViewById(R.id.txt_clientId);
        cntId = clientId.getText().toString();
        // Popup bei OnClick
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Message")
        //        .setMessage("Connected to " + url)
        //        .setNeutralButton("OK", null);

        //AlertDialog dialog = builder.create();
        //dialog.show();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setKeepAliveInterval(30);
        options.setCleanSession(false);

        try {
            MemoryPersistence per = new MemoryPersistence();
            mqttClient = new MqttClient(url, cntId, per);
            mqttClient.setCallback(this);
            mqttClient.connect(options);
            //MqttMessage mqM = new MqttMessage();
            //mqM.setPayload("Kuchen".getBytes());
            //mqttClient.publish("kekse", mqM);
            //mqttClient.disconnect();

        } catch(MqttException mq) {
            mq.printStackTrace();
        }
    }

    public void onClickSubscribe(final View sfNormal){
        // TODO
        EditText topic = (EditText)findViewById(R.id.txt_topic);
        top = topic.getText().toString();

        try {
            mqttClient.subscribe(top);

        } catch (MqttException mq) {
            mq.printStackTrace();
        }

    }

    public void onClickUnsubscribe(final View sfNormal){
        // TODO
        EditText topic = (EditText)findViewById(R.id.txt_topic);
        top = topic.getText().toString();

        try {
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
        // komisches herausfinden der temperatur anhand von index abzählen
        final String str = message.toString().substring(message.toString().indexOf("hasValue")+9, message.toString().indexOf("hasValue")+12);
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //EditText bla = (EditText) findViewById(R.id.bla);
                //bla.setText(str);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(topic)
                        .setMessage(str)
                        .setNeutralButton("OK", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO Auto-generated method stub

    }
}
