package com.example.administrator.readwritecontacts;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainFaceActivity extends Activity {

    List<String> listContact=new ArrayList<String>();

    private IntentFilter sendFilter;
    private  SendStatusReceiver sendStatusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_face);
        Button btnSend= (Button) findViewById(R.id.btnSend);
        Button btnEdit= (Button) findViewById(R.id.btnEdit);
        Button btnSave= (Button) findViewById(R.id.btnSave);
        final EditText editText= (EditText) findViewById(R.id.etText);

        sendFilter=new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver=new SendStatusReceiver();
        registerReceiver(sendStatusReceiver,sendFilter);

        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        editText.setText(pref.getString("editText","Help!"));
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
                SmsManager smsManager = SmsManager.getDefault();
                Intent sentIntent = new Intent("SENT_SMS_ACTION");
                PendingIntent pi=PendingIntent.getBroadcast(MainFaceActivity.this,0,sentIntent,0);
                for(String i:listContact) {
                    smsManager.sendTextMessage(
                            i,
                            null,
                            editText.getText().toString(),
                            pi,
                            PendingIntent.getBroadcast(MainFaceActivity.this, 0, sentIntent, 0));
                }

            }
        });
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

}
