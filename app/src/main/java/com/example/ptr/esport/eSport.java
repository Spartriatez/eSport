package com.example.ptr.esport;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class eSport extends AppCompatActivity {
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
            setContentView(R.layout.activity_e_sport);
        }else
        {
            while (cur.moveToNext()){
                username=cur.getString(0);
                password=cur.getString(1);
            }
            boolean test=database.autologin(username,password);
            if(test==false) {
                Toast.makeText(eSport.this, "Błąd logowania", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_e_sport);
            }
            else
            {
                Toast.makeText(eSport.this, "zalogowano", Toast.LENGTH_SHORT).show();
                Intent inten=new Intent(eSport.this,option.class);
                startActivity(inten);
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
                    Toast.makeText(eSport.this, "Wypelnij  poprawnie dane", Toast.LENGTH_SHORT).show();
                else if(res.getCount()==0)
                    Toast.makeText(eSport.this, "Błąd logowania", Toast.LENGTH_SHORT).show();
                else{
                        if(ins==true){
                            Toast.makeText(eSport.this, "Zalogowano", Toast.LENGTH_SHORT).show();
                            Intent inten=new Intent(eSport.this,option.class);
                            startActivity(inten);}
                        else
                            Toast.makeText(eSport.this, "Błąd logowania nie wiadomo", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.Registry: {
                Intent inten=new Intent(eSport.this,Rejestracja.class);
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

    /**
     * Created by ptr on 12.05.17.
     */


}
