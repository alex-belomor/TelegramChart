package com.belomor.telegramchart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.belomor.telegramchart.adapter.ItemAdapter;
import com.belomor.telegramchart.data.ModelChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class StartActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {

    @BindView(R.id.charts_list)
    RecyclerView mChartsList;

    Unbinder unbinder;

    ArrayList<ModelChart> chartArray;

    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        unbinder = ButterKnife.bind(this);

        mChartsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mChartsList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));

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
            Type type = new TypeToken<ArrayList<ModelChart>>() {
            }.getType();
            JSONArray jsonObject = null;
            try {
                jsonObject = new JSONArray(jsonString);
                chartArray = gson.fromJson(jsonObject.toString(), type);
                itemAdapter = new ItemAdapter(chartArray,StartActivity.this);
                mChartsList.setAdapter(itemAdapter);
                Log.d("TEST", "TEST");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("TEST", e.toString());
        }

    }

    @Override
    public void onClick(int pos) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("chart", chartArray.get(pos));
        startActivity(intent);
    }
}
