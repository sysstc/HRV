package com.example.yyerg.hrv;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivityFragment extends Fragment {
    private LinearLayout llBarChart;
    private View vRRChart;
    private View vFTChart;

    private FFTManager m_FTManager;

    private ArrayList<Integer> m_RR;
    private ArrayList<Integer> m_FT;
    private Random rand;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int i;
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        m_RR = new ArrayList<Integer>();
        for(i=0;i<30;i++){
            m_RR.add(0);
        }
        m_FTManager = new FFTManager();
        m_FT = m_FTManager.TraditionalFT(m_RR);

        llBarChart = (LinearLayout) rootView.findViewById(R.id.llBarChart);
        rand = new Random();
        try{
            vRRChart = getBarChart(m_RR);
            vFTChart = getBarChart(m_FT);
            llBarChart.removeAllViews();
            llBarChart.addView(vRRChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
            llBarChart.addView(vFTChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
        }catch(Exception e){

        }
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        final ScheduledFuture drawHandle =
                scheduleTaskExecutor.scheduleAtFixedRate(drawThread, 1, 1, TimeUnit.SECONDS);

        return rootView;

    }

    private Runnable drawThread = new Runnable() {
        public void run() {
            int i = rand.nextInt(1000 - 700) + 700;
            Log.d(MainActivity.APP_TAG, "add: " + Integer.toString(i));
            if(m_RR.size()==30) {
                m_RR.remove(0);
            }
            m_RR.add(i);
            m_FT = m_FTManager.TraditionalFT(m_RR);
            try{
                Log.d(MainActivity.APP_TAG, "getBarChart");
            }catch(Exception e){

            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vRRChart = getBarChart(m_RR);
                    vFTChart = getBarChart(m_FT);
                    llBarChart.removeAllViews();
                    llBarChart.addView(vRRChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
                    llBarChart.addView(vFTChart, new LayoutParams(LayoutParams.WRAP_CONTENT, 300));
                }
            });
        }
    };

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
                                    int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    public XYMultipleSeriesDataset buildBarDataset(String title, ArrayList<Integer> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        CategorySeries series = new CategorySeries(title);
        int seriesLength = values.size();
        Log.d(MainActivity.APP_TAG, "length: " + Integer.toString(values.size()));
        for (int k = 0; k < seriesLength; k++) {
            series.add(values.get(k));
        }
        dataset.addSeries(series.toXYSeries());
        return dataset;
    }

    private View getBarChart(ArrayList<Integer> RR){
        String title = "m_RR";
        int[] barColors = new int[] { Color.CYAN};

        XYMultipleSeriesRenderer renderer = buildBarRenderer(barColors);
        renderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);

        setChartSettings(renderer, "m_RR", "time", "m_RR interval", 0.5,
                30.5, 0, 1000, Color.GRAY, Color.RED);
        renderer.setXLabels(1);
        renderer.setYLabels(10);
        XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
        seriesRenderer.setDisplayChartValues(true);
        return ChartFactory.getBarChartView(this.getActivity(), buildBarDataset(title, RR), renderer,
                Type.DEFAULT);
    }
}
