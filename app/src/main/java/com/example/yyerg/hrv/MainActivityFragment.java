package com.example.yyerg.hrv;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private LinearLayout llBarChart;
    private View vChart;

    private ArrayList<Integer> RR;
    private Random rand;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        RR = new ArrayList<Integer>();
        RR.add(10);
        RR.add(11);
        RR.add(12);
                llBarChart = (LinearLayout) rootView.findViewById(R.id.llBarChart);
        rand = new Random();
        try{
            vChart = getBarChart("RR Interval", "ErrCode", "QTY", RR);
            llBarChart.removeAllViews();
            //llBarChart.addView(vChart);
            llBarChart.addView(vChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
        }catch(Exception e){

        }
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        final ScheduledFuture drawHandle =
                scheduleTaskExecutor.scheduleAtFixedRate(drawThread, 5, 5, TimeUnit.SECONDS);
        return rootView;
    }



    private Runnable drawThread = new Runnable() {
        public void run() {
            int i = rand.nextInt(1000 - 700) + 700;
            RR.add(i);
            getBarChart("RR Interval", "ErrCode", "QTY", RR);
        }
    };

    private View getBarChart(String chartTitle, String XTitle, String YTitle, ArrayList<Integer> rr){

        XYSeries Series = new XYSeries(YTitle);

        XYMultipleSeriesDataset Dataset = new XYMultipleSeriesDataset();
        Dataset.addSeries(Series);

        XYMultipleSeriesRenderer Renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer yRenderer = new XYSeriesRenderer();
        Renderer.addSeriesRenderer(yRenderer);

        //Renderer.setApplyBackgroundColor(true);			//設定背景顏色
        //Renderer.setBackgroundColor(Color.BLACK);			//設定圖內圍背景顏色
        Renderer.setMarginsColor(Color.WHITE);				//設定圖外圍背景顏色
        Renderer.setTextTypeface(null, Typeface.BOLD);		//設定文字style

        Renderer.setShowGrid(true);							//設定網格
        Renderer.setGridColor(Color.GRAY);					//設定網格顏色

        Renderer.setChartTitle(chartTitle);					//設定標頭文字
        Renderer.setLabelsColor(Color.BLACK);				//設定標頭文字顏色
        Renderer.setChartTitleTextSize(20);					//設定標頭文字大小
        Renderer.setAxesColor(Color.BLACK);				//設定雙軸顏色
        Renderer.setBarSpacing(0.5);						//設定bar間的距離

        //Renderer.setXTitle(XTitle);						//設定X軸文字
        //Renderer.setYTitle(YTitle);						//設定Y軸文字
        Renderer.setXLabelsColor(Color.BLACK);				//設定X軸文字顏色
        Renderer.setYLabelsColor(0, Color.BLACK);			//設定Y軸文字顏色
        Renderer.setXLabelsAlign(Align.CENTER);			//設定X軸文字置中
        Renderer.setYLabelsAlign(Paint.Align.CENTER);		//設定Y軸文字置中
        Renderer.setXLabelsAngle(-25); 						//設定X軸文字傾斜度

        Renderer.setXLabels(0); 							//設定X軸不顯示數字, 改以程式設定文字
        Renderer.setYAxisMin(0);							//設定Y軸文最小值

        yRenderer.setColor(Color.RED);              		//設定Series顏色
        //yRenderer.setDisplayChartValues(true);			//展現Series數值

        Series.add(0, 0);
        Renderer.addXTextLabel(0, "");
        for(int r=0; r<rr.size(); r++) {
            //Log.i("DEBUG", (r+1)+" "+xy[r][0]+"; "+xy[r][1]);
            Renderer.addXTextLabel(r+1, rr.get(r).toString());
            Series.add(r+1, rr.get(r));
        }

        View view = ChartFactory.getBarChartView(getActivity(), Dataset, Renderer, Type.DEFAULT);
        return view;
    }
}
