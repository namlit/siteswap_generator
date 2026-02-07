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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.Serializable;
import java.util.List;

import siteswaplib.Siteswap;

public class FavoritesActivity extends AppCompatActivity {

    private static final String STATE_SITESWAPS = "STATE_SITESWAPS";
    private static final String STATE_FILTER_TYPE = "STATE_FILTER_TYPE";
    private static final String STATE_DATABASE_SEARCH_KEY = "STATE_DATABASE_SEARCH_KEY";
    private static final String STATE_IS_VIEW_SITESWAPS = "STATE_IS_VIEW_SITESWAPS";
    ListView mSiteswapListView;
    private List<SiteswapEntity> mSiteswaps;
    private List<String> mDatabaseColumnStrings;
    private enum FilterType {ALL, JUGGLER_NAME, LOCATION, DATE};
    private FilterType mFilterType;
    private String mDatabaseSearchKey;
    private Boolean mIsViewSiteswaps;

    SearchView mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        if(savedInstanceState != null) {
            mSiteswaps = (List<SiteswapEntity>) savedInstanceState.getSerializable(STATE_SITESWAPS);
            mFilterType = (FilterType) savedInstanceState.getSerializable(STATE_FILTER_TYPE);
            mDatabaseSearchKey = savedInstanceState.getString(STATE_DATABASE_SEARCH_KEY);
            mIsViewSiteswaps = savedInstanceState.getBoolean(STATE_IS_VIEW_SITESWAPS);
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mDatabaseSearchKey  = sharedPref.getString(getString(R.string.favorites__preference_database_search_key),  "");
        mIsViewSiteswaps  = sharedPref.getBoolean(getString(R.string.favorites__preference_is_view_siteswaps),  true);
        try {
            mFilterType = FilterType.valueOf(sharedPref.getString(getString(R.string.favorites__preference_filter_type), ""));
        } catch (Exception ex) {
            mFilterType = FilterType.ALL;
        }

        setContentView(R.layout.activity_show_siteswaps);

        // Set up the toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(String.format(getString(R.string.favorites__title_waiting_for_database_query)));
        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);
        mSearchView = (SearchView) findViewById(R.id.search_view);

        // Handle window insets
        View rootView = findViewById(R.id.show_siteswaps_coordinator);
        View appBarLayout = findViewById(R.id.appBarLayout);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply top inset to AppBarLayout
            ViewCompat.setPaddingRelative(appBarLayout, 0, insets.top, 0, 0);

            // Apply bottom inset to ListView
            mSiteswapListView.setPadding(0, 0, 0, insets.bottom);
            mSiteswapListView.setClipToPadding(false);

            return WindowInsetsCompat.CONSUMED;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_show_all)
        {
            mFilterType = FilterType.ALL;
        }
        else if (id == R.id.action_show_jugglers)
        {
            mFilterType = FilterType.JUGGLER_NAME;
        }
        else if (id == R.id.action_show_locations)
        {
            mFilterType = FilterType.LOCATION;
        }
        else if (id == R.id.action_show_dates)
        {
            mFilterType = FilterType.DATE;
        }
        mIsViewSiteswaps = false;
        loadData();

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_SITESWAPS, (Serializable) mSiteswaps);
        outState.putSerializable(STATE_FILTER_TYPE, mFilterType);
        outState.putString(STATE_DATABASE_SEARCH_KEY, mDatabaseSearchKey);
        outState.putBoolean(STATE_IS_VIEW_SITESWAPS, mIsViewSiteswaps);
    }

    @Override
    protected void onStart () {
        super.onStart();
        loadData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.favorites__preference_filter_type), mFilterType.toString());
        editor.putString(getString(R.string.favorites__preference_database_search_key), mDatabaseSearchKey);
        editor.putBoolean(getString(R.string.favorites__preference_is_view_siteswaps), mIsViewSiteswaps);
        editor.commit();
    }

    private void loadData() {
        if (mIsViewSiteswaps || mFilterType == FilterType.ALL) {
            loadSiteswaps();
        }
        else {
            loadDatabaseColumn();
        }
    }

    private void loadSiteswaps() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                final String title;
                switch (mFilterType) {
                    case ALL:
                        mSiteswaps = db.siteswapDao().getAllFavorites();
                        String.format(getString(R.string.favorites__title_siteswaps));
                        title = String.format(getString(R.string.favorites__title_siteswaps));
                        break;
                    case JUGGLER_NAME:
                        mSiteswaps = db.siteswapDao().getSiteswapsOfJuggler(mDatabaseSearchKey);
                        title = mDatabaseSearchKey;
                        break;
                    case LOCATION:
                        mSiteswaps = db.siteswapDao().getSiteswapsOfLocation(mDatabaseSearchKey);
                        title = mDatabaseSearchKey;
                        break;
                    case DATE:
                        mSiteswaps = db.siteswapDao().getSiteswapsOfDate(mDatabaseSearchKey);
                        title = mDatabaseSearchKey;
                        break;
                    default:
                        title = mDatabaseSearchKey;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSiteswaps(title);
                    }
                });
            }
        }).start();
    }


    private void loadDatabaseColumn() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                final String title;
                switch (mFilterType) {
                    case JUGGLER_NAME:
                        mDatabaseColumnStrings = db.siteswapDao().getJugglers();
                        title = String.format(getString(R.string.favorites__title_jugglers));
                        break;
                    case LOCATION:
                        mDatabaseColumnStrings = db.siteswapDao().getLocations();
                        title = String.format(getString(R.string.favorites__title_locations));
                        break;
                    case DATE:
                        mDatabaseColumnStrings = db.siteswapDao().getDates();
                        title = String.format(getString(R.string.favorites__title_dates));
                        break;
                    default:
                        title = "";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDatabaseColumn(title);
                    }
                });
            }
        }).start();

    }


    private void setSiteswaps(String title) {
        setTitle(title);
        ArrayAdapter adapter = new ArrayAdapter(
                FavoritesActivity.this, android.R.layout.simple_list_item_1, mSiteswaps);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SiteswapEntity siteswapEntity = ((SiteswapEntity) parent.getItemAtPosition(position));
                Siteswap siteswap = siteswapEntity.toSiteswap();
                Intent intent = new Intent(getApplicationContext(), DetailedSiteswapActivity.class);
                intent.putExtra(getString(R.string.intent_detailed_siteswap_view__siteswap), siteswap);
                intent.putExtra(getString(R.string.intent_detailed_siteswap_view__skip_rotation_to_starting_position), true);
                startActivity(intent);
            }
        });

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

    private void setDatabaseColumn(String title) {
        setTitle(title);
        ArrayAdapter adapter = new ArrayAdapter(
                FavoritesActivity.this, android.R.layout.simple_list_item_1, mDatabaseColumnStrings);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDatabaseSearchKey = ((String) parent.getItemAtPosition(position));
                mIsViewSiteswaps = true;
                loadSiteswaps();
            }
        });
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
}
