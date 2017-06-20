package com.example.ptr.esport;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
public class option extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText et1,et2,et3,et4,et5,et6;
    Spinner sp1;
    DBSqlite database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        et1=(EditText)findViewById(R.id.edt1);
        et1.setEnabled(false);
        sp1=(Spinner)findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Rolki");
        list.add("Rower");
        list.add("Bieg");
        list.add("Chód");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(dataAdapter);

        et3=(EditText)findViewById(R.id.edt3);
        et3.setEnabled(false);
        et4=(EditText)findViewById(R.id.edt4);
        et4.setEnabled(false);
        et5=(EditText)findViewById(R.id.edt5);
        et5.setEnabled(false);
        et6=(EditText)findViewById(R.id.edt6);
        et6.setEnabled(false);

        database=new DBSqlite(this);
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
        getMenuInflater().inflate(R.menu.option, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_sendf) {

        }else if (id == R.id.nav_sends) {

        }else if (id == R.id.nav_sendi) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clicked(MenuItem item) {
        boolean test=database.deleteUser();
        if(test==true) {
            Toast.makeText(option.this, "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
            Intent inten = new Intent(option.this, eSport.class);
            startActivity(inten);
        }else
            Toast.makeText(option.this, "błąd wylogowania", Toast.LENGTH_SHORT).show();
    }
    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clk(View view) {
        Cursor res = database.showLogUser();
        StringBuffer buffer=new StringBuffer();
        buffer.append(res);
        while(res.moveToNext())
        {
            buffer.append("Username : "+res.getString(0)+"\n");
            buffer.append("Password: "+res.getString(1)+"\n");
        }
        showMessage("Dane",buffer.toString());
    }

    public void MapClick(View view) {
        Intent inten=new Intent(option.this,eSportMap.class);
        startActivity(inten);
    }
}
