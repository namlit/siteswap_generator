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
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

import siteswaplib.NamedSiteswap;
import siteswaplib.NamedSiteswaps;
import siteswaplib.Siteswap;

public class NamedSiteswapActivity extends AppCompatActivity {

    static final private List<NamedSiteswap> mSiteswapList = NamedSiteswaps.getListOfNamedSiteswaps();

    SearchView mSearchView;
    ListView mSiteswapListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_siteswaps);

        setTitle(String.format(getString(R.string.named_siteswaps__title)));

        mSiteswapListView = (ListView) findViewById(R.id.siteswap_list);
        mSearchView = (SearchView) findViewById(R.id.search_view);

        SiteswapArrayAdapter adapter = new SiteswapArrayAdapter(
                NamedSiteswapActivity.this, android.R.layout.simple_list_item_1, mSiteswapList);
        mSiteswapListView.setAdapter(adapter);
        mSiteswapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Siteswap siteswap = ((Siteswap) parent.getItemAtPosition(position));
                Intent intent = new Intent(getApplicationContext(), DetailedSiteswapActivity.class);
                intent.putExtra(getString(R.string.intent_detailed_siteswap_view__siteswap), siteswap);
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

}
