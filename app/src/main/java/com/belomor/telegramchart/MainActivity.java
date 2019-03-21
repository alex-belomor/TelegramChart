package com.belomor.telegramchart;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.belomor.telegramchart.data.ModelChart;
import com.belomor.telegramchart.view.GraphView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.graph_view)
    GraphView mGraph;

    @BindView(R.id.parent)
    ScrollView mParent;

    private ModelChart chart;

    private int maxFollowers;

    Unbinder unbinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);

        mParent.setHorizontalScrollBarEnabled(false);

        this.chart = (ModelChart) getIntent().getSerializableExtra("chart");

        mGraph.setData(chart, 0);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.night) {
            GlobalManager.nightMode = !GlobalManager.nightMode;
            handleTheme();
        }
        return super.onOptionsItemSelected(item);
    }
}
