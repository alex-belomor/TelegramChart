package com.belomor.telegramchart;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

    @BindView(R.id.graph_container)
    LinearLayout mContainer;

    @BindView(R.id.parent)
    ScrollView mParent;

    private ModelChart chart;

    private ArrayList<ModelChart> charts;
    private ArrayList<GraphView> chartViews = new ArrayList<>();

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        mParent.setHorizontalScrollBarEnabled(false);

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
                charts = gson.fromJson(jsonObject.toString(), type);
            } catch (JSONException e) {
            }
        } catch (Exception e) {

        }

        for (int i = 0; i < charts.size(); i++) {
            GraphView graphView = new GraphView(this);
            graphView.setData(charts.get(i));
            graphView.setTitle("Chart #" + (i + 1));
            mContainer.addView(graphView);
            ((ViewGroup.MarginLayoutParams) graphView.getLayoutParams()).bottomMargin = BelomorUtil.getDpInPx(18, getApplicationContext());
            graphView.setElevation(BelomorUtil.getDpInPx(1, getApplicationContext()));
            chartViews.add(graphView);
        }

        handleTheme();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    private void handleTheme() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (ThemeManager.nightMode) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_dark)));
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark_dark));
            mParent.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.window_background_dark));
            for (GraphView graphView : chartViews) {
                graphView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_background_dark));
            }
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary_light)));
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark_light));
            mParent.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.window_background_light));
            for (GraphView graphView : chartViews) {
                graphView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.chart_background_light));
            }
        }

        for (GraphView graphView : chartViews) {
            graphView.updateTheme();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.night) {
            ThemeManager.nightMode = !ThemeManager.nightMode;
            handleTheme();
        }
        return super.onOptionsItemSelected(item);
    }
}
