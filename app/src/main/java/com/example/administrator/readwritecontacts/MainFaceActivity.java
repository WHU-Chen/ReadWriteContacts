package com.example.administrator.readwritecontacts;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainFaceActivity extends Activity {

    List<String> listContact=new ArrayList<String>();

    private TextView locationText;
    private Location lastLocation;
    private IntentFilter sendFilter;
    private  SendStatusReceiver sendStatusReceiver;
    private EditText editText;
    public LocationManager getLocationManager() {
        return locationManager;
    }

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_face);
        Button btnSend= (Button) findViewById(R.id.btnSend);
        Button btnEdit= (Button) findViewById(R.id.btnEdit);
        Button btnSave= (Button) findViewById(R.id.btnSave);
        locationText= (TextView) findViewById(R.id.locationText);
        lastLocation=null;
        editText= (EditText) findViewById(R.id.etText);

        initialLocation();

        sendFilter=new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver=new SendStatusReceiver();
        registerReceiver(sendStatusReceiver,sendFilter);

        LocationManager locationManager;

        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        editText.setText(pref.getString("editText",getString(R.string.initialSms)));
        readContacts();
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainFaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                String text=editText.getText().toString();
                editor.putString("editText",text);
                editor.commit();
                Toast.makeText(MainFaceActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });
    }

    public void sendSMS() {
        String location="";
        lastLocation=getLocationManager().getLastKnownLocation(provider);
        if(lastLocation!=null){
            location="\n我的位置：纬度"+lastLocation.getLatitude()+" 经度"+lastLocation.getLongitude();
            Log.d("MainFaceActivity","获取到位置信息");
        }else Log.d("MainFaceActivity","未获取到位置信息");
        SmsManager smsManager = SmsManager.getDefault();
        Intent sentIntent = new Intent("SENT_SMS_ACTION");
        PendingIntent pi=PendingIntent.getBroadcast(MainFaceActivity.this,0,sentIntent,0);
        for(String i:listContact) {
            smsManager.sendTextMessage(
                    i,
                    null,
                    editText.getText().toString()+location,
                    pi,
                    PendingIntent.getBroadcast(MainFaceActivity.this, 0, sentIntent, 0));
        }

    }

    private  void initialLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providersList=locationManager.getProviders(true);
        if(providersList.contains(LocationManager.GPS_PROVIDER)){
            provider=LocationManager.GPS_PROVIDER;
            Log.d("MainFaceActivity","获得GPS_PROVIDER");
        }else if(providersList.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
            Log.d("MainFaceActivity","获得NETWORK_PROVIDER");
        }else {
            Toast.makeText(this,"No location provider",Toast.LENGTH_SHORT).show();
            Log.d("MainFaceActivity", "未获取PROVIDER");
            provider=null;
        }
        Location location=locationManager.getLastKnownLocation(provider);
        if(location!=null)
            locationText.setText(getLocationText(location));
        locationManager.requestLocationUpdates(provider,5000,10,locationListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        readContacts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(sendStatusReceiver);
        locationManager.removeUpdates(locationListener);
    }

    private void readContacts(){
        listContact.clear();
        FileInputStream in=null;
        BufferedReader reader=null;
        try{
            in=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String name="",number="";
            while((name=reader.readLine())!=null){
                number=reader.readLine();
                listContact.add(number);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader!=null){
                try{
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String name="to "+intent.getStringExtra("name");
            if(name.equals("to null"))name="";
            if(getResultCode()==RESULT_OK){
                Toast.makeText(context,"Sending "+name+" succeeded",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context,"Sending "+name+" failed",Toast.LENGTH_LONG).show();
            }
        }
    }

    LocationListener locationListener =new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            locationText.setText(getLocationText(location));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
            locationText.setText(getLocationText(locationManager.getLastKnownLocation(s)));
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private String getLocationText(Location location) {
        return new String("latitude:"+location.getLatitude()+"\nlongtitude:"+location.getLongitude());
    }
}
