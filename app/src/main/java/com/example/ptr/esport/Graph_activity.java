package com.example.ptr.esport;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Graph_activity extends AppCompatActivity {
    private XYPlot plot,plot2,plot3,plot4;
    DBSqlite sql;
    Button b;
    int id_u=0;
    String data=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_activity);

        plot = (XYPlot)findViewById(R.id.plot5);
        plot2 = (XYPlot)findViewById(R.id.plot2);
        plot3 = (XYPlot)findViewById(R.id.plot3);
        plot4 = (XYPlot)findViewById(R.id.plot);
        b=(Button)findViewById(R.id.button6);
        sql=new DBSqlite(this);
        Cursor test = sql.isEmpty();
        List<Double> list1=new ArrayList<Double>();
        List<Double> list2=new ArrayList<Double>();
        List<Integer> time=new ArrayList<Integer>();
        List<Double> speed=new ArrayList<Double>();
        List<Double> calories=new ArrayList<Double>();
        if (test.getCount() != 0) {
            Cursor returnDate = sql.returnDateHistory();
            if (returnDate.getCount() != 0) {
                while (returnDate.moveToNext()) {
                    id_u = returnDate.getInt(0);
                    data = returnDate.getString(1);
                }
            }
            Cursor res = sql.getAllInfo(id_u,data);
            StringBuffer buffer=new StringBuffer();
            buffer.append(res);
            while(res.moveToNext())
            {
                list1.add(res.getDouble(0));
                list2.add(res.getDouble(1));
                Log.d("stoper",res.getString(2));
                time.add(convertTime(res.getString(2)));
                speed.add(res.getDouble(3));
                calories.add(res.getDouble(4));
            }
            Number[] xAxis=new Number[list2.size()];
            Number[] yAxis=new Number[list1.size()];
            Number[] xAxis2=new Number[time.size()];
            Number[] yAxis2=new Number[speed.size()];
            Number[] yAxis3=new Number[calories.size()];
            Number[] yAxis4=new Number[list2.size()];
            for(int i=0;i<list2.size();i++){
                yAxis4[i]=xAxis[i]=list2.get(i)*1000;

            }
            for(int j=0;j<list1.size();j++){
                yAxis[j]=list1.get(j);
            }

            for(int k=0;k<time.size();k++){
                xAxis2[k]=time.get(k);
            }

            for(int l=0;l<speed.size();l++){
                yAxis2[l]=speed.get(l);
            }
            for(int m=0;m<calories.size();m++){
                yAxis3[m]=calories.get(m);
            }
            /* wykres 1 */
            XYSeries series;
            series = new SimpleXYSeries( Arrays.asList(xAxis),Arrays.asList(yAxis), "Profil wysokości");
            LineAndPointFormatter series1Format =new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
            plot.addSeries(series, series1Format);
            plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = Math.round(((Number) obj).floatValue());
                    return toAppendTo.append(i);
                }
                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });

            /* wykres 2*/

            XYSeries series2;
            series2 = new SimpleXYSeries( Arrays.asList(xAxis2),Arrays.asList(yAxis2), "Prędkość");
            LineAndPointFormatter series2Format =new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
            series2Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
            plot2.addSeries(series2, series2Format);
            plot2.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = Math.round(((Number) obj).floatValue());
                    return toAppendTo.append(i);
                }
                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });

             /* wykres 3*/
            XYSeries series3;
            series3 = new SimpleXYSeries( Arrays.asList(xAxis2),Arrays.asList(yAxis3), "kalorie");
            LineAndPointFormatter series3Format =new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
            series3Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
            plot3.addSeries(series3, series3Format);
            plot3.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = Math.round(((Number) obj).floatValue());
                    return toAppendTo.append(i);
                }
                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });

            /* wykres 4*/
            XYSeries series4;
            series4 = new SimpleXYSeries( Arrays.asList(xAxis2),Arrays.asList(yAxis4), "dystans");
            LineAndPointFormatter series4Format =new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
            series3Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
            plot4.addSeries(series4, series4Format);
            plot4.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    int i = Math.round(((Number) obj).floatValue());
                    return toAppendTo.append(i);
                }
                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });
        }
    }
    public void getHistory(View view) {
        Intent inten=new Intent(Graph_activity.this,history_activity.class);
        startActivity(inten);
    }
    public int convertTime(String tm){
        int seconds=0;
        int result=0;
        int hour=0;
        int min=0;
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss:SSS");
        SimpleDateFormat f1=new SimpleDateFormat("HH");
        SimpleDateFormat f2=new SimpleDateFormat("mm");
        SimpleDateFormat f3= new SimpleDateFormat("ss");
        try {
            Date time=format.parse(tm);
            seconds=Integer.valueOf(f3.format(time));
            min=Integer.valueOf(f2.format(time));
            hour=Integer.valueOf(f1.format(time));
            result=(hour*3600)+(min*60)+seconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
