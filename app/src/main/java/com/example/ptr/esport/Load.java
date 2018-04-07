package com.example.ptr.esport;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Load extends AppCompatActivity {
    Spinner sp1,sp2,sp3;
    DBSqlite database;
    String data=null;
    String data2=null;
    int length=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        sp1=(Spinner)findViewById(R.id.spin2);
        sp2=(Spinner)findViewById(R.id.spin3);
        sp3=(Spinner)findViewById(R.id.spin4);
        database=new DBSqlite(this);
        final List<String> list = new ArrayList<String>();
        final List<String> numberlist = new ArrayList<String>();
        numberlist.add("--brak--");
        numberlist.add("100 m");
        numberlist.add("200 m");
        numberlist.add("300 m");
        numberlist.add("500 m");
        list.add("Podstawowy");
        list.add("Rozszerzony");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(dataAdapter);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dane=adapterView.getSelectedItem().toString();
                //Toast.makeText(getBaseContext(),dane,Toast.LENGTH_LONG).show();
                switch(dane){
                    case "Podstawowy": dane="basic";
                        break;
                    case "Rozszerzony": dane="advantage";
                        break;
                    default: dane="basic";
                }
                setString(dane);
                Cursor cur=database.getFile(dane);
                List<String> listtemp = new ArrayList<String>();
                if(cur.getCount()==0)
                {
                    Toast.makeText(getBaseContext(),"brak danych",Toast.LENGTH_LONG).show();
                }else{

                    while (cur.moveToNext()){
                        listtemp.add(cur.getString(0));
                    }
                    ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_spinner_item, listtemp);
                    dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp2.setAdapter(dataAdapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dane2=adapterView.getSelectedItem().toString();
                setString2(dane2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, numberlist);
        dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp3.setAdapter(dataAdapter3);
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dane3=adapterView.getSelectedItem().toString();
                int ln=0;
                switch(dane3){
                    case "100 m": ln=100;
                        break;
                    case "200 m": ln=200;
                        break;
                    case "300 m": ln=300;
                        break;
                    case "500 m": ln=500;
                        break;
                    default: ln=0;
                }
                setlength(ln);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setString(String data){
        this.data=data;
    }
    public void setString2(String data2){
        this.data2=data2;
    }
    public void setlength(int length){
        this.length=length;
    }
    public void check(View view) throws IOException {
        boolean testload=database.isloaded();
        if(!testload) {
            boolean test = database.insertLoadFile(data2, data, length);
            if (test == false) {
                Toast.makeText(Load.this, "Błąd zapisu", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Load.this, "Zapisano", Toast.LENGTH_SHORT).show();
                Intent inten = new Intent(Load.this, eSportMenu.class);
                startActivity(inten);
            }
        }else{
            Toast.makeText(Load.this, "załadowano mape", Toast.LENGTH_SHORT).show();
        }
    }
}
