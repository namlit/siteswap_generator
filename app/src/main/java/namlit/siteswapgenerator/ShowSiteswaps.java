package namlit.siteswapgenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;

import siteswaplib.SiteswapGenerator;
import siteswaplib.Siteswap;

public class ShowSiteswaps extends AppCompatActivity {

    private SiteswapGenerator mGenerator = null;
    private LinkedList<Siteswap> mSiteswapList = null;
    private boolean mNoTimeout = true;

    ListView mSiteswapListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_siteswaps);

        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);

        if (savedInstanceState != null) {

            mNoTimeout = savedInstanceState.getBoolean(getString(R.string.show_siteswaps__saved_is_no_timeout));
            mGenerator = (SiteswapGenerator) savedInstanceState.getSerializable(
                    getString(R.string.show_siteswaps__saved_siteswap_generator));
            mSiteswapList = mGenerator.getSiteswaps();

        }
        else {

            Intent intent = getIntent();

//            int numberOfObjects  = intent.getIntExtra(getString(R.string.intent__siteswap_number_of_objects),   0);
//            int periodLength     = intent.getIntExtra(getString(R.string.intent__siteswap_period_length),       0);
//            int maxThrow         = intent.getIntExtra(getString(R.string.intent__siteswap_max_throw),           0);
//            int minThrow         = intent.getIntExtra(getString(R.string.intent__siteswap_min_throw),           0);
//            int numberOfJugglers = intent.getIntExtra(getString(R.string.intent__siteswap_number_of_jugglers),  0);
//            int maxResults       = intent.getIntExtra(getString(R.string.intent__siteswap_max_results),         0);
//            int timeout          = intent.getIntExtra(getString(R.string.intent__siteswap_calculation_timeout), 0);
//            ArrayList<Filter> filterList = (ArrayList<Filter>) intent.getSerializableExtra(getString(R.string.intent__siteswap_filter_list));
            //LinkedList<Filter> filterList = new LinkedList<Filter>();

            mGenerator = (SiteswapGenerator) intent.getSerializableExtra(getString(R.string.intent__siteswap_generator));
            //mGenerator.setFilterList(filterList);
        }

        if (mGenerator.isCalculationComplete()) {
            loadSiteswaps();
        }
        else {
            generateSiteswaps();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putBoolean(getString(R.string.show_siteswaps__saved_is_no_timeout), mNoTimeout);
        out.putSerializable(getString(R.string.show_siteswaps__saved_siteswap_generator), mGenerator);
        super.onSaveInstanceState(out);
    }

    private void loadSiteswaps() {
        mSiteswapList = mGenerator.getSiteswaps();
        ArrayAdapter adapter = new ArrayAdapter<Siteswap>(
                ShowSiteswaps.this, android.R.layout.simple_list_item_1, mSiteswapList);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Siteswap siteswap = (Siteswap) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), DetailedSiteswapActivity.class);
                intent.putExtra(getString(R.string.intent_detailed_siteswap_view__siteswap), siteswap);
                startActivity(intent);
            }
        });


        if (mNoTimeout || mSiteswapList.size() == mGenerator.getMaxResults())
            setTitle(String.format(getString(R.string.show_siteswaps__title), mSiteswapList.size()));
        else
            setTitle(String.format(getString(R.string.show_siteswaps__title_timeout), mSiteswapList.size()));
    }

    private void generateSiteswaps() {

        setTitle(getString(R.string.show_siteswaps__title_loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                mNoTimeout = mGenerator.generateSiteswaps();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadSiteswaps();
                    }
                });
            }
        }).start();

    }
}
