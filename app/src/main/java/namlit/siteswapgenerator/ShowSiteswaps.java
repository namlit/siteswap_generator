/*
* Siteswap Generator: Android App for generating juggling siteswaps
* Copyright (C) 2017 Tilman Sinning
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package namlit.siteswapgenerator;

import android.app.FragmentManager;
import android.content.Intent;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.ShareActionProvider;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.LinkedList;

import siteswaplib.SiteswapGenerator;
import siteswaplib.Siteswap;

public class ShowSiteswaps extends AppCompatActivity implements SiteswapGenerationFragment.SiteswapGenerationCallbacks {

    private static final String TAG_SITESWAP_GENERATION_TASK_FRAGMENT = "siteswap_generation_task_fragment";
    private SiteswapGenerator mGenerator = null;
    private LinkedList<Siteswap> mSiteswapList = null;
    private SiteswapGenerator.Status mGenerationStatus = SiteswapGenerator.Status.GENERATING;
    private SiteswapGenerationFragment mSiteswapGenerationFragment;

    ListView mSiteswapListView;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_siteswaps);

        setTitle(String.format(getString(R.string.show_siteswaps__title_generating)));

        Intent intent = getIntent();
        if(intent != null) {
            mGenerator = (SiteswapGenerator) intent.getSerializableExtra(getString(R.string.intent__siteswap_generator));
        }

        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);
        mSearchView = (SearchView) findViewById(R.id.search_view);

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

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_siteswap_list, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_item_share)
        {
            Intent shareIntent = createShareIntent();
            Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_via));

            // Check if there are apps available to handle the intent
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooserIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private Intent createShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String siteswapString = "";
        if (mSiteswapList != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int char_counter = 0;
            for (Siteswap siteswap: mSiteswapList)
            {
                stringBuilder.append(siteswap.toString() + "\n");
                char_counter += siteswap.period_length();
                if (char_counter >= 1000) {
                    stringBuilder.append(getString(
                            R.string.show_siteswaps__share_to_many_siteswaps));
                    break;
                }
            }
            siteswapString = stringBuilder.toString();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, siteswapString);
        shareIntent.setType("text/plain");
        return shareIntent;
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
                Siteswap compatible_siteswap = mGenerator.getCompatibleSiteswap();
                if (compatible_siteswap != null) {
                    siteswap = Siteswap.mergeCompatible(compatible_siteswap, siteswap);
                    if (siteswap == null) {
                        siteswap = (Siteswap) parent.getItemAtPosition(position);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), DetailedSiteswapActivity.class);
                intent.putExtra(getString(R.string.intent_detailed_siteswap_view__siteswap), siteswap);
                startActivity(intent);
            }
        });


        switch (mGenerationStatus) {
            case GENERATING:
                setTitle(String.format(getString(R.string.show_siteswaps__title_generating)));
                break;
            case ALL_SITESWAPS_FOUND:
                setTitle(String.format(getString(R.string.show_siteswaps__title_found_all), mSiteswapList.size()));
                break;
            case MAX_RESULTS_REACHED:
                setTitle(String.format(getString(R.string.show_siteswaps__title_limit_reached), mSiteswapList.size()));
                break;
            case TIMEOUT_REACHED:
                setTitle(String.format(getString(R.string.show_siteswaps__title_timeout_reached), mSiteswapList.size()));
                break;
            case MEMORY_FULL:
                setTitle(String.format(getString(R.string.show_siteswaps__title_memory_full), mSiteswapList.size()));
                break;
            case CANCELLED:
                setTitle(String.format(getString(R.string.show_siteswaps__title_cancelled)));
                break;
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.getFilter().filter(null);
                } else {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    public SiteswapGenerator getSiteswapGenerator() {
        return mGenerator;
    }
    public void onGenerationComplete(SiteswapGenerator generator, SiteswapGenerator.Status status) {
        mGenerator = generator;
        mGenerationStatus = status;
        loadSiteswaps();

    }
}
