package com.example.administrator.readwritecontacts;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainFaceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_face);
        Button btnSent= (Button) findViewById(R.id.btnSent);
        Button btnEdit= (Button) findViewById(R.id.btnEdit);
        EditText editText= (EditText) findViewById(R.id.etText);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainFaceActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

}
