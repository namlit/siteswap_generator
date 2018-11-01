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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import siteswaplib.Siteswap;

public class FavoritesActivity extends AppCompatActivity {

    ListView mSiteswapListView;
    private List<SiteswapEntity> mFavorites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_siteswaps);

        setTitle(String.format(getString(R.string.favorites__title_waiting_for_database_query)));

       new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                 mFavorites = db.siteswapDao().getAllFavorites();
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                        initializeList();
                     }
                 });
            }
        }).start();



    }

    private void initializeList() {
        setTitle(String.format(getString(R.string.favorites__title)));
        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);
        ArrayAdapter adapter = new ArrayAdapter(
                FavoritesActivity.this, android.R.layout.simple_list_item_1, mFavorites);
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


}
