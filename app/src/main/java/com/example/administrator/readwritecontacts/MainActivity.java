package com.example.administrator.readwritecontacts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.test.AndroidTestCase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends Activity {

    ListView listView;

    ContactAdapter adapter;

    List<Contact> contactsList = new ArrayList<Contact>(), chosenList=new ArrayList<Contact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= (ListView) findViewById(R.id.list_view);
        adapter=new ContactAdapter(this , R.layout.contacts_item, contactsList);
        listView.setAdapter(adapter);
        readContact();
        sortContact();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                if (checkBox.isChecked()) checkBox.setChecked(false);
                else checkBox.setChecked(true);
            }
        });
        Log.d("MainActivity", "onCreateFinished");
        Button btnReturn= (Button) findViewById(R.id.btnSubmit);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream out=null;
                BufferedWriter writer=null;
                try{
                    out=openFileOutput("data", Context.MODE_PRIVATE);
                    writer=new BufferedWriter(new OutputStreamWriter(out));
                    for(int i=0;i<listView.getChildCount();i++){
                        View childView=listView.getChildAt(i);
                        CheckBox checkBox= (CheckBox) childView.findViewById(R.id.checkbox);
                        String name= ((TextView) childView.findViewById(R.id.text_name)).getText().toString();
                        String number= ((TextView) childView.findViewById(R.id.text_number)).getText().toString();
                        if(checkBox.isChecked()){
                            writer.write(name+"\n"+number+"\n");
                        }

                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }finally{
                    if(writer!=null)
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
                Toast.makeText(MainActivity.this,"Save data succeed",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void readContact(){
        Cursor cursor =null;
        try{
            cursor=getContentResolver().query(
                   ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null
           );
           while(cursor.moveToNext()){
               String displayName=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
               String displayPhone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
               Contact newContact=new Contact(displayName,displayPhone);
               contactsList.add(newContact);
           }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null)
                cursor.close();
        }


        FileInputStream in=null;
        BufferedReader reader=null;
        try{
            in=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String name="",number="";
            while((name=reader.readLine())!=null){
                number=reader.readLine();
                for(Contact i:contactsList){
                    if(i.getName().equals(name)&&i.getNumber().equals(number)) {
                        i.isChecked = true;
                        break;
                    }
                }
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
    private void sortContact(){
        ComparatorContact comparatorContact=new ComparatorContact();
        Collections.sort(contactsList,comparatorContact);
    }

}
