package com.example.administrator.readwritecontacts;

import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_face);
        Button btnSend= (Button) findViewById(R.id.btnSend);
        Button btnEdit= (Button) findViewById(R.id.btnEdit);
        Button btnSave= (Button) findViewById(R.id.btnSave);
        final EditText editText= (EditText) findViewById(R.id.etText);
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
                for(String i:listContact) {
                    smsManager.sendTextMessage(
                            i,
                            null,
                            editText.getText().toString(),
                            null,
                            null);
                }
            }
        });
    }

    private void readContacts(){
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

}
