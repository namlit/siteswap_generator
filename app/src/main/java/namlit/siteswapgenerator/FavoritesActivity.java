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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import siteswaplib.Siteswap;

public class FavoritesActivity extends AppCompatActivity {

    ListView mSiteswapListView;
    private List<SiteswapEntity> mSiteswaps;
    private List<String> mJugglers;
    private List<String> mLocations;
    private List<String> mDates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_siteswaps);

        setTitle(String.format(getString(R.string.favorites__title_waiting_for_database_query)));
        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);
        loadAllFavorites();

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
            loadAllFavorites();
        }
        else if (id == R.id.action_show_jugglers)
        {
            loadJugglers();
        }
        else if (id == R.id.action_show_locations)
        {
            loadLocations();
        }
        else if (id == R.id.action_show_dates)
        {
            loadDates();
        }

        return super.onOptionsItemSelected(item);
    }

    void loadAllFavorites() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mSiteswaps = db.siteswapDao().getAllFavorites();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSiteswaps();
                    }
                });
            }
        }).start();
    }

    void loadJugglers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mJugglers = db.siteswapDao().getJugglers();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setJugglers();
                    }
                });
            }
        }).start();
    }

    void loadLocations() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mLocations = db.siteswapDao().getLocations();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLocations();
                    }
                });
            }
        }).start();
    }

    void loadDates() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mDates = db.siteswapDao().getDates();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDates();
                    }
                });
            }
        }).start();
    }


    void loadByJugglers(final String jugglers) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mSiteswaps = db.siteswapDao().getSiteswapsOfJuggler(jugglers);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSiteswaps();
                    }
                });
            }
        }).start();
    }

    void loadByLocation(final String location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mSiteswaps = db.siteswapDao().getSiteswapsOfLocation(location);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSiteswaps();
                    }
                });
            }
        }).start();
    }

    void loadByDate(final String date) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                mSiteswaps = db.siteswapDao().getSiteswapsOfDate(date);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSiteswaps();
                    }
                });
            }
        }).start();
    }

    private void setSiteswaps() {
        setTitle(String.format(getString(R.string.favorites__title_siteswaps)));
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
                startActivity(intent);
            }
        });
    }

    private void setJugglers() {
        setTitle(String.format(getString(R.string.favorites__title_jugglers)));
        ArrayAdapter adapter = new ArrayAdapter(
                FavoritesActivity.this, android.R.layout.simple_list_item_1, mJugglers);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String jugglers = ((String) parent.getItemAtPosition(position));
                loadByJugglers(jugglers);
            }
        });
    }

    private void setLocations() {
        setTitle(String.format(getString(R.string.favorites__title_locations)));
        ArrayAdapter adapter = new ArrayAdapter(
                FavoritesActivity.this, android.R.layout.simple_list_item_1, mLocations);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String locations = ((String) parent.getItemAtPosition(position));
                loadByLocation(locations);
            }
        });
    }

    private void setDates() {
        setTitle(String.format(getString(R.string.favorites__title_dates)));
        ArrayAdapter adapter = new ArrayAdapter(
                FavoritesActivity.this, android.R.layout.simple_list_item_1, mDates);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = ((String) parent.getItemAtPosition(position));
                loadByDate(date);
            }
        });
    }


}
