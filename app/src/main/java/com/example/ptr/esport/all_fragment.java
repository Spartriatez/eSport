package com.example.ptr.esport;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Created by ptr on 07.09.17.
 */

public class all_fragment extends Fragment {
    DBSqlite sql;
    Spinner sp1,sp2;
    Button b1,b2;
    String month;
    String date_from;
    String date_to;
    int year;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_fragment,container,false);
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
        sql=new DBSqlite(getContext());
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp1=(Spinner)getActivity().findViewById(R.id.spinner1);
        sp2=(Spinner)getActivity().findViewById(R.id.spinner2);
        b1=(Button)getActivity().findViewById(R.id.zaladuj);
        b2=(Button)getActivity().findViewById(R.id.Clear);
        final Cursor res = sql.showNameUser();
        String dane = null;
        int id_u = 0;
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                dane = res.getString(0);
            }
        } else {
            Toast.makeText(getContext(), "brak danych", Toast.LENGTH_SHORT).show();
        }

        if (dane != null) {
            Cursor res2 = sql.getInfoUser(dane);
            while (res2.moveToNext()) {
                id_u = res2.getInt(0);
            }
            Log.d("username", String.valueOf(id_u));
        }
        Cursor cur10 = sql.getDate(id_u);
       String data=null;
        if (cur10.getCount() == 0) {
            Toast.makeText(getContext(),"Brak danych",Toast.LENGTH_SHORT).show();
        } else {
           while (cur10.moveToNext()) {
             data=cur10.getString(0);
            }
            SimpleDateFormat d1=new SimpleDateFormat("dd-LL-yyyy HH:mm");
            SimpleDateFormat d2=new SimpleDateFormat("LL");
            SimpleDateFormat d3=new SimpleDateFormat("yyyy");
            List<String> listmonth = new ArrayList<String>();
            List<String> listyear = new ArrayList<String>();
            String month=null;
            String year=null;
            try {
                Date dt1=d1.parse(data);
                month=d2.format(dt1);
                listmonth.add(month);
                year=d3.format(dt1);
                listyear.add(year);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, listmonth);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp1.setAdapter(dataAdapter);
                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, listyear);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp2.setAdapter(dataAdapter2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
           // Log.d("days of month", String.valueOf(daysInMonth(month,Integer.valueOf(year))));
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String dane2 = adapterView.getSelectedItem().toString();
                    setMonth(dane2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String dane2 = adapterView.getSelectedItem().toString();
                    setYear(dane2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     date_from="01:"+getMonth()+":"+String.valueOf(getYear())+" 00:00:00:000";
                     date_to=String.valueOf(daysInMonth(getMonth(),getYear()))+":"+getMonth()+":"+String.valueOf(getYear())+" 00:00:00:000";
                    //Log.d("MM:YY","From "+date_from+"to "+date_to);
/********************************************************************************/
                    final Cursor res = sql.showNameUser();
                    String dane = null;
                    int id_u = 0;
                    if (res.getCount() != 0) {
                        while (res.moveToNext()) {
                            dane = res.getString(0);
                        }
                    } else {
                        Toast.makeText(getContext(), "brak danych", Toast.LENGTH_SHORT).show();
                    }

                    if (dane != null) {
                        Cursor res2 = sql.getInfoUser(dane);
                        while (res2.moveToNext()) {
                            id_u = res2.getInt(0);
                        }
                        //Log.d("username", String.valueOf(id_u));
                    }

                    Log.d("dane",String.valueOf(sql.insFrTo(id_u,date_from,date_to)));
                    Cursor temp=sql.returnDateInMonth(id_u,date_to,date_to);
                    if(temp.getCount()!=0){
                        while (temp.moveToNext()) {
                            id_u = temp.getInt(0);
                            Log.d("ddddddddddddddd",String.valueOf(id_u));
                        }
                    }else{
                        Toast.makeText(getContext(), "Błąd ładowania", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("dane",String.valueOf(sql.delFrTo()));
                }
            });
        }

    }
    public void setMonth(String m){
        month=m;
    }
    public void setYear(String y){
        year=Integer.valueOf(y);
    }
    public String getMonth(){
        return month;
    }
    public int getYear(){
        return year;
    }

    private static boolean przestepny(int rok)
    {
        return ((rok % 4 == 0) && (rok % 100 != 0)) || (rok % 400 == 0);
    }
    public int daysInMonth(String month,int year){
        int days;
        switch(month){
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                days=31;
                break;
            case "02":{
                if(przestepny(year))
                    days=29;
                else
                    days=28;
                break;
            }
            default: days=30;
        }
        return days;
    }
}
