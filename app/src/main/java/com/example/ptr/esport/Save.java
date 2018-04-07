package com.example.ptr.esport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Save extends AppCompatActivity {
    EditText et1;
    String type;
    DBSqlite database;
    String json,json1,json2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        et1=(EditText)findViewById(R.id.et1save);
        Bundle bundle=getIntent().getExtras();
        json1=bundle.getString("route");
        Bundle bundle2=getIntent().getExtras();
        json2=bundle2.getString("route2");
        if(json1!=null){
            json=json1;
            type=eSportMap2.getType();
        }
        if(json2!=null){
            json=json2;
            type=eSportMap3.getType();
        }
        database=new DBSqlite(this);
    }
    public void gotomenu(View view) {
        json1="";
        json2="";
        json="";
        Intent inten = new Intent(Save.this,eSportMenu.class);
        startActivity(inten);
    }

    public void savemap(View view) throws IOException {
        String res=et1.getText().toString();
        if(res.isEmpty() || res==null){
            Toast.makeText(Save.this, "Podaj nazwe pliku", Toast.LENGTH_SHORT).show();
        }else if(database.isExist(res,type)){
            Toast.makeText(Save.this, "Nazwa juz istnieje", Toast.LENGTH_SHORT).show();
        }else{
            if(database.insertFilename(res,type)){
                String file=getCacheDir().getCanonicalPath();
                String dir=file+"/"+type+"_map";
                File f=new File(dir);
                if(!f.exists()){
                    f.mkdir();
                    Runtime.getRuntime().exec("chmod 777 " + dir);
                    Log.d("Message","Created directory");
                }else{
                    Log.d("Message","Directory exist");
                }
                File my_file=new File(dir,res+".json");
                FileOutputStream stream = new FileOutputStream(my_file);
                try {
                    stream.write(json.getBytes());
                    Runtime.getRuntime().exec("chmod 777 " + dir+"/"+res+".json");
                } finally {
                    stream.close();
                }
                Toast.makeText(Save.this, "Dane zostaly zapisane", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(Save.this,eSportMenu.class);
                startActivity(inten);
                type="";
                json="";
                json="";
            }else{
                Toast.makeText(Save.this, "Blad bazy danych", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
