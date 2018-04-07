package com.example.ptr.esport;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ptr on 03.09.17.
 */

public class DBSqlite extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "eSport.db";
    public static final String TABLE_NAME1="User";
    public static final String TABLE_NAME2="Treningi";
    public static final String TABLE_NAME3="Czasy";
    public static final String TABLE_NAME10="save_localization";
    public static final String TABLE_NAME4="Logged";
    public static final String TABLE_NAME5="Save_file";
    public static final String TABLE_NAME6="Loading_file";
    public static final String TABLE_NAME7="Save_GPS";
    public static final String TABLE_NAME8="IS_RUNNING";
    public static final String TABLE_NAME9="temp_timer";
    public static final String TABLE_NAME11="temp_one_date";
    public static final String TABLE_NAME12="temp_month";
    String sql = "create table IF NOT EXISTS " + TABLE_NAME1 + "(id_user INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,password TEXT,waga NUMERIC,wzrost NUMERIC,data_ur TEXT,plec TEXT)";
    String sql2 = "create table IF NOT EXISTS " + TABLE_NAME2 + "(id_user INTEGER,sport TEXT,czas_sum TEXT,odl_sum NUMERIC,l_krok_sum NUMERIC,kalorie NUMERIC,predkosc NUMEIC, data_wyk TEXT)";
    String sql3 = "create table IF NOT EXISTS " + TABLE_NAME3 + "(id_user INTEGER,npm NUMERIC,stoper NUMERIC, odl NUMERIC,sum_cal NUMERIC,predkosc NUMERIC,data_wyk TEXT)";
    // is login
    String sql4="create table IF NOT EXISTS "+TABLE_NAME4+"(username TEXT,password TEXT)";
    //save files
    String sql5="create table IF NOT EXISTS "+TABLE_NAME5+"(id INTEGER PRIMARY KEY AUTOINCREMENT,filename TEXT,type TEXT)";
    String sql6="create table IF NOT EXISTS "+TABLE_NAME6+"(filename TEXT,type TEXT,control_point NUMERIC)";
    //save route
    String sql7="create table IF NOT EXISTS "+TABLE_NAME7+"(id INTEGER PRIMARY KEY AUTOINCREMENT,latitude REAL,longitude REAL,npm NUMERIC)";
    //is running application
    String sql8="create table IF NOT EXISTS "+TABLE_NAME8+"(id INTEGER PRIMARY KEY AUTOINCREMENT,Sport TEXT,ktory NUMERIC)";
    String sql9 = "create table IF NOT EXISTS " + TABLE_NAME9 + "(npm NUMERIC,stoper TEXT, odl NUMERIC,sum_cal NUMERIC,predkosc NUMERIC)";
    String sql10="create table IF NOT EXISTS "+TABLE_NAME10+"(id_user INTEGER,latitude REAL,longitude REAL,npm NUMERIC,data NUMERIC)";
    String sql11="create table IF NOT EXISTS "+TABLE_NAME11+"(id_user INTEGER,data Text)";
    String sql12="create table IF NOT EXISTS "+TABLE_NAME12+"(id_user INTEGER,date_from Text,date_to Text)";
    public DBSqlite(Context context)
    {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteStatement stmt=db.compileStatement(sql);
        stmt.execute();
        stmt=db.compileStatement(sql2);
        stmt.execute();
        stmt=db.compileStatement(sql3);
        stmt.execute();
        stmt=db.compileStatement(sql4);
        stmt.execute();
        stmt=db.compileStatement(sql5);
        stmt.execute();
        stmt=db.compileStatement(sql6);
        stmt.execute();
        stmt=db.compileStatement(sql7);
        stmt.execute();
        stmt=db.compileStatement(sql8);
        stmt.execute();
        stmt=db.compileStatement(sql9);
        stmt.execute();
        stmt=db.compileStatement(sql10);
        stmt.execute();
        stmt=db.compileStatement(sql11);
        stmt.execute();
        stmt=db.compileStatement(sql12);
        stmt.execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sq="Drop TABLE IF EXISTS "+TABLE_NAME1;
        String sq2="Drop TABLE IF EXISTS "+TABLE_NAME2;
        String sq3="Drop TABLE IF EXISTS "+TABLE_NAME3;
        String sq4="Drop TABLE IF EXISTS "+TABLE_NAME4;
        String sq5="Drop TABLE IF EXISTS "+TABLE_NAME5;
        String sq6="Drop TABLE IF EXISTS "+TABLE_NAME6;
        String sq7="Drop TABLE IF EXISTS "+TABLE_NAME7;
        String sq8="Drop TABLE IF EXISTS "+TABLE_NAME8;
        String sq9="Drop TABLE IF EXISTS "+TABLE_NAME9;
        String sq10="Drop TABLE IF EXISTS "+TABLE_NAME10;
        String sq11="Drop TABLE IF EXISTS "+TABLE_NAME11;
        String sq12="Drop TABLE IF EXISTS "+TABLE_NAME12;
        SQLiteStatement stmt=db.compileStatement(sq);
        stmt.execute();
        stmt=db.compileStatement(sq2);
        stmt.execute();
        stmt=db.compileStatement(sq3);
        stmt.execute();
        stmt=db.compileStatement(sq4);
        stmt.execute();
        stmt=db.compileStatement(sq5);
        stmt.execute();
        stmt=db.compileStatement(sq6);
        stmt.execute();
        stmt=db.compileStatement(sq7);
        stmt.execute();
        stmt=db.compileStatement(sq8);
        stmt.execute();
        stmt=db.compileStatement(sq9);
        stmt.execute();
        stmt=db.compileStatement(sq10);
        stmt.execute();
        stmt=db.compileStatement(sq11);
        stmt.execute();
        stmt=db.compileStatement(sq12);
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
    public boolean isExist(String filename,String type){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_NAME5 + " where filename='"+filename+"' and type='"+type+"'";
        Cursor res=db.rawQuery(sql,null);
        while(res.moveToNext())
        {
            Log.d("str1"," Id: "+res.getString(0)+"\n");
            Log.d("str2","Filename : "+res.getString(1)+"\n");
            Log.d("str3","type: "+res.getString(2)+"\n");

        }
        if(res.getCount()==0)
            return false;
        else
            return true;
    }
    public boolean insertFilename(String filename,String type) {
        String sq = "Insert into " + TABLE_NAME5 + "(filename,type) Values(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1, filename);
            statement.bindString(2, type);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public Cursor getFile(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT filename FROM "+TABLE_NAME5+" WHERE type='"+type+"'";

        Cursor res=db.rawQuery(sql,null);
        return res;
    }
    public boolean insertLoadFile(String filename,String type,int length){
        String sq = "Insert into " + TABLE_NAME6 + "(filename,type,control_point) Values(?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1, filename);
            statement.bindString(2, type);
            statement.bindDouble(3,length);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public boolean isloaded(){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_NAME6;
        Cursor res=db.rawQuery(sql,null);
        if(res.getCount()==0)
            return false;
        else
            return true;
    }
    public Cursor getLoadFile(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT * FROM "+TABLE_NAME6;

        Cursor res=db.rawQuery(sql,null);
        return res;
    }
    public boolean deleteFile()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME6;
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
    public boolean insertLocalization(double latitude,double longitude,double npm){
        String sq = "Insert into " + TABLE_NAME7 + "(latitude,longitude,npm) Values(?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindDouble(1,latitude);
            statement.bindDouble(2,longitude);
            Calendar c = Calendar.getInstance();
            statement.bindDouble(3,npm);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public Cursor loadLocalization(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT latitude,longitude FROM "+TABLE_NAME7;

        Cursor res=db.rawQuery(sql,null);
        return res;
    }
    public Cursor getAllLocalization(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT * FROM "+TABLE_NAME7;

        Cursor res=db.rawQuery(sql,null);
        return res;
    }
    public boolean deleteAllLoc()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME7;
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
    public boolean insertIsRunning(String sport, int ktory){
        String sq = "Insert into " + TABLE_NAME8 + "(Sport,ktory) Values(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindString(1,sport);
            statement.bindLong(2,ktory);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public boolean isRunning(){
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "select * from " + TABLE_NAME8;
        Cursor res=db.rawQuery(sql,null);
        if(res.getCount()==0)
            return false;
        else
            return true;
    }
    public Cursor getSport(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT Sport,ktory FROM "+TABLE_NAME8;

        Cursor res=db.rawQuery(sql,null);
        return res;
    }
    public boolean deleteIsRunning()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME8;
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
    public Cursor showNameUser()
    {   SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select username from " + TABLE_NAME4;
        data=db.rawQuery(sql,null);
        return data;
    }

    public Cursor getInfoUser(String username){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select id_user,waga,wzrost,data_ur,plec from " + TABLE_NAME1+ " where username='"+username+"'";
        data=db.rawQuery(sql,null);
        return data;
    }
    //npm NUMERIC,stoper NUMERIC, odl NUMERIC,l_krok NUMERIC,predkosc NUMERIC
    public boolean insertSport(double  npm,String stoper,double odl,double sum_cal,double predkosc) {
        String sq = "Insert into " + TABLE_NAME9 + "(npm,stoper,odl,sum_cal,predkosc) Values(?,?,?,?,?)";
        Log.d("dane","weszlo");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement=db.compileStatement(sq);
            statement.bindDouble(1,npm);
            statement.bindString(2,stoper);
            statement.bindDouble(3,odl);
            statement.bindDouble(4,sum_cal);
            statement.bindDouble(5,predkosc);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public Cursor selectAllData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from " + TABLE_NAME9;
        data=db.rawQuery(sql,null);
        return data;
    }
    public boolean deleteAll()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME9;
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
// String sql2 = "create table IF NOT EXISTS " + TABLE_NAME2 + "(id_tren INTEGER PRIMARY KEY AUTOINCREMENT,id_user INTEGER,sport TEXT,czas_sum NUMERIC,odl_sum NUMERIC,l_krok_sum NUMERIC,kalorie NUMERIC,predkosc NUMEIC, data_wyk TEXT, FOREIGN KEY('id_user') REFERENCES " + TABLE_NAME1 + "(id_user))";

    public boolean insertAllSport(int id_u,String sport,String czas,String odl_sum,String l_krok,String kalorie, String predkosc,String data) {
        String sq = "Insert into " + TABLE_NAME2 + "(id_user,sport,czas_sum,odl_sum,l_krok_sum,kalorie,predkosc, data_wyk) Values(?,?,?,?,?,?,?,?)";
        Log.d("dane","weszlo");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement=db.compileStatement(sq);
            statement.bindLong(1,id_u);
            statement.bindString(2,sport);
            statement.bindString(3,czas);
            statement.bindDouble(4,Double.valueOf(odl_sum));
            statement.bindDouble(5,Double.valueOf(l_krok));
            statement.bindDouble(6,Double.valueOf(kalorie));
            statement.bindDouble(7,Double.valueOf(predkosc));
            statement.bindString(8,data);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    //"(id_user INTEGER,npm NUMERIC,stoper NUMERIC, odl NUMERIC,sum_cal NUMERIC,predkosc NUMERIC,data_wyk TEXT)";

    public boolean insertSportStorage(int id_user,double  npm,String stoper,double odl,double sum_cal,double predkosc,String data) {
        String sq = "Insert into " + TABLE_NAME3 + "(id_user,npm,stoper,odl,sum_cal,predkosc,data_wyk) Values(?,?,?,?,?,?,?)";
        Log.d("dane","weszlo");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement=db.compileStatement(sq);
            statement.bindLong(1,id_user);
            statement.bindDouble(2,npm);
            statement.bindString(3,stoper);
            statement.bindDouble(4,odl);
            statement.bindDouble(5,sum_cal);
            statement.bindDouble(6,predkosc);
            statement.bindString(7,data);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }

    public boolean insertLocalizationStorage(int id_user,double latitude,double longitude,double npm,String data){
        String sq = "Insert into " + TABLE_NAME10 + "(id_user,latitude,longitude,npm,data) Values(?,?,?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindLong(1,id_user);
            statement.bindDouble(2,latitude);
            statement.bindDouble(3,longitude);
            Calendar c = Calendar.getInstance();
            statement.bindDouble(4,npm);
            statement.bindString(5,data);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public Cursor getDate(int id_user){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select data_wyk from " + TABLE_NAME2+" WHERE id_user="+id_user;
        data=db.rawQuery(sql,null);
        return data;
    }

   public boolean setCheckDate(int id_user,String data) {
        String sq = "Insert into " + TABLE_NAME11 + "(id_user,data) Values(?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindLong(1, id_user);
            statement.bindString(2, data);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public boolean clear()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME11;
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
    public Cursor isEmpty(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from " + TABLE_NAME11;
        data=db.rawQuery(sql,null);
        return data;
    }
    //sport TEXT,czas_sum TEXT,odl_sum NUMERIC,l_krok_sum NUMERIC,kalorie NUMERIC,predkosc NUMEIC,
    public Cursor returnInfoSport(int id_u,String date){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select sport,czas_sum,odl_sum,l_krok_sum,kalorie,predkosc from " + TABLE_NAME2+" WHERE id_user="+id_u+" AND data_wyk='"+date+"'";
        data=db.rawQuery(sql,null);
        return data;
    }
    public Cursor returnInfoGroupSport(int id_u,String date){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select AVG(predkosc), MAX(predkosc),MIN(predkosc) from " + TABLE_NAME3+" WHERE id_user="+id_u+" AND data_wyk='"+date+"' group by '"+date+"'";
        data=db.rawQuery(sql,null);
        return data;
    }

    public Cursor returnSaveMap(int id_u,String date){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select latitude,longitude from " + TABLE_NAME10+" WHERE id_user="+id_u+" AND data='"+date+"'";
        data=db.rawQuery(sql,null);
        return data;
    }
    public Cursor returnDateHistory(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select id_user,data from " + TABLE_NAME11;
        data=db.rawQuery(sql,null);
        return data;
    }
    //id_user INTEGER,date_from Text,date_to Text
    public boolean insFrTo(int id_u,String df,String dt){
        String sq="Insert into "+TABLE_NAME12+"(id_user,date_from,date_to) Values(?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            SQLiteStatement statement = db.compileStatement(sq);
            statement.bindLong(1,id_u);
            statement.bindString(2,df);
            statement.bindString(3,dt);
            statement.execute();
            return true;
        } catch (Exception e) {
            Log.w("Exception:", e);
            return false;
        }
    }
    public boolean delFrTo(){
        try {
            String sq="DELETE FROM "+TABLE_NAME12;
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

    public Cursor returnDateInMonth(int id_u,String df,String dt){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select id_user from " + TABLE_NAME2+" WHERE data_wyk>'"+df+"' and data_wyk<'"+dt+"' and id_user="+id_u;
        data=db.rawQuery(sql,null);
        return data;
    }

    //"(id_user INTEGER,npm NUMERIC,stoper NUMERIC, odl NUMERIC,sum_cal NUMERIC,predkosc NUMERIC,data_wyk TEXT)";
    public Cursor getAllInfo(int id_u,String data){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data2=null;
        String sql = "select npm,odl,stoper,predkosc,sum_cal from " + TABLE_NAME3+" WHERE id_user="+id_u+" AND data_wyk='"+data+"'";
        data2=db.rawQuery(sql,null);
        return data2;
    }
    //TABLE_NAME9
    public Cursor tempProfile(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data2=null;
        String sql = "select npm,odl from " + TABLE_NAME9;
        data2=db.rawQuery(sql,null);
        return data2;
    }
    public Cursor avgSpeed(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data2=null;
        String sql = "select avg(predkosc) from " + TABLE_NAME9;
        data2=db.rawQuery(sql,null);
        return data2;
    }
    /***************************/
    public Cursor checkAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from " + TABLE_NAME2;
        data=db.rawQuery(sql,null);
        return data;
    }

    public boolean deleteAllSp()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME2;
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
    public Cursor checkAll2(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from " + TABLE_NAME3;
        data=db.rawQuery(sql,null);
        return data;
    }

    public boolean deleteAllSp2()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME3;
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
    public Cursor checkAll3(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor data=null;
        String sql = "select * from " + TABLE_NAME10;
        data=db.rawQuery(sql,null);
        return data;
    }

    public boolean deleteAllSp3()
    {
        try {
            String sq="DELETE FROM "+TABLE_NAME10;
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
