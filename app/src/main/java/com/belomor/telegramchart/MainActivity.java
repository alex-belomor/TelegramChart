package com.belomor.telegramchart;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.util.ArrayMap;
import android.util.Log;

import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.data.TestChartData;
import com.belomor.telegramchart.view.GraphView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.graph_view)
    GraphView mGraph;

    private ArrayList<ModelChart> chartArray;

    private int maxFollowers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.App_Dark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);



        InputStream is = getResources().openRawResource(R.raw.chart_data);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();

            String jsonString = writer.toString();

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ModelChart>>(){}.getType();
            JSONArray jsonObject = null;
            try {
                jsonObject = new JSONArray(jsonString);
                chartArray = gson.fromJson(jsonObject.toString(), type);
                mGraph.setData(chartArray.get(4), 0);
                Log.d("TEST", "TEST");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("TEST", e.toString());
        }




//        mGraph.setData(chartArray, maxFollowers);
    }
}
