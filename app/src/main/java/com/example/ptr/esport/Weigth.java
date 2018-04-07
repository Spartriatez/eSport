package com.example.ptr.esport;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Weigth extends AppCompatActivity {
    DBSqlite sql;
    private XYPlot plot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weigth);
        sql=new DBSqlite(this);
        Cursor res=sql.tempProfile();
        plot = (XYPlot)findViewById(R.id.plot);
        List<Double> list1=new ArrayList<Double>();
        List<Double> list2=new ArrayList<Double>();
        if(res.getCount()!=0){
            list1.clear();
            list2.clear();
            plot.clear();
            while(res.moveToNext())
            {
                list1.add(res.getDouble(0));
                list2.add(res.getDouble(1));
            }
            Number[] xAxis=new Number[list2.size()];
            Number[] yAxis=new Number[list1.size()];
            for(int i=0;i<list2.size();i++){
               xAxis[i]=list2.get(i)*1000;

            }
            for(int j=0;j<list1.size();j++){
                yAxis[j]=list1.get(j);
            }
            XYSeries series;
            series = new SimpleXYSeries( Arrays.asList(xAxis),Arrays.asList(yAxis), "Profil wysokości");
            LineAndPointFormatter series1Format =new LineAndPointFormatter(this, R.xml.line_point_formatter_with_labels);
            series1Format.setInterpolationParams(
                    new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
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

        }
    }

    public void map(View view) {
        Intent inten=new Intent(Weigth.this,eSportMap1.class);
        startActivity(inten);
    }
}
