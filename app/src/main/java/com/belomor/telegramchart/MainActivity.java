package com.belomor.telegramchart;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.view.GraphView;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.graph_view)
    GraphView mGraph;

    @BindView(R.id.parent)
    ViewGroup mParent;

    private ArrayList<ModelChart> chartArray;

    private int maxFollowers;

    Unbinder unbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.App_Dark);
        super.onCreate(savedInstanceState);
        if (GlobalManager.view == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null, true);
            GlobalManager.view = view;
        }

        if (GlobalManager.view.getParent() != null) {
            ((ViewGroup) GlobalManager.view.getParent()).removeView(GlobalManager.view); //
        }

        setContentView(GlobalManager.view);


        unbinder = ButterKnife.bind(this);


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
                mGraph.setData(chartArray.get(0), 0);
                Log.d("TEST", "TEST");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("TEST", e.toString());
        }
//        mGraph.setData(chartArray, maxFollowers);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //noinspection SimplifiableIfStatement
        if (id == R.id.night) {
            GlobalManager.nightMode = !GlobalManager.nightMode;
            if (GlobalManager.nightMode) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_dark)));
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark_dark));
                mParent.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.window_background_dark));
                mGraph.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_background_dark));
            } else {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_light)));
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark_light));
                mParent.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.window_background_light));
                mGraph.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_background_light));
            }

            mGraph.updateTheme();
        }

        return super.onOptionsItemSelected(item);
    }
}
