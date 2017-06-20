package com.example.ptr.esport;

/**
 * Created by ptr on 13.05.17.
 */
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBSqlite extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "eSport.db";
    public static final String TABLE_NAME1="User";
    public static final String TABLE_NAME2="Treningi";
    public static final String TABLE_NAME3="Czasy";
    public static final String TABLE_NAME4="Logged";

    String sql = "create table IF NOT EXISTS " + TABLE_NAME1 + "(id_user INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,password TEXT,waga NUMERIC,wzrost NUMERIC,data_ur TEXT,plec TEXT)";
    String sql2 = "create table IF NOT EXISTS " + TABLE_NAME2 + "(id_tren INTEGER PRIMARY KEY AUTOINCREMENT,id_user INTEGER,sport TEXT,czas_sum NUMERIC,odl_sum NUMERIC,l_krok_sum NUMERIC,kalorie NUMERIC, data_wyk NUMERIC, FOREIGN KEY('id_user') REFERENCES " + TABLE_NAME1 + "(id_user))";
    String sql3 = "create table IF NOT EXISTS " + TABLE_NAME3 + "(id_tren INTEGER,wspolrzedne INTEGER,czas_gmt NUMERIC, odl NUMERIC,l_krok NUMERIC,predkosc INTEGER,datawykonania NUMERIC, FOREIGN KEY('id_tren') REFERENCES " + TABLE_NAME2 + "(id_tren))";
    String sql4="create table IF NOT EXISTS "+TABLE_NAME4+"(username TEXT,password TEXT)";

    public DBSqlite(Context context)
    {
      super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteStatement stmt=db.compileStatement(sql);
        stmt.execute();
        stmt=db.compileStatement(sql4);
        stmt.execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sq="Drop TABLE IF EXISTS "+TABLE_NAME1;
        String sq4="Drop TABLE IF EXISTS "+TABLE_NAME4;
        SQLiteStatement stmt=db.compileStatement(sq);
        stmt.execute();
        stmt=db.compileStatement(sq4);
        stmt.execute();
        onCreate(db);
    }
    public Cursor getAllData()
    {   SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from "+TABLE_NAME1;
        data=db.rawQuery(sql,null);
        return data;
    }

    public Cursor logged(String username,String password) throws NoSuchAlgorithmException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(password.getBytes(Charset.forName("US-ASCII")),0,password.length());
        byte[] magnitude = digest.digest();
        BigInteger bi = new BigInteger(1, magnitude);
        String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
           String sql = "select * from " + TABLE_NAME1 + " where username='" + username+"' and password='"+hash+"'";

           data = db.rawQuery(sql, null);
           return data;

    }
    public boolean autologin(String username,String password){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_NAME1 + " where username='"+username+"' and password='"+password+"'";
        Cursor res=db.rawQuery(sql,null);
        if(res.getCount()==0)
            return false;
        else
            return true;
    }

    public String showUsers(String username)
    {
        String result=null;
        try {
            String sq = "Select username from " + TABLE_NAME1 + " where username=?";
            SQLiteDatabase db = this.getWritableDatabase();
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1,username);
            result = statement.simpleQueryForString();
        }catch (Exception e)
        {
            Log.w("Exception:", e);
            return null;
        }
        return result;
    }

    public boolean insertDataUsers(String username,String password,String waga,String wzrost,String data_ur,String plec)
    {
        String sq="Insert into "+TABLE_NAME1+"(username,password,waga,wzrost,data_ur,plec) Values(?,?,?,?,?,?)";
        SQLiteDatabase db=this.getWritableDatabase();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            SQLiteStatement statement=db.compileStatement(sq);
            statement.clearBindings();
            statement.bindString(1,username);
            digest.update(password.getBytes(Charset.forName("US-ASCII")),0,password.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            statement.bindString(2,hash);
            statement.bindDouble(3,Double.parseDouble(waga));
            statement.bindDouble(4,Double.parseDouble(wzrost));
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
            Date date=ft.parse(data_ur);
            statement.bindString(5,ft.format(date));
            statement.bindString(6,plec);
            statement.execute();
            return true;
        }catch (Exception e)
        {
            Log.w("Exception:", e);
            return false;
        }
    }
    public boolean insertUserLog(String username,String password)
    {
        String sq="Insert into "+TABLE_NAME4+"(username,password) Values(?,?)";
        SQLiteDatabase db=this.getWritableDatabase();
        try{
            SQLiteStatement statement=db.compileStatement(sq);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            statement.clearBindings();
            digest.update(password.getBytes(Charset.forName("US-ASCII")),0,password.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            statement.bindString(1,username);
            statement.bindString(2,hash);
            statement.execute();
            return true;
        }catch (Exception e)
        {
            Log.w("Exception:", e);
            return false;
        }
    }

    public Cursor showLogUser()
    {   SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from " + TABLE_NAME4;
        data=db.rawQuery(sql,null);
        return data;
    }
    public boolean deleteUser()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME4;
            SQLiteDatabase db=this.getWritableDatabase();
            SQLiteStatement statement=db.compileStatement(sq);
            statement.execute();
            return true;
        }catch (Exception e)
        {
            Log.w("Exception:", e);
            return false;
        }

    }
}
