package namlit.siteswapgenerator;

import android.app.FragmentManager;
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

public class ShowSiteswaps extends AppCompatActivity implements SiteswapGenerationFragment.SiteswapGenerationCallbacks {

    private static final String TAG_SITESWAP_GENERATION_TASK_FRAGMENT = "siteswap_generation_task_fragment";
    private SiteswapGenerator mGenerator = null;
    private LinkedList<Siteswap> mSiteswapList = null;
    private boolean mNoTimeout = true;
    private SiteswapGenerationFragment mSiteswapGenerationFragment;

    ListView mSiteswapListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(final Thread paramThread, final Throwable paramThrowable) {
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//                        Toast.makeText(getApplicationContext(), "Uncaught exeption: " + paramThread.toString() + " " + paramThrowable.toString(), Toast.LENGTH_LONG).show();
//                        Looper.loop();
//                    }
//                }.start();
//                try {
//                    Thread.sleep(4000); // Let the Toast display before app will get shutdown
//                } catch (InterruptedException e) {
//                }
//                System.exit(2);
//            }
//        });
        setContentView(R.layout.activity_show_siteswaps);

        Intent intent = getIntent();
        if(intent != null) {
            mGenerator = (SiteswapGenerator) intent.getSerializableExtra(getString(R.string.intent__siteswap_generator));
        }

        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);

        FragmentManager fm = getFragmentManager();
        mSiteswapGenerationFragment = (SiteswapGenerationFragment) fm.findFragmentByTag(TAG_SITESWAP_GENERATION_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mSiteswapGenerationFragment == null) {
            mSiteswapGenerationFragment = new SiteswapGenerationFragment();
            fm.beginTransaction().add(mSiteswapGenerationFragment, TAG_SITESWAP_GENERATION_TASK_FRAGMENT).commit();
        }
        else {
            mSiteswapGenerationFragment.getSiteswapGenerator();
        }
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

    public SiteswapGenerator getSiteswapGenerator() {
        return mGenerator;
    }
    public void onGenerationComplete(SiteswapGenerator generator, boolean noTimeout) {
        mGenerator = generator;
        mNoTimeout = noTimeout;
        loadSiteswaps();
    }
}
