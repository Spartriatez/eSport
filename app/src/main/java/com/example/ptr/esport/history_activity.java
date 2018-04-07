package com.example.ptr.esport;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class history_activity extends AppCompatActivity {
    Spinner sp1;
    private BroadcastReceiver broadcastReceiver;
    String date=null;
    DBSqlite sql;
    Button b1,b2;
    TextView tx1,tx2,tx3,tx4,tx5,tx6,tx7,tx8,tx9;
    int id_u=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp1=(Spinner) findViewById(R.id.spinner3);
        b1=(Button)findViewById(R.id.loadInfo) ;
        b2=(Button)findViewById(R.id.clear) ;
        tx1=(TextView)findViewById(R.id.textView100);
        tx2=(TextView)findViewById(R.id.textView101);
        tx3=(TextView)findViewById(R.id.textView102);
        tx4=(TextView)findViewById(R.id.textView103);
        tx5=(TextView)findViewById(R.id.textView104);
        tx6=(TextView)findViewById(R.id.textView105);
        tx7=(TextView)findViewById(R.id.textView106);
        tx8=(TextView)findViewById(R.id.textView107);
        tx9=(TextView)findViewById(R.id.textView108);
        sql=new DBSqlite(this);

        final Cursor res = sql.showNameUser();
        String dane = null;

        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                dane = res.getString(0);
            }
        } else {
            Toast.makeText(this, "brak danych", Toast.LENGTH_SHORT).show();
        }

        if (dane != null) {
            Cursor res2 = sql.getInfoUser(dane);
            while (res2.moveToNext()) {
                id_u = res2.getInt(0);
            }
        }
        Cursor cur10 = sql.getDate(id_u);
        List<String> listtemp = new ArrayList<String>();
        if (cur10.getCount() == 0) {
            listtemp.add("brak danych");
            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, listtemp);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(dataAdapter2);
        } else {

            while (cur10.moveToNext()) {
                listtemp.add(cur10.getString(0));
            }
            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, listtemp);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(dataAdapter2);
        }
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String dane2 = adapterView.getSelectedItem().toString();
                save_date(dane2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Cursor test = sql.isEmpty();
        if (test.getCount() != 0) {
            Cursor returnDate = sql.returnDateHistory();
            if (returnDate.getCount() != 0) {
                while (returnDate.moveToNext()) {
                    id_u = returnDate.getInt(0);
                    save_date(returnDate.getString(1));
                }
            }
            AllDate();
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Cursor test = sql.isEmpty();
                if (test.getCount() == 0) {
                    if (returnDate() == null) {
                        Toast.makeText(getBaseContext(), "Nie wybrano daty", Toast.LENGTH_SHORT).show();
                    } else if (returnDate() == "brak danych") {
                        Toast.makeText(getBaseContext(), "Brak danych", Toast.LENGTH_SHORT).show();
                    } else {
                        final Cursor res = sql.showNameUser();
                        String dane = null;
                        int id_u = 0;
                        if (res.getCount() != 0) {
                            while (res.moveToNext()) {
                                dane = res.getString(0);
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "brak danych", Toast.LENGTH_SHORT).show();
                        }

                        if (dane != null) {
                            Cursor res2 = sql.getInfoUser(dane);
                            while (res2.moveToNext()) {
                                id_u = res2.getInt(0);
                            }
                        }
                        Log.d("decyzja ", String.valueOf(sql.setCheckDate(id_u, returnDate())));
                        AllDate();
                    }
                }else {
                    Toast.makeText(getBaseContext(),"Dane już istnieją",Toast.LENGTH_SHORT).show();
                }
            }

        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean del=sql.clear();
                if(del){
                    Toast.makeText(getBaseContext(),"Wyczyszczono",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getBaseContext(),"Błąd",Toast.LENGTH_SHORT).show();
                }
                tx1.setText("");
                tx2.setText("");
                tx3.setText("");
                tx4.setText("");
                tx5.setText("");
                tx6.setText("");
                tx7.setText("");
                tx8.setText("");
                tx9.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_activity, menu);
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
        }else if(id==R.id.map){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void getMenu(MenuItem item) {
        Intent inten=new Intent(history_activity.this,eSportMenu.class);
        startActivity(inten);
    }
    public void AllDate(){

            Cursor infoSport=sql.returnInfoSport(id_u,returnDate());
            //sport,czas_sum,odl_sum,l_krok_sum,kalorie,predkosc
            if(infoSport.getCount()!=0){
                while (infoSport.moveToNext()) {
                    tx1.setText(infoSport.getString(0));
                    tx2.setText(infoSport.getString(1));
                    tx3.setText(infoSport.getString(2));
                    tx9.setText(infoSport.getString(3));
                    tx8.setText(infoSport.getString(4));
                    tx4.setText(infoSport.getString(5));

                }
            }
            Cursor infoSport2=sql.returnInfoGroupSport(id_u,returnDate());
            //sport,czas_sum,odl_sum,l_krok_sum,kalorie,predkosc
            if(infoSport2.getCount()!=0){
                while (infoSport2.moveToNext()) {
                    tx5.setText(infoSport2.getString(0));
                    tx6.setText(infoSport2.getString(1));
                    tx7.setText(infoSport2.getString(2));
                }
            }
    }
    public void save_date(String d){
        date=d;
    }
    public String returnDate(){
        return date;
    }
//map
    public void getMap(MenuItem item) {
        Intent inten=new Intent(history_activity.this,History_Map.class);
        startActivity(inten);
    }

    public void getGraph(MenuItem item) {
        Intent inten=new Intent(history_activity.this,Graph_activity.class);
        startActivity(inten);
    }


}
