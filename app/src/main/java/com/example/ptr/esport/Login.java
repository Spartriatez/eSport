package com.example.ptr.esport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {
    EditText ed1,ed2;
    DBSqlite database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=new DBSqlite(this);
        Cursor cur=database.showLogUser();
        String username=null;
        String password=null;
        if(cur.getCount()==0)
        {
            setContentView(R.layout.activity_login);
        }else
        {
            while (cur.moveToNext()){
                username=cur.getString(0);
                password=cur.getString(1);
            }
            boolean test=database.autologin(username,password);
            if(test==false) {
                Toast.makeText(Login.this, "Błąd logowania", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_login);
            }
            else
            {
                Toast.makeText(Login.this, "zalogowano", Toast.LENGTH_SHORT).show();
                Intent inten=new Intent(Login.this,eSportMenu.class);
                startActivity(inten);
            }
        }
        if(!runtime_permissions()){
            Intent i =new Intent(getApplicationContext(),GPS_Service.class);
            startService(i);
            Intent j =new Intent(getApplicationContext(),StopWatch_Service.class);
            startService(j);
            Intent k =new Intent(getApplicationContext(),StepsCounter.class);
            startService(k);
        }
    }
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
            } else {
                runtime_permissions();
            }
        }
    }
    public void click(View view) throws NoSuchAlgorithmException {
        switch(view.getId())
        {
            case R.id.Login:
            {
                ed1=(EditText)findViewById(R.id.log);
                ed2=(EditText)findViewById(R.id.passwd);
                Cursor res=database.logged(ed1.getText().toString(),ed2.getText().toString());
                boolean ins=database.insertUserLog(ed1.getText().toString(),ed2.getText().toString());
                if(ed1.getText().toString().equals("")||ed2.getText().toString().equals(""))
                    Toast.makeText(Login.this, "Wypelnij  poprawnie dane", Toast.LENGTH_SHORT).show();
                else if(res.getCount()==0)
                    Toast.makeText(Login.this, "Błąd logowania", Toast.LENGTH_SHORT).show();
                else{
                    if(ins==true){
                        Toast.makeText(Login.this, "Zalogowano", Toast.LENGTH_SHORT).show();
                        Intent inten=new Intent(Login.this,eSportMenu.class);
                        startActivity(inten);}
                    else
                        Toast.makeText(Login.this, "Błąd logowania nie wiadomo", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.Registry: {
                Intent inten=new Intent(Login.this,Rejestracja.class);
                startActivity(inten);
                break;
            }
        }
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
        if(res.getCount()==0) {
            showMessage("Error","Nic nie ma");
            return;
        }
        StringBuffer buffer=new StringBuffer();
        buffer.append(res);
        while(res.moveToNext())
        {
            buffer.append("Username : "+res.getString(0)+"\n");
            buffer.append("Password: "+res.getString(1)+"\n");
        }
        showMessage("Dane",buffer.toString());
    }
}
