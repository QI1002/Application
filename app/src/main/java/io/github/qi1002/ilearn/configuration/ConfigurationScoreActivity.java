package io.github.qi1002.ilearn.configuration;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.qi1002.ilearn.R;

// use https://github.com/PhilJay/MPAndroidChart 3.0.1 as android chart libraries
public class ConfigurationScoreActivity extends AppCompatActivity {

    private int MAX_MONTH_COUNT = 5;

    class ScoreXAxisValueFormatter implements IAxisValueFormatter {

        private List<String> mValues;

        public ScoreXAxisValueFormatter(List<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues.get((int)value);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_score);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.config_score));

        BarChart chart_bar = (BarChart)findViewById(R.id.chart);
        chart_bar.setData(getBarData());
        configChartAxis(chart_bar);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;

        float dpX = Utils.convertPixelsToDp(0);
        float dpY = Utils.convertPixelsToDp(height) - 160;

        Description desc = new Description();
        desc.setText("The score history in recent 5 months");
        //desc.setText("The score history \n" + width + " " + height + " " + dpX + " " + dpY);
        desc.setTextSize(20);
        desc.setYOffset(dpY);
        desc.setXOffset(dpX);
        chart_bar.setDescription(desc);
        chart_bar.animateY(5000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BarData getBarData(){
        BarDataSet dataSet = new BarDataSet(getChartData(), "");

        dataSet.setColors(getChartColors());
        dataSet.setStackLabels(getStackLabels());

        return new BarData(dataSet);
    }

    private String[] getStackLabels(){
        return new String[]{
                "Score<=25",
                "50>=Score>25 ",
                "75>=Score>50",
                "Score>=75"};
    }

    public int getColorWrapper(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(id);
        } else {
            //noinspection deprecation
            return getResources().getColor(id);
        }
    }

    private int[] getChartColors() {

        int[] colors = new int[]{
                getColorWrapper(R.color.chart_color_0_25),
                getColorWrapper(R.color.chart_color_25_50),
                getColorWrapper(R.color.chart_color_50_75),
                getColorWrapper(R.color.chart_color_75_100)};
        return colors;
    }

    private List<BarEntry> getChartData(){
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0;i<MAX_MONTH_COUNT;i++){
            float count_75_100 = (i+1)*1;
            float count_50_75 = (i+1)*2;
            float count_25_50 = (i+1)*3;
            float count_0_25 = (i+1)*4;
            chartData.add(new BarEntry(i,new float[]{count_0_25, count_25_50, count_50_75, count_75_100}));
        }
        return chartData;
    }

    private List<String> getLabels(){

        // get current month index
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] monthNames = symbols.getMonths();
        List<String> chartLabels = new ArrayList<>();
        for(int i=0;i<MAX_MONTH_COUNT;i++){
            int monthIndex =  (13 - MAX_MONTH_COUNT + month + i) % 12;
            String yearString = (monthIndex > month) ? Integer.toString(year - 1) : Integer.toString(year);
            chartLabels.add(yearString + " " + monthNames[monthIndex]);
        }
        return chartLabels;
    }

    private void configChartAxis(BarChart chart_bar){
        XAxis xAxis = chart_bar.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ScoreXAxisValueFormatter(getLabels()));

        YAxis leftYAxis = chart_bar.getAxisLeft();
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setDrawGridLines(false);

        YAxis RightYAxis = chart_bar.getAxisRight();
        RightYAxis.setAxisMinimum(0);
        RightYAxis.setEnabled(false); // not show the right side axis
    }
}
